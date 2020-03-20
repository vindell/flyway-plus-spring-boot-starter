package org.flywaydb.spring.boot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.spring.boot.ext.FlywayFluentConfiguration;
import org.flywaydb.spring.boot.ext.FlywayMigrationProvider;
import org.flywaydb.spring.boot.ext.FlywayModularizedMigrationInitializer;
import org.flywaydb.spring.boot.ext.FlywayModularizedProperties;
import org.flywaydb.spring.boot.ext.resolver.LocationModuleResolver;
import org.flywaydb.spring.boot.ext.resolver.LocationVendorResolver;
import org.flywaydb.spring.boot.ext.resolver.TableModuleResolver;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.jpa.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcOperationsDependsOnPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 扩展Flyway实现，解决使用Druid数据源时执行SQL权限问题（Druid安全机制导致）
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Configuration
@ConditionalOnClass(Flyway.class)
@ConditionalOnBean({DataSource.class})
@ConditionalOnProperty(prefix = "spring.flyway", name = "moduleable", havingValue = "true")
/** 在主体数据库迁移之前完成各个模块的数据库迁移 */
@AutoConfigureBefore(name = {
	"org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
	"org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration",
	"org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration",
	"com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration",
	"com.alibaba.druid.spring.boot.DruidAutoConfiguration",
	"com.zaxxer.hikari.spring.boot.HikaricpAutoConfiguration",
	"org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"
})
@EnableConfigurationProperties({ FlywayModularizedMigrationProperties.class })
public class FlywayModularizedAutoConfiguration{
	
	@Bean
	@FlywayDataSource
	public DataSource flywayDatasource(DataSourceProperties properties) {
		return properties.initializeDataSourceBuilder().build();
	}
	
	@Bean
	public FlywayMigrationProvider flywayMigration() {
		return new FlywayMigrationProvider();
	}
	
	@Configuration
	@EnableConfigurationProperties({ DataSourceProperties.class, FlywayModularizedMigrationProperties.class })
	public static class FlywayModuleConfiguration {

		private final FlywayModularizedMigrationProperties properties;
		
		private final DataSourceProperties dataSourceProperties;

		private final ResourceLoader resourceLoader;

		private final DataSource dataSource;

		private final DataSource flywayDataSource;
		
		private final List<FlywayConfigurationCustomizer> configurationCustomizers;

		private final ObjectProvider<Callback> callbacks;
		
		private final List<FlywayFluentConfiguration> configurations;

		public FlywayModuleConfiguration(
				FlywayModularizedMigrationProperties properties,
				DataSourceProperties dataSourceProperties, 
				ResourceLoader resourceLoader,
				ObjectProvider<DataSource> dataSource, 
				@FlywayDataSource ObjectProvider<DataSource> flywayDataSource,
				ObjectProvider<FlywayConfigurationCustomizer> fluentConfigurationCustomizers,
				ObjectProvider<Callback> callbacks,
				ObjectProvider<FlywayFluentConfiguration> configurations) {
			this.properties = properties;
			this.dataSourceProperties = dataSourceProperties;
			this.resourceLoader = resourceLoader;
			this.dataSource = dataSource.getIfUnique();
			this.flywayDataSource = flywayDataSource.getIfAvailable();
			this.configurationCustomizers = fluentConfigurationCustomizers.orderedStream().collect(Collectors.toList());
			this.callbacks = callbacks;
			this.configurations = configurations.orderedStream().collect(Collectors.toList());
		}
		
		@Bean("flyways")
		public List<Flyway> flyways() {
			
			List<Flyway> flyways = new ArrayList<>();
			
			// 基于配置文件的多模块
			if(!CollectionUtils.isEmpty(this.properties.getModules())) {
				
				for (FlywayModularizedProperties properties : this.properties.getModules()) {
					
					FluentConfiguration configuration = new FluentConfiguration();
					DataSource dataSource = configureDataSource(properties, configuration);
					configureProperties(properties, configuration);
					checkLocationExists(properties, dataSource);
					
					List<Callback> orderedCallbacks = callbacks.orderedStream().collect(Collectors.toList());
					configureCallbacks(configuration, orderedCallbacks);
					
					this.configurationCustomizers.forEach((customizer) -> customizer.customize(configuration));
					flyways.add(configuration.load());
				}
				
			}
			
			// 基于Java配置对象的多模块
			if(!CollectionUtils.isEmpty(this.configurations)) {
				
				for (FlywayFluentConfiguration configuration : this.configurations) {
					
					configureDataSource(configuration);
					checkLocationExists(configuration);
					configureConfiguration(configuration);
					
					List<Callback> orderedCallbacks = callbacks.orderedStream().collect(Collectors.toList());
					configureCallbacks(configuration, orderedCallbacks);
					
					this.configurationCustomizers.forEach((customizer) -> customizer.customize(configuration));
					flyways.add(configuration.load());
				}
				
			}
			
			return flyways;
		}
		
