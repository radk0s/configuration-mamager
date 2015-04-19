package controllers.security;

import persistence.configuration.BaseModule;
import persistence.model.User;
import persistence.services.UserPersistenceService;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class Secured extends Security.Authenticator {

	@Inject
	UserPersistenceService userService;

	// WORKAROUND - fixed in Play! 2.4 ->
	// Play.application().injector().instanceOf(Security.Authenticator.class);
	public Secured() {
		Injector injector = Guice.createInjector(new BaseModule());
		userService = injector.getInstance(UserPersistenceService.class);
	}

	@Override
	public String getUsername(Context ctx) {
		User user = null;
		String[] authToken = ctx.request().headers().get(AuthenticationController.AUTH_TOKEN);

		if ((authToken != null && authToken.length>0)) {
			user = userService.findByAuthToken(authToken[0]);
			if (user != null) {
				ctx.args.put("user", user);
				return user.getEmail();
			}
		}
		return null;
	}

	@Override
	public Result onUnauthorized(Context ctx) {
		return redirect("");
	}
}
