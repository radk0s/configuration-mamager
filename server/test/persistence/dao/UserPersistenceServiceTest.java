package persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import persistence.configuration.BaseModule;
import persistence.filters.Filter;
import persistence.model.User;
import persistence.services.UserPersistenceService;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.WithApplication;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class UserPersistenceServiceTest extends WithApplication {

	private Injector injector;
	private UserPersistenceService userService;
	public static FakeApplication app;

	@Before
	public void setUp() throws Exception {
		app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
		Helpers.start(app);
		injector = Guice.createInjector(new BaseModule());
		userService = injector.getInstance(UserPersistenceService.class);
	}

	@After
	public void tearDown() throws Exception {
		injector = null;
	}

	@Test
	public void inject() {
		assertNotNull(userService);
	}

	@Test
	public void saveAndGet() {
		int numberOfUsers = 5;
		List<User> users = saveUsers(numberOfUsers);

		List<User> usersFromDb = userService.getAll();

		assertEquals(users.size(), usersFromDb.size());

		for (int i = 0; i < users.size(); i++) {
			User expUser = users.get(i);
			User actUser = usersFromDb.get(i);
			assertEquals(expUser.getEmail(), actUser.getEmail());
			assertEquals(expUser.getPasswordMd5(), actUser.getPasswordMd5());
		}
	}

	@Test
	public void saveAndDelete() {
		int numberOfUsers = 5;
		List<User> users = saveUsers(numberOfUsers);

		userService.delete(users.get(0));
		List<User> usersFromDb = userService.getAll();
		assertEquals(numberOfUsers - 1, usersFromDb.size());
	}

	@Test
	public void getBy() {
		int numberOfUsers = 5;
		List<User> users = saveUsers(numberOfUsers);

		User user=userService.getSingleBy(Filter.create().eqAttr("email", users.get(0).getEmail()));
		assertNotNull(user);
		assertEquals(user.getEmail(), users.get(0).getEmail());
	}

	
	private List<User> saveUsers(int numberOfUsers) {
		List<User> users = createUsers(numberOfUsers);
		for (User user : users) {
			assertNull("Id should be null before save", user.getId());
			userService.save(user);
			assertNotNull("Id should not be null after save", user.getId());
		}
		return users;
	}

	private List<User> createUsers(int numberOfUsers) {
		List<User> users = Lists.newArrayList();
		for (int i = 0; i < numberOfUsers; i++) {
			User user = createUser(i);
			users.add(user);
		}
		return users;
	}

	private User createUser(int i) {
		User user = new User();
		user.setEmail("login"+i);
		user.setPassword("password");
		return user;
	}
}
