package controllers.ssControllers

import com.google.inject.Inject
import persistence.model.User
import securesocial.core.RuntimeEnvironment
import javax.inject.Singleton
/**
 * Created by Ewelina on 2015-04-07.
 */
@Singleton
class LoginPage @Inject()(implicit override val env: RuntimeEnvironment[User]) extends securesocial.controllers.BaseLoginPage[User]{
}
