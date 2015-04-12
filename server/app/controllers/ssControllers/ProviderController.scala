package controllers.ssControllers

import com.google.inject.{Inject, Singleton}
import persistence.model.User
import securesocial.core.RuntimeEnvironment

/**
 * Created by Ewelina on 2015-04-07.
 */
@Singleton
class ProviderController @Inject()(implicit override val env: RuntimeEnvironment[User]) extends securesocial.controllers.BaseProviderController[User]{
}
