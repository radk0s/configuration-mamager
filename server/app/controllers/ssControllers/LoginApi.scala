package controllers.ssControllers

import javax.inject.Singleton

import com.google.inject.Inject
import persistence.model.User
import play.api.mvc.Action
import securesocial.core._
import securesocial.core.providers.UsernamePasswordProvider
import securesocial.core.services.SaveMode

/**
 * Created by Ewelina on 2015-04-07.
 */
@Singleton
class LoginApi @Inject()(implicit override val env: RuntimeEnvironment[User]) extends securesocial.controllers.BaseLoginApi[User]{

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit val rds  = (
      (__ \ 'email).read[String] and
      (__ \ 'password).read[String] and
      (__ \ 'password2).read[String] and
      (__ \ 'firstName).read[String] and
      (__ \ 'lastName).read[String]
    ) tupled


  def signUp = Action(parse.json) { request =>
        request.body.validate[(String, String, String, String, String)].map {
            case (email, password, password2, firstName, lastName) => {
              var res : Map[String,String] = Map("status" -> "OK", "message" -> "Your profile was successfully saved");
              env.userService.findByEmailAndProvider(email, UsernamePasswordProvider.UsernamePassword).map {
                maybeUser => {
                  maybeUser match {
                    case Some(user) => {
                      res = Map("status" -> "BAD REQUEST", "message" -> "provided email already exists")
                    }
                    case None => {
                      val basicProfile = BasicProfile(
                        UsernamePasswordProvider.UsernamePassword,
                        email,
                        Some(firstName),
                        Some(lastName),
                        Some("%s %s".format(firstName, lastName)),
                        Some(email),
                        None,
                        AuthenticationMethod.UserPassword,
                        passwordInfo = Some(env.currentHasher.hash(password)))

                      env.userService.save(basicProfile, SaveMode.SignUp);
                    }
                  }
                }
              }
              if(res.get("status").equals(Some("BAD REQUEST")) ) {
                BadRequest(Json.toJson(res))
              } else {
                Ok(Json.toJson(res))
              }
            }
        }.recoverTotal {
          e => BadRequest(Json.toJson(Map("status" -> "BAD REQUEST", "message" -> ("Validation error:" + JsError.toFlatJson(e)))))
        }
  }
}


