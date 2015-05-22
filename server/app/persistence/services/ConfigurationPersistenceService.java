package persistence.services;

import java.util.List;

import persistence.model.Configuration;
import persistence.model.User;

public interface ConfigurationPersistenceService extends BasePersistenceService<Configuration> {

	List<Configuration> getConfigurationsByUser(User user);

	void deleteByName(String name);

	Configuration findConfigurationByName(String name);
}
