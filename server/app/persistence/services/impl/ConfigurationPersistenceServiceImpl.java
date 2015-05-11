package persistence.services.impl;

import java.util.List;

import persistence.dao.BaseDao;
import persistence.dao.ConfigurationDao;
import persistence.filters.Filter;
import persistence.model.Configuration;
import persistence.model.User;
import persistence.services.ConfigurationPersistenceService;

import com.google.inject.Inject;

public class ConfigurationPersistenceServiceImpl extends BasePersistenceServiceImpl<Configuration> implements ConfigurationPersistenceService {
	@Inject
	private ConfigurationDao configurationDao;

	@Override
	protected BaseDao<Configuration> getBaseDao() {
		return configurationDao;
	}

	@Override
	public List<Configuration> getConfigurationsByUser(User user) {
		return configurationDao.getBy(Filter.create().eqAttr("user", user));
	}

	@Override
	public void deleteByName(String name) {
		Configuration conf = configurationDao.getSingleBy(Filter.create().eqAttr("name", name));
		configurationDao.delete(conf);
	}

}
