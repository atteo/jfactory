package org.atteo.jfactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.google.common.collect.Sets;
import com.google.gerrit.common.TimeUtil;
import com.google.gerrit.common.data.GroupDescriptions;
import com.google.gerrit.reviewdb.client.*;
import com.google.gerrit.server.account.AccountByEmailCache;
import com.google.gerrit.server.account.AccountCache;
import com.google.gerrit.server.account.AccountLoader;
import com.google.gerrit.server.account.VersionedAuthorizedKeys;
import com.google.gerrit.server.group.GroupsCollection;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.gerrit.common.errors.InvalidSshKeyException;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.events.NewProjectCreatedListener;
import com.google.gerrit.extensions.restapi.UnprocessableEntityException;
import com.google.gerrit.reviewdb.server.ReviewDb;
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
    private final VersionedAuthorizedKeys.Accessor authorizedKeys;
	private final AccountCache accountCache;
	private final AccountByEmailCache byEmailCache;
	private final AccountLoader.Factory infoLoader;
	private final GroupsCollection groupsCollection;
	private final String username;
	private final String name;
	private final String sshKey;

	@Inject
	public CreateBatchUser(@PluginName String pluginName, SchemaFactory<ReviewDb> schema,
			PluginConfigFactory configFactory, SshKeyCache sshKeyCache, VersionedAuthorizedKeys.Accessor authorizedKeys,
        	AccountCache accountCache, AccountByEmailCache byEmailCache, AccountLoader.Factory infoLoader,
			GroupsCollection groupsCollection) {
		this.schema = schema;
		this.sshKeyCache = sshKeyCache;
        this.authorizedKeys = authorizedKeys;
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
				accountExternalId = createBatchUser(db);
				logger.info("Batch user account created");
			}

			authorizedKeys.addKey(accountExternalId.getAccountId(), sshKey);
			sshKeyCache.evict(username);
		} catch (OrmException|IOException|ConfigInvalidException|InvalidSshKeyException e) {
			throw new RuntimeException(e);
		}
	}

	private AccountExternalId createBatchUser(ReviewDb db) {
		logger.info("Creating new batch user");

		try {
			return createNewUser(db);
		} catch (OrmException | UnprocessableEntityException e) {
			throw new RuntimeException(e);
		}
	}

	private AccountExternalId createNewUser(ReviewDb db) throws OrmException, UnprocessableEntityException {
		Account.Id id = new Account.Id(db.nextAccountId());
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

		Set<AccountGroup.Id> groups = parseGroups(Lists.newArrayList("2"));

		for (AccountGroup.Id groupId : groups) {
			AccountGroupMember m =
					new AccountGroupMember(new AccountGroupMember.Key(id, groupId));
			db.accountGroupMembers().insert(Collections.singleton(m));
		}

		accountCache.evictByUsername(username);

		return extUser;
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
