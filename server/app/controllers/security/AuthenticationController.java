package controllers.security;

import persistence.model.User;
import persistence.services.UserPersistenceService;
import play.data.Form;
import play.data.validation.Constraints;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import utils.TokenGenerator;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;

public class AuthenticationController extends Controller {

	@Inject
	private UserPersistenceService userService;
	public static final String AUTH_TOKEN = "authToken";

	public static User getUser() {
		return (User) Http.Context.current().args.get("user");
	}

	// returns an authToken
	public Result authenticate() {
		Form<Login> loginForm = Form.form(Login.class).bindFromRequest();

		if (loginForm.hasErrors()) {
			return badRequest(loginForm.errorsAsJson());
		}
		Login login = loginForm.get();

		User user = userService.findByEmailAndPassword(login.email, login.password);

		if (user == null) {
			return unauthorized();
		} else {
			String authToken = createToken(user);

			ObjectNode authTokenJson = Json.newObject();
			authTokenJson.put(AUTH_TOKEN, authToken);
			response().setCookie(AUTH_TOKEN, authToken);
			return ok(authTokenJson);
		}
	}

	private String createToken(User user) {
		String authToken = TokenGenerator.generate();
		user.setAuthToken(authToken);
		userService.save(user);
		return authToken;
	}

	@Security.Authenticated(Secured.class)
	public Result logout() {
		response().discardCookie(AUTH_TOKEN);
		userService.deleteAuthToken(getUser());
		return redirect("/");
	}

	public static class Login {

		@Constraints.Required
		@Constraints.Email
		public String email;

		@Constraints.Required
		public String password;
	}
}