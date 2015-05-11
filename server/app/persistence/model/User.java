package persistence.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import utils.Hasher;

import com.google.common.collect.Lists;

@Entity
@Table(name = "USERS")
public class User extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "PASSWORD")
	private int passwordMd5;

	@Column(name = "PROVIDER")
	private Provider provider;

	@Column(name = "FIRST_NAME")
	private String firstName;

	@Column(name = "LAST_NAME")
	private String lastName;

	private Integer authToken;

	private String awsSecretKey;

	private String awsAccessKey;

	private String digitalOceanToken;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Configuration> configurations = Lists.newArrayList();

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

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
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

	public String getAwsAccessKey() {
		return awsAccessKey;
	}

	public void setAwsAccessKey(String awsToken) {
		this.awsAccessKey = awsToken;
	}

	public String getAwsSecretKey() {
		return awsSecretKey;
	}

	public void setAwsSecretKey(String awsSecretToken) {
		this.awsSecretKey = awsSecretToken;
	}

	public String getDigitalOceanToken() {
		return digitalOceanToken;
	}

	public void setDigitalOceanToken(String digitalOceanToken) {
		this.digitalOceanToken = digitalOceanToken;
	}

	public List<Configuration> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(List<Configuration> configurations) {
		this.configurations = configurations;
	}
}
