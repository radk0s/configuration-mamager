package persistence.assemblers;


import persistence.model.LocalToken;
import securesocial.core.java.Token;

/**
 * Created by Ewelina on 2015-04-11.
 */
public class TokenAssembler {

    public static Token convert(LocalToken token) {
        securesocial.core.java.Token resultToken = new securesocial.core.java.Token();
        resultToken.setUuid(token.getUuid());
        resultToken.setCreationTime(token.getCreatedAt());
        resultToken.setExpirationTime(token.getExpireAt());
        resultToken.setEmail(token.getEmail());
        resultToken.setIsSignUp(token.isSignUp());

        return resultToken;
    }

    public static LocalToken convert(Token token) {
        LocalToken resultToken = new LocalToken();
        resultToken.setUuid(token.getUuid());
        resultToken.setCreatedAt(token.getCreationTime());
        resultToken.setExpireAt(token.getExpirationTime());
        resultToken.setEmail(token.getEmail());
        resultToken.setIsSignUp(token.getIsSignUp());

        return resultToken;
    }
}
