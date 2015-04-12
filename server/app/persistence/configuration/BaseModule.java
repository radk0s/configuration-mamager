package persistence.configuration;

import com.google.inject.TypeLiteral;
import controllers.ssControllers.LocalRuntimeEnvironment;
import controllers.ssControllers.LocalSecured;
import persistence.model.User;

import com.google.inject.AbstractModule;
import securesocial.core.RuntimeEnvironment;
import securesocial.core.java.Secured;

public class BaseModule extends AbstractModule{

	@Override
	protected void configure() {
		bind(new TypeLiteral<RuntimeEnvironment<User>>(){}).to(LocalRuntimeEnvironment.class);
		bind(Secured.class).to(LocalSecured.class);
	}

}
