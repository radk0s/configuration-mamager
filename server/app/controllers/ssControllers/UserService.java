package controllers.ssControllers;

import org.joda.time.DateTime;
import persistence.assemblers.TokenAssembler;
import persistence.assemblers.UserAssembler;
import persistence.model.LocalToken;
import persistence.model.User;
import play.libs.F;
import securesocial.core.BasicProfile;
import securesocial.core.PasswordInfo;
import securesocial.core.java.BaseUserService;
import securesocial.core.java.Token;
import securesocial.core.services.SaveMode;

import java.util.List;

/**
 * Created by Ewelina on 2015-04-07.
 */
public class UserService extends BaseUserService<User> {

    @Override
    public F.Promise<User> doSave(BasicProfile basicProfile, SaveMode saveMode) {
       User user = null;
        if(saveMode == SaveMode.SignUp()) {
            user = UserAssembler.convert(basicProfile);
            user.save();
        } else if(saveMode == SaveMode.LoggedIn()) {
            user = User.find.byId(basicProfile.userId());
            UserAssembler.update(basicProfile, user);
            user.update();
        } else {
            throw new RuntimeException("Unknown mode");
        }

        return F.Promise.pure(user);
    }

    @Override
    public F.Promise<Token> doSaveToken(Token token) {
        LocalToken localToken = TokenAssembler.convert(token);
        localToken.save();
        return F.Promise.pure(token);
    }

    @Override
    public F.Promise<User> doLink(User user, BasicProfile basicProfile) {
        return F.Promise.pure(user);
    }

    @Override
    public F.Promise<BasicProfile> doFind(String providerId, String userId) {
        BasicProfile basicProfile = null;
        User user = User.find.byId(userId);
        if(user != null) {
            basicProfile = UserAssembler.convert(user);
        }
        return F.Promise.pure(basicProfile);
    }

    @Override
    public F.Promise<PasswordInfo> doPasswordInfoFor(User user) {
        //TODO: IMPLEMENT ME!
        return null;
    }

    @Override
    public F.Promise<BasicProfile> doUpdatePasswordInfo(User user, PasswordInfo passwordInfo) {
        //TODO: IMPLEMENT ME!
        return null;
    }

    @Override
    public F.Promise<Token> doFindToken(String token) {
        Token result = null;
        LocalToken localToken = LocalToken.find.byId(token);
        if(localToken != null) {
            result = TokenAssembler.convert(localToken);
        }
        return F.Promise.pure(result);
    }

    @Override
    public F.Promise<BasicProfile> doFindByEmailAndProvider(String email, String providerId) {
        BasicProfile basicProfile = null;
        User user = User.findByEmail(email);
        if (user != null) {
            basicProfile = UserAssembler.convert(user);
        }
        return F.Promise.pure(basicProfile);
    }

    @Override
    public F.Promise<Token> doDeleteToken(String uuid) {
        LocalToken localToken = LocalToken.find.byId(uuid);
        Token token = null;
        if(localToken != null) {
            localToken.delete();
            token = TokenAssembler.convert(localToken);
        }
        return  F.Promise.pure(token);
    }

    @Override
    public void doDeleteExpiredTokens() {
        List<LocalToken> list = LocalToken.find.where().lt("expireAt", new DateTime().toString()).findList();
        for(LocalToken localToken : list) {
            localToken.delete();
        }
    }
}
