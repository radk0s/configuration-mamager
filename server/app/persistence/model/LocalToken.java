package persistence.model;

import org.joda.time.DateTime;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Ewelina on 2015-04-11.
 */

@Entity
@Table(name = "TOKENS")
public class LocalToken extends Model {

    private static final long serialVersionUID = 1L;

    @Id
    private String uuid;
    private String email;
    private DateTime createdAt;
    private DateTime expireAt;
    private boolean isSignUp;

    public static Finder<String, LocalToken> find = new Finder<String, LocalToken>(
            String.class, LocalToken.class
    );

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public DateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(DateTime expireAt) {
        this.expireAt = expireAt;
    }

    public boolean isSignUp() {
        return isSignUp;
    }

    public void setIsSignUp(boolean isSignUp) {
        this.isSignUp = isSignUp;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
