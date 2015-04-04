package persistence.services.impl;

import com.avaje.ebean.annotation.Transactional;
import com.google.inject.Inject;

import persistence.dao.BaseDao;
import persistence.dao.UserDao;
import persistence.filters.Filter;
import persistence.model.User;
import persistence.services.UserPersistenceService;

@Transactional
public class UserPersistenceServiceImpl extends BasePersistenceServiceImpl<User> implements UserPersistenceService {

	@Inject
	private UserDao userDao;

	@Override
	protected BaseDao<User> getBaseDao() {
		return userDao;
	}
}
