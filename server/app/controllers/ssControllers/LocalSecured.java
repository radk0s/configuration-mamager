package controllers.ssControllers;

import com.google.inject.Inject;
import securesocial.core.java.Secured;

/**
 * Created by Ewelina on 2015-04-07.
 */
public class LocalSecured extends Secured {
    @Inject
    public LocalSecured(LocalRuntimeEnvironment env) throws Throwable {
        super(env);
    }
}
