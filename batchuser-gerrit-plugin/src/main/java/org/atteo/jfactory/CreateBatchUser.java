package org.atteo.jfactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gerrit.common.TimeUtil;
import com.google.gerrit.common.data.GroupDescriptions;
import com.google.gerrit.common.errors.InvalidSshKeyException;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.restapi.UnprocessableEntityException;
import com.google.gerrit.reviewdb.client.Account;
import com.google.gerrit.reviewdb.client.AccountExternalId;
import com.google.gerrit.reviewdb.client.AccountGroup;
import com.google.gerrit.reviewdb.client.AccountGroupMember;
import com.google.gerrit.reviewdb.server.ReviewDb;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.IdentifiedUser;
import com.google.gerrit.server.account.AccountByEmailCache;
import com.google.gerrit.server.account.AccountCache;
import com.google.gerrit.server.account.AccountLoader;
import com.google.gerrit.server.account.VersionedAuthorizedKeys;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.git.WorkQueue;
import com.google.gerrit.server.group.GroupsCollection;
import com.google.gerrit.server.ssh.SshKeyCache;
import com.google.gerrit.server.util.RequestContext;
import com.google.gerrit.server.util.ThreadLocalRequestContext;
import com.google.gwtorm.server.OrmException;
import com.google.gwtorm.server.SchemaFactory;
import com.google.inject.Provider;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class CreateBatchUser {
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
	private final WorkQueue.Executor executor;
	private final ThreadLocalRequestContext context;
	private final IdentifiedUser.GenericFactory userFactory;

	@Inject
	public CreateBatchUser(@PluginName String pluginName, SchemaFactory<ReviewDb> schema,
						   PluginConfigFactory configFactory, SshKeyCache sshKeyCache, VersionedAuthorizedKeys.Accessor authorizedKeys,
						   AccountCache accountCache, AccountByEmailCache byEmailCache, AccountLoader.Factory infoLoader,
						   GroupsCollection groupsCollection, WorkQueue queue, ThreadLocalRequestContext context,
						   IdentifiedUser.GenericFactory userFactory) {
		this.schema = schema;
		this.sshKeyCache = sshKeyCache;
		this.authorizedKeys = authorizedKeys;
		this.accountCache = accountCache;
		this.byEmailCache = byEmailCache;
		this.infoLoader = infoLoader;
		this.groupsCollection = groupsCollection;
		this.context = context;
		this.userFactory = userFactory;
		pluginConfig = configFactory.getFromGerritConfig(pluginName);
		username = pluginConfig.getString("username", "jenkins");
		sshKey = pluginConfig.getString("sshKey");
		name = pluginConfig.getString("name", "Batch user");
		executor = queue.getDefaultQueue();

		createBatchUserIfNotExistsYet();
	}

	private void createBatchUserIfNotExistsYet() {
		try (ReviewDb db = schema.open()) {
			List<Account> accounts = db.accounts().anyAccounts().toList();
			if (accounts.isEmpty()) {
				logger.info("Cannot create batch user account. No admin user yet. Log in as admin user to Gerrit.");
				executor.schedule(this::createBatchUserIfNotExistsYet, 2, TimeUnit.SECONDS);
				return;
			}

			RequestContext oldContext = null;

			try {
				oldContext = context.setContext(new RequestContext() {
					@Override
					public CurrentUser getUser() {
						return userFactory.create(accounts.iterator().next().getId());
					}

					@Override
					public Provider<ReviewDb> getReviewDbProvider() {
						return () -> db;
					}
				});

				AccountExternalId.Key accountKey = new AccountExternalId.Key(AccountExternalId.SCHEME_USERNAME, username);
				AccountExternalId accountExternalId = db.accountExternalIds().get(accountKey);

				if (accountExternalId == null) {
					accountExternalId = createBatchUser(db);
					logger.info("Batch user account created");
				}

				authorizedKeys.addKey(accountExternalId.getAccountId(), sshKey);
				sshKeyCache.evict(username);
			} finally {
				if (oldContext != null) {
					context.setContext(oldContext);
				}
			}
		} catch (OrmException | IOException | ConfigInvalidException | InvalidSshKeyException e) {
			throw new RuntimeException(e);
		}
	}

	private AccountExternalId createBatchUser(ReviewDb db) {
		logger.info("Creating new batch user: " + name);

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
