package controllers;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import persistence.model.User;
import persistence.services.UserPersistenceService;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;

import com.google.inject.Inject;

import controllers.security.AuthenticationController;
import controllers.security.Secured;

import java.io.IOException;

public class Application extends Controller {

	private static int port = 3000;
	@Inject
	private UserPersistenceService userService;

	@Authenticated(Secured.class)
	public static Result index() {
		return ok();
	}

	@Authenticated(Secured.class)
	public Result hello() {
		response().setContentType("application/json");
		User user = null;
		String authToken = ctx().request().headers().get(AuthenticationController.AUTH_TOKEN)[0];
		if ((authToken != null)) {
			user = userService.findByAuthToken(authToken);
		}


		final long userToken = user != null ? user.getId() : 1;
		final String userEmail = user != null ? user.getEmail() : "";
		final String userDOToken = user != null ? user.getDigitalOceanToken() : "";
		final String userAWSAccessKey = user != null ? user.getAwsAccessKey() : "";
        final String userAWSSecretKey = user != null ? user.getAwsSecretKey() : "";


        return ok("{\"userToken\":\"" + userToken + "\",\"userEmail\":\"" + userEmail + "\",\"userDOToken\":\""
				+ userDOToken + "\",\"userAWSAccessKey\":\"" + userAWSAccessKey+ "\",\"userAWSSecretKey\":\"" + userAWSSecretKey + "\"}");
	}

	@Authenticated(Secured.class)
	public Result startTerminal() {
		JsonNode json = request().body().asJson();
		System.out.println(json);

		String username = json.get("username").asText();
		String hostname = json.get("host").asText();
		Integer port = Application.port++;

		try {
			String cmd = "node /root/wetty/app.js --sslkey /root/wetty/key.pem --sslcert /root/wetty/cert.pem -p "+ port +" --sshhost "+ hostname +" --sshuser "+ username +" --sshauth password";
			System.out.println(cmd);
			Runtime.getRuntime().exec(cmd);
			Thread.sleep(500);
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
			return internalServerError("Unable to create ssh agent.");
		}

		ObjectNode responseJson = Json.newObject();
		responseJson.put("port", port);
		responseJson.put("username", username);
		responseJson.put("host", hostname);
		return ok(responseJson);

	}

}
