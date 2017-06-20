package org.atteo.jfactory;

import com.google.inject.AbstractModule;

public class CreateBatchUserModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(CreateBatchUser.class).asEagerSingleton();
	}
}
