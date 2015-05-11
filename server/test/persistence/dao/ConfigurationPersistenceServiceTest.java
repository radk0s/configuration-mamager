package persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import persistence.configuration.BaseModule;
import persistence.model.Configuration;
import persistence.model.User;
import persistence.services.ConfigurationPersistenceService;
import persistence.services.UserPersistenceService;
import play.test.FakeApplication;
import play.test.Helpers;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ConfigurationPersistenceServiceTest {

	private Injector injector;
	private ConfigurationPersistenceService configurationService;
	private UserPersistenceService userService;
	public static FakeApplication app;

	@Before
	public void setUp() throws Exception {
		app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
		Helpers.start(app);
		injector = Guice.createInjector(new BaseModule());
		configurationService = injector.getInstance(ConfigurationPersistenceService.class);
		userService = injector.getInstance(UserPersistenceService.class);
	}

	@After
	public void tearDown() throws Exception {
		injector = null;
	}

	@Test
	public void inject() {
		assertNotNull(configurationService);
	}

	@Before
	public void saveUser() {
		userService.save(createUser(1));
		userService.save(createUser(2));
	}

	@Test
	public void saveAndGet() {
		int numberOfConfigs = 5;
		List<Configuration> configurations = saveConfigurations(numberOfConfigs, createUser(1));

		List<Configuration> configFromDb = configurationService.getAll();

		assertEquals(configurations.size(), configFromDb.size());

		compareConfigs(configurations, configFromDb);
	}

	private void compareConfigs(List<Configuration> configurations, List<Configuration> configFromDb) {
		for (int i = 0; i < configurations.size(); i++) {
			Configuration expConfig = configurations.get(i);
			Configuration actConfig = configFromDb.get(i);
			assertEquals(expConfig.getName(), actConfig.getName());
			assertEquals(expConfig.getData(), actConfig.getData());
			assertEquals(expConfig.getUser(), actConfig.getUser());
		}
	}

	@Test
	public void saveAndDelete() {
		int numberOfConfigs = 5;
		List<Configuration> configs = saveConfigurations(numberOfConfigs, createUser(1));

		configurationService.delete(configs.get(0));
		List<Configuration> configsFromDb = configurationService.getAll();
		assertEquals(numberOfConfigs - 1, configsFromDb.size());
	}

	@Test
	public void getByName() {
		int numberOfConfigs = 5;
		List<Configuration> configs = saveConfigurations(numberOfConfigs, createUser(1));

		Configuration config = configurationService.findConfigurationByName(configs.get(0).getName());
		assertNotNull(config);
		assertEquals(config.getName(), configs.get(0).getName());
	}

	@Test
	public void getByUser() {
		int numberOfConfigs = 5;
		User user1 = createUser(1);
		User user2 = createUser(2);
		List<Configuration> user1Configs = saveConfigurations(numberOfConfigs, user1);
		List<Configuration> user2Configs = saveConfigurations(numberOfConfigs + 2, user2);

		List<Configuration> user1ConfigsFromDb = configurationService.getConfigurationsByUser(user1);
		assertEquals(user1Configs.size(), user1ConfigsFromDb.size());
		compareConfigs(user1Configs, user1ConfigsFromDb);
		List<Configuration> user2ConfigsFromDb = configurationService.getConfigurationsByUser(user2);
		assertEquals(user2Configs.size(), user2ConfigsFromDb.size());
		compareConfigs(user2Configs, user2ConfigsFromDb);
	}

	private List<Configuration> saveConfigurations(int numberOfConfiguration, User user) {
		List<Configuration> configs = createConfigurations(numberOfConfiguration);
		for (Configuration config : configs) {
			assertNull("Id should be null before save", config.getId());
			config.setUser(user);
			configurationService.save(config);
			assertNotNull("Id should not be null after save", config.getId());
		}
		return configs;
	}

	private List<Configuration> createConfigurations(int numberOfConfigs) {
		List<Configuration> configs = Lists.newArrayList();
		for (int i = 0; i < numberOfConfigs; i++) {
			Configuration config = createConfiguration(i);
			configs.add(config);
		}
		return configs;
	}

	private Configuration createConfiguration(int i) {
		Configuration config = new Configuration();
		config.setName("name" + i);
		config.setData("data");
		return config;
	}

	private User createUser(int i) {
		User user = new User();
		user.setEmail("login" + i);
		user.setPassword("password");
		return user;
	}

}
