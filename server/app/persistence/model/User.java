package persistence.model;

import javax.persistence.Entity;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

@Entity
public class User extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	private String login;
	private int passwordMd5;

	public static int calculateMd5(String password) {
		HashFunction hf = Hashing.md5();
		HashCode hc = hf.newHasher().putString(password)
		.hash();
		return hc.asInt();
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public int getPasswordMd5() {
		return passwordMd5;
	}

	public void setPasswordMd5(int passwordMd5) {
		this.passwordMd5 = passwordMd5;
	}
}
