package persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import utils.Hasher;

@Entity
@Table(name = "USERS")
public class User extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "PASSWORD")
	private int passwordMd5;

	@Column(name = "PROVIDER")
	private String provider;

	@Column(name = "FIRST_NAME")
	private String firstName;

	@Column(name = "LAST_NAME")
	private String lastName;

	private Integer authToken;

	private String awsToken;

	private String digitalOceanToken;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getPasswordMd5() {
		return passwordMd5;
	}

	public void setPassword(String password) {
		this.passwordMd5 = Hasher.calculateMd5(password);
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Integer getAuthTokenMd5() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		if (authToken != null)
			this.authToken = Hasher.calculateMd5(authToken);
		else
			this.authToken = null;
	}

	public String getAwsToken() {
		return awsToken;
	}

	public void setAwsToken(String awsToken) {
		this.awsToken = awsToken;
	}

	public String getDigitalOceanToken() {
		return digitalOceanToken;
	}

	public void setDigitalOceanToken(String digitalOceanToken) {
		this.digitalOceanToken = digitalOceanToken;
	}
}
