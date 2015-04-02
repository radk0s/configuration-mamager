package controllers;

import play.*;
import play.mvc.*;

import securesocial.core.java.SecureSocial;
import views.html.*;

public class Application extends Controller {


    @SecureSocial.SecuredAction
    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

}
