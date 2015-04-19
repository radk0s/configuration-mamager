package persistence.services;

import persistence.model.User;

public interface UserPersistenceService extends BasePersistenceService<User> {

	User findByAuthToken(String string);

	User findByEmailAndPassword(String email, String password);
	
	void deleteAuthToken(User user);

	User findByEmail(String email);

}