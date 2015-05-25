package controllers;

import persistence.model.User;
import persistence.services.UserPersistenceService;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;

import com.google.inject.Inject;

import controllers.security.AuthenticationController;
import controllers.security.Secured;

import java.io.IOException;

public class Application extends Controller {

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
		try {
			Process myProcess = Runtime.getRuntime().exec("node /home/radchamot/Desktop/wetty/app.js -p 3000");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ok();

	}

}
