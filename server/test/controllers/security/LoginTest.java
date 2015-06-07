package controllers.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import org.junit.Before;
import org.junit.Test;

import persistence.configuration.BaseModule;
import persistence.model.User;
import persistence.services.UserPersistenceService;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.WithApplication;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class LoginTest extends WithApplication {

	private UserPersistenceService userService;
	public static FakeApplication app;

	@Before
	public void setUp() {
		app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
		Helpers.start(app);
		Injector injector = Guice.createInjector(new BaseModule());
		userService = injector.getInstance(UserPersistenceService.class);
	}

	@Test
	public void signUp() {

		Result result = callAction(controllers.security.routes.ref.RegistrationController.signUp(),
				fakeRequest().withFormUrlEncodedBody(ImmutableMap.of("email", "email@gmail.com", "password", "password", "passwordConfirmation", "password")));

		assertEquals(200, status(result));

		User user = userService.findByEmailAndPassword("email@gmail.com", "password");
		assertNotNull(user);

		final String header = play.test.Helpers.header(AuthenticationController.AUTH_TOKEN, result);
		assertNotNull(header);
	}

	@Test
	public void authenticateAccessAndLogout() {

		User user = new User();
		user.setEmail("email@gmail.com");
		user.setPassword("password");

		userService.save(user);

		// Authenticate
		Result result = callAction(controllers.security.routes.ref.AuthenticationController.authenticate(),
				fakeRequest().withFormUrlEncodedBody(ImmutableMap.of("email", "email@gmail.com", "password", "password")));
		assertEquals(200, status(result));

		final String header = play.test.Helpers.header(AuthenticationController.AUTH_TOKEN, result);
		assertNotNull(header);

		// Access method
		result = callAction(controllers.routes.ref.Application.index(), fakeRequest().withHeader(AuthenticationController.AUTH_TOKEN, header));
		assertEquals(200, status(result));

		// Logout
		result = callAction(controllers.security.routes.ref.AuthenticationController.logout(), fakeRequest().withHeader(AuthenticationController.AUTH_TOKEN, header));
		assertEquals(303, status(result));
	}

	@Test
	public void authenticateFailure() {
		Result result = callAction(controllers.security.routes.ref.AuthenticationController.authenticate(),
				fakeRequest().withFormUrlEncodedBody(ImmutableMap.of("email", "admin", "password", "badpassword")));
		assertEquals(400, status(result));
	}

	@Test
	public void notAuthenticated() {
		Result result = callAction(controllers.routes.ref.Application.index(), fakeRequest());
		assertEquals(303, status(result));
	}
}