		private DataSource configureDataSource(FlywayModularizedProperties properties, FluentConfiguration configuration) {
			if (properties.isCreateDataSource()) {
				String url = getProperty(properties::getUrl, this.dataSourceProperties::getUrl);
				String user = getProperty(properties::getUser, this.dataSourceProperties::getUsername);
				String password = getProperty(properties::getPassword, this.dataSourceProperties::getPassword);
				configuration.dataSource(url, user, password);
				if (!CollectionUtils.isEmpty(properties.getInitSqls())) {
					String initSql = StringUtils.collectionToDelimitedString(properties.getInitSqls(), "\n");
					configuration.initSql(initSql);
				}
			} else if (this.flywayDataSource != null) {
				configuration.dataSource(this.flywayDataSource);
			} else {
				configuration.dataSource(this.dataSource);
			}
			return configuration.getDataSource();
		}
		
		private DataSource configureDataSource(FluentConfiguration configuration) {
			// 没有初始化Datasource,则使用默认的Datasource
			if( null == configuration.getDataSource()) {
				if (this.flywayDataSource != null) {
					configuration.dataSource(this.flywayDataSource);
				} else {
					configuration.dataSource(this.dataSource);
				}
			}
			return configuration.getDataSource();
		}
			
		private void checkLocationExists(FlywayModularizedProperties properties, DataSource dataSource) {
			if (properties.isCheckLocation()) {
				String[] locations = new LocationVendorResolver(dataSource)
						.resolveLocations(properties.getLocations());
				Assert.state(locations.length != 0,
						"Migration script locations not configured");
				boolean exists = hasAtLeastOneLocation(locations);
				Assert.state(exists, () -> "Cannot find migrations location in: "
						+ Arrays.asList(locations)
						+ " (please add migrations or check your Flyway configuration)");
			}
		}
		
		private void checkLocationExists(FlywayFluentConfiguration configuration) {
			String[] locations = new LocationVendorResolver(configuration.getDataSource())
					.resolveLocations(configuration.getLocationAsStrings());
			Assert.state(locations.length != 0,
					"Migration script locations not configured");
			boolean exists = hasAtLeastOneLocation(locations);
			Assert.state(exists, () -> "Cannot find migrations location in: "
					+ Arrays.asList(locations)
					+ " (please add migrations or check your Flyway configuration)");
		}

		private void configureProperties(FlywayModularizedProperties properties, FluentConfiguration configuration) {
			PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
			String[] locations = new LocationVendorResolver(configuration.getDataSource())
					.resolveLocations(properties.getLocations());
			locations = new LocationModuleResolver(properties.getModule()).resolveLocations(locations);
			map.from(locations).to(configuration::locations);
			map.from(properties.getEncoding()).to(configuration::encoding);
			map.from(properties.getConnectRetries()).to(configuration::connectRetries);
			map.from(properties.getSchemas()).as(StringUtils::toStringArray).to(configuration::schemas);
			String table = new TableModuleResolver(properties.getModule()).resolveTable(properties.getTable());
			map.from(table).to(configuration::table);
			// No method reference for compatibility with Flyway 5.x
			map.from(properties.getTablespace()).whenNonNull().to((tablespace) -> configuration.tablespace(tablespace));
			map.from(properties.getBaselineDescription()).to(configuration::baselineDescription);
			map.from(properties.getBaselineVersion()).to(configuration::baselineVersion);
			map.from(properties.getInstalledBy()).to(configuration::installedBy);
			map.from(properties.getPlaceholders()).to(configuration::placeholders);
			map.from(properties.getPlaceholderPrefix()).to(configuration::placeholderPrefix);
			map.from(properties.getPlaceholderSuffix()).to(configuration::placeholderSuffix);
			map.from(properties.isPlaceholderReplacement()).to(configuration::placeholderReplacement);
			map.from(properties.getSqlMigrationPrefix()).to(configuration::sqlMigrationPrefix);
			map.from(properties.getSqlMigrationSuffixes()).as(StringUtils::toStringArray)
					.to(configuration::sqlMigrationSuffixes);
			map.from(properties.getSqlMigrationSeparator()).to(configuration::sqlMigrationSeparator);
			map.from(properties.getRepeatableSqlMigrationPrefix()).to(configuration::repeatableSqlMigrationPrefix);
			map.from(properties.getTarget()).to(configuration::target);
			map.from(properties.isBaselineOnMigrate()).to(configuration::baselineOnMigrate);
			map.from(properties.isCleanDisabled()).to(configuration::cleanDisabled);
			map.from(properties.isCleanOnValidationError()).to(configuration::cleanOnValidationError);
			map.from(properties.isGroup()).to(configuration::group);
			map.from(properties.isIgnoreMissingMigrations()).to(configuration::ignoreMissingMigrations);
			map.from(properties.isIgnoreIgnoredMigrations()).to(configuration::ignoreIgnoredMigrations);
			map.from(properties.isIgnorePendingMigrations()).to(configuration::ignorePendingMigrations);
			map.from(properties.isIgnoreFutureMigrations()).to(configuration::ignoreFutureMigrations);
			map.from(properties.isMixed()).to(configuration::mixed);
			map.from(properties.isOutOfOrder()).to(configuration::outOfOrder);
			map.from(properties.isSkipDefaultCallbacks()).to(configuration::skipDefaultCallbacks);
			map.from(properties.isSkipDefaultResolvers()).to(configuration::skipDefaultResolvers);
			map.from(properties.isValidateOnMigrate()).to(configuration::validateOnMigrate);
			// Pro properties
			map.from(properties.getBatch()).whenNonNull().to(configuration::batch);
			map.from(properties.getDryRunOutput()).whenNonNull().to(configuration::dryRunOutput);
			map.from(properties.getErrorOverrides()).whenNonNull().to(configuration::errorOverrides);
			map.from(properties.getLicenseKey()).whenNonNull().to(configuration::licenseKey);
			map.from(properties.getOracleSqlplus()).whenNonNull().to(configuration::oracleSqlplus);
			// No method reference for compatibility with Flyway 5.x
			map.from(properties.getOracleSqlplusWarn()).whenNonNull()
					.to((oracleSqlplusWarn) -> configuration.oracleSqlplusWarn(oracleSqlplusWarn));
			map.from(properties.getStream()).whenNonNull().to(configuration::stream);
			map.from(properties.getUndoSqlMigrationPrefix()).whenNonNull().to(configuration::undoSqlMigrationPrefix);
		}

