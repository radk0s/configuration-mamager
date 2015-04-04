package persistence.configuration;

import persistence.dao.UserDao;
import persistence.dao.impl.UserDaoImpl;
import persistence.services.UserPersistenceService;
import persistence.services.impl.UserPersistenceServiceImpl;

import com.google.inject.AbstractModule;

public class BaseModule extends AbstractModule{

	@Override
	protected void configure() {
		bind(UserDao.class).to(UserDaoImpl.class);
		bind(UserPersistenceService.class).to(UserPersistenceServiceImpl.class);
	}

}
