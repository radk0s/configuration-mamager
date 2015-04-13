package controllers;

import com.google.inject.Inject;

import controllers.ssControllers.LocalRuntimeEnvironment;
import persistence.model.User;
import play.mvc.*;
import securesocial.core.java.SecureSocial;
import securesocial.core.java.SecuredAction;

public class Application extends Controller {

	@Inject
	private LocalRuntimeEnvironment env;

	@SecuredAction
	public Result hello() {
        response().setContentType("application/json");
		User user = (User) ctx().args.get(SecureSocial.USER_KEY);
		final String userToken = user!=null? user.getId() : "guest";
        final String userEmail = user!=null? user.getEmail() : "guest";
        final String userDOToken = user!=null? user.getFirstName() : "guest";
        final String userAWSToken = user!=null? user.getLastName() : "guest";
		return ok("{\"userToken\":\""+userToken+"\",\"userEmail\":\""+userEmail+"\",\"userDOToken\":\""+userDOToken+"\",\"userAWSToken\":\""+userAWSToken+"\"}");
	}

}
