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
		User user = (User) ctx().args.get(SecureSocial.USER_KEY);
		final String userName = user!=null? user.getId() : "guest";
		return ok("Hello "+userName);
	}

}
