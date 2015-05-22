package persistence.configuration;

import persistence.dao.ConfigurationDao;
import persistence.dao.UserDao;
import persistence.dao.impl.ConfigurationDaoImpl;
import persistence.dao.impl.UserDaoImpl;
import persistence.services.ConfigurationPersistenceService;
import persistence.services.UserPersistenceService;
import persistence.services.impl.ConfigurationPersistenceServiceImpl;
import persistence.services.impl.UserPersistenceServiceImpl;
import play.mvc.Security;

import com.google.inject.AbstractModule;

import controllers.security.Secured;

public class BaseModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(UserDao.class).to(UserDaoImpl.class);
		bind(ConfigurationDao.class).to(ConfigurationDaoImpl.class);
		bind(UserPersistenceService.class).to(UserPersistenceServiceImpl.class);
		bind(ConfigurationPersistenceService.class).to(ConfigurationPersistenceServiceImpl.class);
		bind(Security.Authenticator.class).to(Secured.class);
	}

}
