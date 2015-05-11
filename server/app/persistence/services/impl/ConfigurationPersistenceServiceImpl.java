package persistence.services.impl;

import java.util.List;

import persistence.dao.BaseDao;
import persistence.dao.ConfigurationDao;
import persistence.filters.Filter;
import persistence.model.Configuration;
import persistence.model.User;
import persistence.services.ConfigurationPersistenceService;
import persistence.services.UserPersistenceService;

import com.google.inject.Inject;

public class ConfigurationPersistenceServiceImpl extends BasePersistenceServiceImpl<Configuration> implements ConfigurationPersistenceService {
	@Inject
	private ConfigurationDao configurationDao;
	@Inject
	private UserPersistenceService userService;

	@Override
	protected BaseDao<Configuration> getBaseDao() {
		return configurationDao;
	}

	@Override
	public List<Configuration> getConfigurationsByUser(User user) {
		User userFromDb = userService.findByEmail(user.getEmail());
		return configurationDao.getBy(Filter.create().eqAttr("user", userFromDb));
	}

	@Override
	public void deleteByName(String name) {
		Configuration conf = findConfigurationByName(name);
		configurationDao.delete(conf);
	}

	@Override
	public void save(Configuration entity) {
		User userFromDb = userService.findByEmail(entity.getUser().getEmail());
		entity.setUser(userFromDb);
		super.save(entity);
	}

	@Override
	public Configuration findConfigurationByName(String name) {
		return configurationDao.getSingleBy(Filter.create().eqAttr("name", name));
	}
}
