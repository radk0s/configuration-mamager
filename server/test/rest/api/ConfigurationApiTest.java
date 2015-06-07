package rest.api;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static play.test.Helpers.DELETE;
import static play.test.Helpers.GET;
import static play.test.Helpers.POST;
import static play.test.Helpers.callAction;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.route;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import persistence.configuration.BaseModule;
import persistence.model.Provider;
import persistence.services.ConfigurationPersistenceService;
import play.libs.Json;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.Helpers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

import controllers.security.AuthenticationController;

public class ConfigurationApiTest {

	public static FakeApplication app;
	private ConfigurationPersistenceService configurationService;
	private String header;
	private Injector injector;

	@Before
	public void setUp() throws Exception {
		app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
		Helpers.start(app);
		injector = Guice.createInjector(new BaseModule());
		configurationService = injector.getInstance(ConfigurationPersistenceService.class);
		// FIXME: change user to existing user with DO token?
		Result result = callAction(controllers.security.routes.ref.RegistrationController.signUp(),
				fakeRequest().withFormUrlEncodedBody(ImmutableMap.of("email", "email@gmail.com", "password", "password", "passwordConfirmation", "password")));

		header = play.test.Helpers.header(AuthenticationController.AUTH_TOKEN, result);
	}

	@After
	public void tearDown() throws Exception {
		injector = null;
	}

	@Test
	public void manageConfigurations() throws JSONException {
		// Get number of configurations before create
		int numberOfConfigurations = configurationService.getBy(null).size();

		// Create Configuration
		ImmutableMap<String, String> body = ImmutableMap.of("name", "name", "provider", Provider.DIGITAL_OCEAN.toString(), "data", "data");
		Result r = route(fakeRequest(POST, "/configuration").withFormUrlEncodedBody(body).withHeader(AuthenticationController.AUTH_TOKEN, header));

		assertThat(r).isNotNull();
		assertEquals(configurationService.getBy(null).size(), numberOfConfigurations + 1);

		// Get configurations
		r = route(fakeRequest(GET, "/configuration").withHeader(AuthenticationController.AUTH_TOKEN, header));

		assertThat(contentAsString(r)).isNotNull();
		JSONArray array = new JSONArray(contentAsString(r));
		assertEquals(array.length(), numberOfConfigurations + 1);

		// Delete configuration
		ObjectNode json = Json.newObject();
		json.put("name", "name");
		r = route(fakeRequest(DELETE, "/configuration").withJsonBody(json).withHeader(AuthenticationController.AUTH_TOKEN, header));

		assertThat(contentAsString(r)).isNotNull();
		assertEquals(configurationService.getBy(null).size(), numberOfConfigurations);
	}
}
