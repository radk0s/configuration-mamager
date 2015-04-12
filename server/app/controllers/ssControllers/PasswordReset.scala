package controllers.ssControllers

import com.google.inject.Inject
import persistence.model.User
import securesocial.core.RuntimeEnvironment
import javax.inject.Singleton

/**
 * Created by Ewelina on 2015-04-07.
 */
@Singleton
class PasswordReset @Inject()(implicit override val env: RuntimeEnvironment[User]) extends securesocial.controllers.BasePasswordReset[User]{
}