		private void configureConfiguration(FlywayFluentConfiguration configuration) {

			String[] locations = new LocationVendorResolver(configuration.getDataSource())
					.resolveLocations(configuration.getLocationAsStrings());
			configuration.locations(locations);
			String table = new TableModuleResolver(configuration.getModule()).resolveTable(configuration.getTable());
			configuration.table(table);
			
		}
		
		private void configureCallbacks(FluentConfiguration configuration, List<Callback> callbacks) {
			if (!callbacks.isEmpty()) {
				configuration.callbacks(callbacks.toArray(new Callback[0]));
			}
		}
		
		private String getProperty(Supplier<String> property, Supplier<String> defaultValue) {
			String value = property.get();
			return (value != null) ? value : defaultValue.get();
		}

		private boolean hasAtLeastOneLocation(String... locations) {
			for (String location : locations) {
				if (this.resourceLoader.getResource(normalizePrefix(location)).exists()) {
					return true;
				}
			}
			return false;
		}

		private String normalizePrefix(String location) {
			return location.replace("filesystem:", "file:");
		}
		
		@Bean
		public FlywayModularizedMigrationInitializer flywayModuleInitializer(@Qualifier("flyways") List<Flyway> flyways,
				ObjectProvider<FlywayMigrationStrategy> migrationStrategy) {
			return new FlywayModularizedMigrationInitializer(flyways, migrationStrategy.getIfAvailable());
		}

		/**
		 * Additional configuration to ensure that EntityManagerFactory beans
		 * depend on the {@code flywayInitializer} bean.
		 */
		@Configuration
		@ConditionalOnClass(LocalContainerEntityManagerFactoryBean.class)
		@ConditionalOnBean(AbstractEntityManagerFactoryBean.class)
		protected static class FlywayInitializerJpaDependencyConfiguration
				extends EntityManagerFactoryDependsOnPostProcessor {

			public FlywayInitializerJpaDependencyConfiguration() {
				super("flywayModuleInitializer");
			}

		}

		/**
		 * Additional configuration to ensure that {@link JdbcOperations} beans depend on
		 * the {@code flywayInitializer} bean.
		 */
		@Configuration
		@ConditionalOnClass(JdbcOperations.class)
		@ConditionalOnBean(JdbcOperations.class)
		protected static class FlywayInitializerJdbcOperationsDependencyConfiguration
				extends JdbcOperationsDependsOnPostProcessor {

			public FlywayInitializerJdbcOperationsDependencyConfiguration() {
				super("flywayModuleInitializer");
			}

		}

	}
	
	/**
	 * Additional configuration to ensure that EntityManagerFactory beans depend
	 * on the {@code flyway} bean.
	 */
	@Configuration
	@ConditionalOnClass(LocalContainerEntityManagerFactoryBean.class)
	@ConditionalOnBean(AbstractEntityManagerFactoryBean.class)
	protected static class FlywayJpaDependencyConfiguration
			extends EntityManagerFactoryDependsOnPostProcessor {

		public FlywayJpaDependencyConfiguration() {
			super("flyways");
		}

	}

	/**
	 * Additional configuration to ensure that {@link JdbcOperations} beans depend on the
	 * {@code flyway} bean.
	 */
	@Configuration
	@ConditionalOnClass(JdbcOperations.class)
	@ConditionalOnBean(JdbcOperations.class)
	protected static class FlywayJdbcOperationsDependencyConfiguration
			extends JdbcOperationsDependsOnPostProcessor {

		public FlywayJdbcOperationsDependencyConfiguration() {
			super("flyways");
		}

	}
	
}