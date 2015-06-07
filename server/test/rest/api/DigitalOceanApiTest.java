package rest.api;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.DELETE;
import static play.test.Helpers.GET;
import static play.test.Helpers.POST;
import static play.test.Helpers.PUT;
import static play.test.Helpers.callAction;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.route;

import org.junit.Before;
import org.junit.Test;

import play.libs.Json;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.Helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;

import controllers.security.AuthenticationController;

public class DigitalOceanApiTest {

	public static FakeApplication app;
	private String header;

	@Before
	public void setUp() throws Exception {
		app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
		Helpers.start(app);

		// FIXME: change user to existing user with DO token?
		Result result = callAction(controllers.security.routes.ref.RegistrationController.signUp(),
				fakeRequest().withFormUrlEncodedBody(ImmutableMap.of("email", "email@gmail.com", "password", "password", "passwordConfirmation", "password")));

		header = play.test.Helpers.header(AuthenticationController.AUTH_TOKEN, result);
	}

	@Test
	public void listInstances() {
		Result r = route(fakeRequest(GET, "/instances/do").withHeader(AuthenticationController.AUTH_TOKEN, header));
		assertThat(contentAsString(r)).isNotNull();
	}

	@Test
	public void createInstance() {
		Result r = route(fakeRequest(PUT, "/instances/do").withJsonBody(createJsonForCreateInstance()).withHeader(AuthenticationController.AUTH_TOKEN, header));
		assertThat(contentAsString(r)).isNotNull();
	}

	private ObjectNode createJsonForCreateInstance() {
		ObjectNode requestJson = Json.newObject();
		// FIXME: add name, region, size and image
		requestJson.put("name", "");
		requestJson.put("region", "");
		requestJson.put("size", "");
		requestJson.put("image", "");
		requestJson.putNull("ssh_keys");
		requestJson.put("backups", false);
		requestJson.put("ipv6", true);
		requestJson.putNull("user_data");
		requestJson.putNull("private_networking");
		return requestJson;
	}

	@Test
	public void runInstance() {
		Result r = route(fakeRequest(POST, "/instances/do/run").withJsonBody(createJsonWithID()).withHeader(AuthenticationController.AUTH_TOKEN, header));
		assertThat(contentAsString(r)).isNotNull();
	}

	private JsonNode createJsonWithID() {
		ObjectNode requestJson = Json.newObject();
		// FIXME: add name, region, size and image
		requestJson.put("instanceId", "");
		return requestJson;
	}

	@Test
	public void stopInstance() {
		Result r = route(fakeRequest(POST, "/instances/do/stop").withJsonBody(createJsonWithID()).withHeader(AuthenticationController.AUTH_TOKEN, header));
		assertThat(contentAsString(r)).isNotNull();
	}

	@Test
	public void deleteInstance() {
		Result r = route(fakeRequest(DELETE, "/instances/do").withJsonBody(createJsonWithID()).withHeader(AuthenticationController.AUTH_TOKEN, header));
		assertThat(contentAsString(r)).isNotNull();
	}

	@Test
	public void listRegions() {
		Result r = route(fakeRequest(GET, "/regions/do").withHeader(AuthenticationController.AUTH_TOKEN, header));
		assertThat(contentAsString(r)).isNotNull();
	}

	@Test
	public void listImages() {
		Result r = route(fakeRequest(GET, "/images/do").withHeader(AuthenticationController.AUTH_TOKEN, header));
		assertThat(contentAsString(r)).isNotNull();
	}

	@Test
	public void listSizes() {
		Result r = route(fakeRequest(GET, "/sizes/do").withHeader(AuthenticationController.AUTH_TOKEN, header));
		assertThat(contentAsString(r)).isNotNull();
	}
}
