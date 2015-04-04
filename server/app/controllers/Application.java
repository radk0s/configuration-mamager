package controllers;

import com.google.inject.Inject;

import persistence.dao.UserDao;
import persistence.model.User;
import persistence.services.UserPersistenceService;
import persistence.services.impl.UserPersistenceServiceImpl;
import play.*;
import play.mvc.*;
import securesocial.core.java.SecureSocial;
import views.html.*;

public class Application extends Controller {

	@SecureSocial.SecuredAction
	public Result index() {
		return ok(index.render("Your new application is ready."));
	}
}
