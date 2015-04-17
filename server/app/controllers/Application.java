package controllers;

import persistence.model.User;
import persistence.services.UserPersistenceService;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;

import com.google.inject.Inject;

import controllers.security.AuthenticationController;
import controllers.security.Secured;

public class Application extends Controller {

	@Inject
	private UserPersistenceService userService;

	@Authenticated(Secured.class)
	public static Result index() {
		return ok();
	}

	public Result hello() {
		response().setContentType("application/json");
		User user = null;

		String authTokenValue = ctx().request().cookie(AuthenticationController.AUTH_TOKEN).value();

		if ((authTokenValue != null)) {
			user = userService.findByAuthToken(authTokenValue);
		}

		final long userToken = user != null ? user.getId() : 1;
		final String userEmail = user != null ? user.getEmail() : "guest";
		final String userDOToken = user != null ? user.getFirstName() : "guest";
		final String userAWSToken = user != null ? user.getLastName() : "guest";

		return ok("{\"userToken\":\"" + userToken + "\",\"userEmail\":\"" + userEmail + "\",\"userDOToken\":\""
				+ userDOToken + "\",\"userAWSToken\":\"" + userAWSToken + "\"}");
	}

}
