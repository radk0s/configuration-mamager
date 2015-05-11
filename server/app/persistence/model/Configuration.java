package persistence.model;

import javax.persistence.Entity;

@Entity
public class Configuration extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	private String name;
	private String data;
	private Provider provider;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

}
