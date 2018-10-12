package org.flywaydb.spring.boot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.flywaydb.core.internal.jdbc.DriverDataSource;
import org.flywaydb.spring.boot.ext.FlywayClassicConfiguration;
import org.flywaydb.spring.boot.ext.FlywayMigrationProvider;
import org.flywaydb.spring.boot.ext.FlywayModuleMigrationInitializer;
import org.flywaydb.spring.boot.ext.FlywayModuleProperties;
import org.flywaydb.spring.boot.ext.resolver.LocationModuleResolver;
import org.flywaydb.spring.boot.ext.resolver.LocationVendorResolver;
import org.flywaydb.spring.boot.ext.resolver.TableModuleResolver;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.jpa.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcOperationsDependsOnPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
@Configuration
@ConditionalOnClass(Flyway.class)
@ConditionalOnBean(DataSource.class)
@ConditionalOnProperty(prefix = FlywayMigrationProperties.PREFIX, name = "enabled", havingValue = "true")
@AutoConfigureAfter({ DataSourceAutoConfiguration.class,
	JdbcTemplateAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
/** 在主体数据库迁移之前完成各个模块的数据库迁移 */
@AutoConfigureBefore(FlywayAutoConfiguration.class)
public class FlywayMigrationAutoConfiguration{
	
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
	@ConditionalOnMissingBean(Flyway.class)
	@EnableConfigurationProperties({ DataSourceProperties.class, FlywayMigrationProperties.class })
	public static class FlywayModuleConfiguration {

		private final FlywayMigrationProperties properties;

		private final DataSourceProperties dataSourceProperties;

		private final ResourceLoader resourceLoader;

		private final DataSource dataSource;

		private final DataSource flywayDataSource;

		private final FlywayMigrationStrategy migrationStrategy;
		
		private List<FlywayClassicConfiguration> migrationConfiguration;

		private List<Callback> flywayCallbacks;

		public FlywayModuleConfiguration(FlywayMigrationProperties properties,
				DataSourceProperties dataSourceProperties, 
				ResourceLoader resourceLoader,
				ObjectProvider<DataSource> dataSource,
				@FlywayDataSource ObjectProvider<DataSource> flywayDataSource,
				ObjectProvider<FlywayMigrationStrategy> migrationStrategy,
				ObjectProvider<List<FlywayClassicConfiguration>> migrationConfiguration,
				ObjectProvider<List<Callback>> flywayCallbacks) {
			this.properties = properties;
			this.dataSourceProperties = dataSourceProperties;
			this.resourceLoader = resourceLoader;
			this.dataSource = dataSource.getIfUnique();
			this.flywayDataSource = flywayDataSource.getIfAvailable();
			this.migrationStrategy = migrationStrategy.getIfAvailable();
			this.migrationConfiguration = migrationConfiguration.getIfAvailable(Collections::emptyList);
			this.flywayCallbacks = flywayCallbacks.getIfAvailable(Collections::emptyList);
		}
		
		@Bean("flyways")
		public List<Flyway> flyways() {
			
			List<Flyway> flyways = new ArrayList<>();
			
			// 基于配置文件的多模块
			if(!CollectionUtils.isEmpty(properties.getModules())) {
				
				for (FlywayModuleProperties properties : properties.getModules()) {
					
					ClassicConfiguration configuration = new ClassicConfiguration();
					
					if (this.properties.isCreateDataSource()) {
						
						String url = getProperty(this.properties::getUrl, this.dataSourceProperties::getUrl);
						String user = getProperty(this.properties::getUser, this.dataSourceProperties::getUsername);
						String password = getProperty(this.properties::getPassword, this.dataSourceProperties::getPassword);
						
					    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
						configuration.setDataSource(new DriverDataSource(classLoader, null, url, user, password));
						configuration.setInitSql(this.properties.getInitSqls() == null ? null : StringUtils.collectionToDelimitedString(this.properties.getInitSqls(), "\n"));
						
					}
					else if (this.flywayDataSource != null) {
						configuration.setDataSource(this.flywayDataSource);
					}
					else {
						configuration.setDataSource(this.dataSource);
					}
					
					configuration.setCallbacks(this.flywayCallbacks.toArray(new Callback[0]));
					
					String[] moduleLocations = new LocationModuleResolver(properties.getModule())
							.resolveLocations(this.properties.getLocations());
					String[] locations = new LocationVendorResolver(configuration.getDataSource())
							.resolveLocations(moduleLocations);
					
					checkLocationExists(locations);
					
					configuration.setLocationsAsStrings(locations);
					
					String table = new TableModuleResolver(properties.getModule()).resolveTable(configuration.getTable());
					configuration.setTable(table);
					
					Flyway flyway = new Flyway(configuration);
					
					flyways.add(flyway);
				}
				
			}
			
			// 基于Java配置对象的多模块
			if(!CollectionUtils.isEmpty(this.migrationConfiguration)) {
				
				for (FlywayClassicConfiguration configuration : this.migrationConfiguration) {
					
					// 没有初始化Datasource,则使用默认的Datasource
					if( null == configuration.getDataSource()) {
						if(this.flywayDataSource != null) {
							configuration.setDataSource(this.flywayDataSource);
						} else {
							configuration.setDataSource(this.dataSource);
						}
					}
					
					configuration.setCallbacks(this.flywayCallbacks.toArray(new Callback[0]));
					
					String[] moduleLocations = new LocationModuleResolver(configuration.getModule())
							.resolveLocations(this.properties.getLocations());
					String[] locations = new LocationVendorResolver(configuration.getDataSource())
							.resolveLocations(moduleLocations);
					
					checkLocationExists(locations);
					
					configuration.setLocationsAsStrings(locations);
					
					String table = new TableModuleResolver(configuration.getModule()).resolveTable(configuration.getTable());
					configuration.setTable(table);
					
					Flyway flyway = new Flyway(configuration);
					
					flyways.add(flyway);
				}
				
			}
			
			
			return flyways;
		}

		private String getProperty(Supplier<String> property,
				Supplier<String> defaultValue) {
			String value = property.get();
			return (value != null) ? value : defaultValue.get();
		}

		private void checkLocationExists(String... locations) {
			if (this.properties.isCheckLocation()) {
				Assert.state(locations.length != 0,
						"Migration script locations not configured");
				boolean exists = hasAtLeastOneLocation(locations);
				Assert.state(exists, () -> "Cannot find migrations location in: "
						+ Arrays.asList(locations)
						+ " (please add migrations or check your Flyway configuration)");
			}
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
		public FlywayModuleMigrationInitializer flywayModuleInitializer(@Qualifier("flyways") List<Flyway> flyways) {
			return new FlywayModuleMigrationInitializer(flyways, this.migrationStrategy);
		}

		/**
		 * Additional configuration to ensure that {@link EntityManagerFactory} beans
		 * depend on the {@code flywayInitializer} bean.
		 */
		@Configuration
		@ConditionalOnClass(LocalContainerEntityManagerFactoryBean.class)
		@ConditionalOnBean(AbstractEntityManagerFactoryBean.class)
		protected static class FlywayInitializerJpaDependencyConfiguration
				extends EntityManagerFactoryDependsOnPostProcessor {

			public FlywayInitializerJpaDependencyConfiguration() {
				super("flywayInitializer", "flywayModuleInitializer");
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
				super("flywayInitializer", "flywayModuleInitializer");
			}

		}

	}
	
}