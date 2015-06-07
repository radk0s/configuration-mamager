package persistence.services.impl;

import persistence.dao.BaseDao;
import persistence.dao.UserDao;
import persistence.filters.Filter;
import persistence.model.User;
import persistence.services.UserPersistenceService;
import utils.Hasher;

import com.avaje.ebean.annotation.Transactional;
import com.google.inject.Inject;

@Transactional
public class UserPersistenceServiceImpl extends BasePersistenceServiceImpl<User> implements UserPersistenceService {

	@Inject
	private UserDao userDao;

	@Override
	protected BaseDao<User> getBaseDao() {
		return userDao;
	}

	@Override
	public User findByAuthToken(String authToken) {
		return userDao.getSingleBy(Filter.create().eqAttr("authToken", Hasher.calculateMd5(authToken)));
	}

	@Override
	public User findByEmailAndPassword(String email, String password) {
		return userDao.getSingleBy(Filter.create().eqAttr("email", email).eqAttr("password", Hasher.calculateMd5(password)));
	}

	@Override
	public User findByEmail(String email) {
		return userDao.getSingleBy(Filter.create().eqAttr("email", email));
	}

	@Override
	public void deleteAuthToken(User user) {
		user.setAuthToken(null);
		userDao.save(user);
	}
}