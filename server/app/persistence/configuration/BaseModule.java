package persistence.configuration;

import persistence.dao.UserDao;
import persistence.dao.impl.UserDaoImpl;
import persistence.services.UserPersistenceService;
import persistence.services.impl.UserPersistenceServiceImpl;
import play.mvc.Security;

import com.google.inject.AbstractModule;

import controllers.security.Secured;

public class BaseModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(UserDao.class).to(UserDaoImpl.class);
		bind(UserPersistenceService.class).to(UserPersistenceServiceImpl.class);
		bind(Security.Authenticator.class).to(Secured.class);
	}

}
