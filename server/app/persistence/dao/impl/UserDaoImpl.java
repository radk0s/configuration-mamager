package persistence.dao.impl;

import com.google.inject.Singleton;

import persistence.dao.UserDao;
import persistence.model.User;

@Singleton
public class UserDaoImpl extends BaseDaoImpl<User> implements UserDao {

	protected UserDaoImpl() {
		super(User.class);
	}

}