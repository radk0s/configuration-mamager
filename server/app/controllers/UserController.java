package controllers;

import java.util.List;

import com.google.inject.Inject;

import persistence.filters.Filter;
import persistence.model.User;
import persistence.services.UserPersistenceService;
import play.mvc.Controller;
import play.mvc.Result;

public class UserController extends Controller {

	@Inject
	private UserPersistenceService userService;

	public Result getUsers() {
		List<User> users = userService.getAll();
		String usersNames="";
		for(User user:users)
			usersNames+=" "+user.getLogin();
		if (users.size() > 0)
			return ok("There are "+users.size()+" users in database. This users are: "+usersNames);
		else
			return ok("There is no users in database.");
	}

	public Result addUser(String login, int passwordMd5) {
		User user = new User();
		user.setLogin(login);
		user.setPasswordMd5(passwordMd5);
		userService.save(user);
		return ok("User saved");
	}
	
	public Result deleteUser(String login){
		User user=userService.getSingleBy(Filter.create().eqAttr("login", login));
		if(user==null)
			return ok("There is no user with login "+login);
		userService.delete(user);

		return ok("User deleted");
	}
}
