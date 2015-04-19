package controllers.security;

import persistence.model.User;
import persistence.services.UserPersistenceService;
import play.data.Form;
import play.data.validation.Constraints;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.TokenGenerator;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;

public class RegistrationController extends Controller {

	@Inject
	private UserPersistenceService userService;

	public Result signUp() {
		Form<Register> registerForm = Form.form(Register.class).bindFromRequest();

		if (registerForm.hasErrors()) {
			return badRequest(registerForm.errorsAsJson());
		}

		Register register = registerForm.get();

		User user = createUser(register);
		String authToken = TokenGenerator.generate();
		user.setAuthToken(authToken);

		userService.save(user);

		ObjectNode authTokenJson = Json.newObject();
		authTokenJson.put(AuthenticationController.AUTH_TOKEN, authToken);
		response().setCookie(AuthenticationController.AUTH_TOKEN, authToken);
		return ok(authTokenJson);

	}

	private User createUser(Register register) {
		User user = new User();
		user.setEmail(register.email);
		user.setPassword(register.password);
		user.setAuthToken(register.awsToken);
		user.setDigitalOceanToken(register.doToken);
		return user;
	}

	public static class Register {

		@Constraints.Required
		@Constraints.Email
		public String email;

		@Constraints.Required
		public String password;

		@Constraints.Required
		public String passwordConfirmation;

		public String awsToken;

		public String doToken;

		/**
		 * Validate the authentication.
		 *
		 * @return null if validation ok, string with details otherwise
		 */
		public String validate() {
			if (isBlank(email)) {
				return "Email is required";
			}

			if (isBlank(password)) {
				return "Password is required";
			}

			if (isBlank(passwordConfirmation)) {
				return "Password confirmation is required";
			}
			return null;
		}

		private boolean isBlank(String input) {
			return input == null || input.isEmpty() || input.trim().isEmpty();
		}
	}
}
