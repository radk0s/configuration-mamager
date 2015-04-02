package services;

import play.Application;
import securesocial.core.Identity;
import securesocial.core.IdentityId;
import securesocial.core.java.BaseUserService;
import securesocial.core.java.Token;

/**
 * Created by Ewelina on 2015-04-02.
 */
public class UserService extends BaseUserService {

    public UserService(Application application) {
        super(application);
    }

    /**
     * Saves the user.  This method gets called when a user logs in.
     * This is your chance to save the user information in your backing store.
     *
     * @param user
     */
    @Override
    public Identity doSave(Identity user) {
        //TODO implement me
        return null;
    }

    /**
     * Finds an Identity in the backing store.
     *
     * @return an Identity instance or null if no user matches the specified id
     */
    @Override
    public Identity doFind(IdentityId id) {
        //TODO implement me
        return null;
    }

    /**
     * Finds an identity by email and provider id.
     * <p/>
     * Note: If you do not plan to use the UsernamePassword provider just provide en empty
     * implementation.
     *
     * @param email      - the user email
     * @param providerId - the provider id
     * @return an Identity instance or null if no user matches the specified id
     */
    @Override
    public Identity doFindByEmailAndProvider(String email, String providerId) {
        //TODO implement me
        return null;
    }

    /**
     * Saves a token
     */
    @Override
    public void doSave(Token token) {
        //TODO implement me
    }

    /**
     * Finds a token by id
     * <p/>
     * Note: If you do not plan to use the UsernamePassword provider just provide en empty
     * implementation
     *
     * @return a Token instance or null if no token matches the id
     */
    @Override
    public Token doFindToken(String tokenId) {
        //TODO implement me
        return null;
    }


    /**
     * Deletes a token
     * <p/>
     * Note: If you do not plan to use the UsernamePassword provider just provide en empty
     * implementation
     *
     * @param uuid the token id
     */
    @Override
    public void doDeleteToken(String uuid) {
        //TODO implement me
    }

    /**
     * Deletes all expired tokens
     * <p/>
     * Note: If you do not plan to use the UsernamePassword provider just provide en empty
     * implementation
     */
    @Override
    public void doDeleteExpiredTokens() {
        //TODO implement me
    }
}
