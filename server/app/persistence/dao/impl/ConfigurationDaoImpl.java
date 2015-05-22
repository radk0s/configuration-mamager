package persistence.dao.impl;

import persistence.dao.ConfigurationDao;
import persistence.model.Configuration;

import com.google.inject.Singleton;

@Singleton
public class ConfigurationDaoImpl extends BaseDaoImpl<Configuration> implements ConfigurationDao {

	protected ConfigurationDaoImpl() {
		super(Configuration.class);
	}

}