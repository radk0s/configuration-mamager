package persistence.configuration;

import play.Application;
import play.GlobalSettings;
import play.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Global extends GlobalSettings {

	private Injector injector;

	@Override
	public void onStart(Application application) {
		injector = Guice.createInjector(new BaseModule());
	}

	@Override
	public <T> T getControllerInstance(Class<T> clazz) throws Exception {
		return injector.getInstance(clazz);
	}
}
