package org.atteo.jfactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.google.common.collect.Sets;
import com.google.gerrit.common.TimeUtil;
import com.google.gerrit.common.data.GroupDescriptions;
import com.google.gerrit.extensions.common.AccountInfo;
import com.google.gerrit.reviewdb.client.*;
import com.google.gerrit.server.account.AccountByEmailCache;
import com.google.gerrit.server.account.AccountCache;
import com.google.gerrit.server.account.AccountLoader;
import com.google.gerrit.server.group.GroupsCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.gerrit.common.errors.InvalidSshKeyException;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.events.NewProjectCreatedListener;
import com.google.gerrit.extensions.restapi.BadRequestException;
import com.google.gerrit.extensions.restapi.ResourceConflictException;
import com.google.gerrit.extensions.restapi.TopLevelResource;
import com.google.gerrit.extensions.restapi.UnprocessableEntityException;
import com.google.gerrit.reviewdb.server.ReviewDb;
import com.google.gerrit.server.account.CreateAccount;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.ssh.SshKeyCache;
import com.google.gwtorm.server.OrmException;
import com.google.gwtorm.server.SchemaFactory;

public class CreateBatchUser implements NewProjectCreatedListener {
	private final Logger logger = LoggerFactory.getLogger(CreateBatchUser.class);
	private final SchemaFactory<ReviewDb> schema;
	private final PluginConfig pluginConfig;
	private final SshKeyCache sshKeyCache;
	private final AccountCache accountCache;
	private final AccountByEmailCache byEmailCache;
	private final AccountLoader.Factory infoLoader;
	private final GroupsCollection groupsCollection;
	private final String username;
	private final String name;
	private final String sshKey;

	@Inject
	public CreateBatchUser(@PluginName String pluginName, SchemaFactory<ReviewDb> schema,
			PluginConfigFactory configFactory, SshKeyCache sshKeyCache, AccountCache accountCache,
            AccountByEmailCache byEmailCache, AccountLoader.Factory infoLoader, GroupsCollection groupsCollection) {
		this.schema = schema;
		this.sshKeyCache = sshKeyCache;
		this.accountCache = accountCache;
		this.byEmailCache = byEmailCache;
		this.infoLoader = infoLoader;
		this.groupsCollection = groupsCollection;
		pluginConfig = configFactory.getFromGerritConfig(pluginName);
		username = pluginConfig.getString("username", "jenkins");
		sshKey = pluginConfig.getString("sshKey");
		name = pluginConfig.getString("name", "Batch user");

		createBatchUserIfNotExistsYet();
	}

	@Override
	public void onNewProjectCreated(Event event) {
		createBatchUserIfNotExistsYet();
	}

	private void createBatchUserIfNotExistsYet() {
		try (ReviewDb db = schema.open()) {
			if (db.accounts().anyAccounts().toList().isEmpty()) {
				logger.info("Cannot create batch user account as an admin account, create admin user first");
				return;
			}

			AccountExternalId.Key accountKey = new AccountExternalId.Key(AccountExternalId.SCHEME_USERNAME, username);
			AccountExternalId accountExternalId = db.accountExternalIds().get(accountKey);

			if (accountExternalId == null) {
				createBatchUser(db);
			} else {
				updateSshKey(accountExternalId.getAccountId(), db);
			}

		} catch (OrmException e) {
			throw new RuntimeException(e);
		}
	}

	public void updateSshKey(Account.Id accountId, final ReviewDb db) throws OrmException {
		logger.info("Updating SSH key");
		db.accountSshKeys().delete(db.accountSshKeys().byAccount(accountId));
		AccountSshKey accountSshKey = createSshKey(accountId, sshKey);
		db.accountSshKeys().insert(Collections.singleton(accountSshKey));
	}

	private void createBatchUser(ReviewDb db) {
		logger.info("Creating new batch user");

		try {
			createNewUser(db);
		} catch (OrmException | UnprocessableEntityException e) {
			throw new RuntimeException(e);
		}

		logger.info("Batch user account created");
	}

	private void createNewUser(ReviewDb db) throws OrmException, UnprocessableEntityException {
		Account.Id id = new Account.Id(db.nextAccountId());
		AccountSshKey sshKey = createSshKey(id, this.sshKey);
		AccountExternalId extUser =
				new AccountExternalId(id, new AccountExternalId.Key(
						AccountExternalId.SCHEME_USERNAME, username));

		/*
		if (input.httpPassword != null) {
			extUser.setPassword(input.httpPassword);
		}
		*/
		db.accountExternalIds().insert(Collections.singleton(extUser));
		Account a = new Account(id, TimeUtil.nowTs());
		a.setFullName(name);
		//a.setPreferredEmail(input.email);
		db.accounts().insert(Collections.singleton(a));

		if (sshKey != null) {
			db.accountSshKeys().insert(Collections.singleton(sshKey));
		}

		Set<AccountGroup.Id> groups = parseGroups(Lists.newArrayList("2"));

		for (AccountGroup.Id groupId : groups) {
			AccountGroupMember m =
					new AccountGroupMember(new AccountGroupMember.Key(id, groupId));
			db.accountGroupMembers().insert(Collections.singleton(m));
		}

		sshKeyCache.evict(username);
		accountCache.evictByUsername(username);
		//byEmailCache.evict(email);

		/*
		AccountLoader loader = infoLoader.create(true);
		AccountInfo info = loader.get(id);
		loader.fill();
		*/
	}

	private AccountSshKey createSshKey(Account.Id id, String sshKey) {
		if (sshKey == null) {
			return null;
		}
		try {
			return sshKeyCache.create(new AccountSshKey.Id(id, 1), sshKey.trim());
		} catch (InvalidSshKeyException e) {
			throw new RuntimeException(e);
		}
	}

	private Set<AccountGroup.Id> parseGroups(List<String> groups)
			throws UnprocessableEntityException {
		Set<AccountGroup.Id> groupIds = Sets.newHashSet();
		if (groups != null) {
			for (String g : groups) {
				groupIds.add(GroupDescriptions.toAccountGroup(
						groupsCollection.parseInternal(g)).getId());
			}
		}
		return groupIds;
	}
}
