package org.flywaydb.spring.boot;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.spring.boot.ext.FlywayMigrationProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 
 * @className	： FlywayMigrationAutoConfiguration
 * @description	： TODO(描述这个类的作用)
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 * @date		： 2017年11月15日 下午1:09:21
 * @version 	V1.0
 * @see http://blog.csdn.net/tanghin/article/details/51264795
 */
@Configuration
@ConditionalOnClass(Flyway.class)
@ConditionalOnBean(DataSource.class)
@ConditionalOnProperty(prefix = FlywayMigrationProperties.PREFIX, name = "enabled", matchIfMissing = true)
@AutoConfigureBefore(FlywayAutoConfiguration.class)
@EnableConfigurationProperties({ FlywayMigrationProperties.class })
public class FlywayMigrationAutoConfiguration{

	@Bean
	@FlywayDataSource
	public DataSource flywayDatasource(DataSourceProperties properties) {
		return properties.initializeDataSourceBuilder().build();
	}
	
	@Bean
	public FlywayMigrationProvider flywayMigration(FlywayMigrationProperties properties) {
		return new FlywayMigrationProvider(properties);
	}
	
}