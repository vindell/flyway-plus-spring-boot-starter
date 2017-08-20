package org.flywaydb.spring.boot;

import java.util.Properties;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.spring.boot.ext.FlywayMigration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({ Flyway.class })
@ConditionalOnProperty(name = { "spring.datasource.flyway.enable" }, havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(FlywayProperties.class)
public class FlywayConfiguration {

	@Bean
	public Flyway flyway(DataSource dataSource, FlywayProperties flywayProperties) {

		Flyway flyway = new Flyway();

		// SQL脚步占位符所使用的替换值来源，即供SQL脚步中使用${}占位符取值的Map
		flyway.setPlaceholders(flywayProperties.getPlaceholders());

		// 参数初始化
		Properties properties = flywayProperties.toProperties();

		if (dataSource == null) {
			flywayProperties.notNullAdd(properties, "driver", flywayProperties.getDriver());
			flywayProperties.notNullAdd(properties, "url", flywayProperties.getUrl());
			flywayProperties.notNullAdd(properties, "user", flywayProperties.getUser());
			flywayProperties.notNullAdd(properties, "password", flywayProperties.getPassword());
		} else {
			flyway.setDataSource(dataSource);
		}
		// 初始化Flyway参数
		flyway.configure(properties);

		return flyway;
	}

	@Bean
	public FlywayMigration flywayMigration(Flyway flyway, FlywayProperties flywayProperties) {

		FlywayMigration flywayMigration = new FlywayMigration();
		
		flywayMigration.setFlyway(flyway);
		flywayMigration.setClearMigrated(flywayProperties.getClearMigrated());
		flywayMigration.setIgnoreMigration(flywayProperties.getIgnoreMigration());
		flywayMigration.setRenameMigrated(flywayProperties.getRenameMigrated());
		flywayMigration.setSqlRenameSuffix(flywayProperties.getSqlRenameSuffix());
		
		return flywayMigration;
	}

}
