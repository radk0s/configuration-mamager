package controllers.configuration;

import persistence.model.Configuration;
import persistence.model.Provider;

public class ConfigurationTranslator {

	public static persistence.model.Configuration convert(ConfigurationController.Configuration configurationFrom) {
		Configuration configurationTo = new Configuration();
		configurationTo.setName(configurationFrom.name);
		configurationTo.setData(configurationFrom.data);
		configurationTo.setProvider(Provider.valueOf(configurationFrom.provider));
		return configurationTo;
	}
}
