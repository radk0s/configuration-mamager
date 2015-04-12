package persistence.assemblers;

import akka.japi.Option;
import persistence.model.User;
import securesocial.core.AuthenticationMethod;
import securesocial.core.BasicProfile;
import securesocial.core.PasswordInfo;

/**
 * Created by Ewelina on 2015-04-11.
 */
public class UserAssembler {

    private static void provideBasicUserInfo(BasicProfile basicProfile, User user) {
        if (basicProfile.email().isDefined())
            user.setEmail(basicProfile.email().get());
        if (basicProfile.firstName().isDefined())
            user.setFirstName(basicProfile.firstName().get());
        if (basicProfile.lastName().isDefined())
            user.setLastName(basicProfile.lastName().get());
        if (basicProfile.passwordInfo().isDefined()) {
            user.setPassword(basicProfile.passwordInfo().get().password());
        }
    }

    public static User convert(BasicProfile basicProfile) {
        User user = new User();
        user.setId(basicProfile.userId());
        user.setProvider(basicProfile.providerId());
        provideBasicUserInfo(basicProfile, user);
        return user;
    }

    public static void update(BasicProfile basicProfile, User user) {
        provideBasicUserInfo(basicProfile, user);
    }

    public static BasicProfile convert(User user) {
        BasicProfile basicProfile = new BasicProfile(user.getProvider(), user.getId(), scala.Option.apply(user.getFirstName()), scala.Option.apply(user.getLastName()), null, scala.Option.apply(user.getEmail()), null, new AuthenticationMethod("userPassword"), null, null, scala.Option.apply(new PasswordInfo("bcrypt", user.getPassword(), null)));
        return basicProfile;
    }
}