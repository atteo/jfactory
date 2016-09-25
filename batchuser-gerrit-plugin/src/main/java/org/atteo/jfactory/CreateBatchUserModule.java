package org.atteo.jfactory;

import com.google.gerrit.extensions.events.NewProjectCreatedListener;
import com.google.gerrit.extensions.registration.DynamicSet;
import com.google.inject.AbstractModule;

public class CreateBatchUserModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(CreateBatchUser.class).asEagerSingleton();
		DynamicSet.bind(binder(), NewProjectCreatedListener.class)
				.to(CreateBatchUser.class);
	}
}
