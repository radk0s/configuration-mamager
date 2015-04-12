package controllers.ssControllers;

import com.google.inject.Inject;
import persistence.model.User;
import securesocial.core.RuntimeEnvironment;

/**
 * Created by Ewelina on 2015-04-07.
 */
public class LocalRuntimeEnvironment extends RuntimeEnvironment.Default<User> {
    @Inject
    private UserService userService;

    @Override
    public securesocial.core.services.UserService userService() {
        return userService;
    }
}
