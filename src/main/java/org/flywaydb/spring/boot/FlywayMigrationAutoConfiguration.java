package org.flywaydb.spring.boot;

import java.util.Properties;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.spring.boot.ext.FlywayMigration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
@ConditionalOnClass(Flyway.class)
@ConditionalOnBean(DataSource.class)
@ConditionalOnProperty(prefix = "flyway.migration", name = "enabled", matchIfMissing = true)
@AutoConfigureAfter({ DataSourceAutoConfiguration.class, FlywayAutoConfiguration.class })
public class FlywayMigrationAutoConfiguration {
	
	@Bean
	@ConfigurationProperties(prefix = "flyway.migration")
	public FlywayMigration flywayMigration(Flyway flyway, FlywayMigrationProperties flywayProperties) {
		
		// SQL脚步占位符所使用的替换值来源，即供SQL脚步中使用${}占位符取值的Map
		flyway.setPlaceholders(flywayProperties.getPlaceholders());

		// 参数初始化
		Properties properties = flywayProperties.toProperties();
		// 初始化Flyway参数
		flyway.configure(properties);
		
		
		FlywayMigration flywayMigration = new FlywayMigration();
		
		flywayMigration.setClearMigrated(flywayProperties.getClearMigrated());
		flywayMigration.setIgnoreMigration(flywayProperties.getIgnoreMigration());
		flywayMigration.setRenameMigrated(flywayProperties.getRenameMigrated());
		flywayMigration.setSqlRenameSuffix(flywayProperties.getSqlRenameSuffix());
		
		return flywayMigration;
	}

}
