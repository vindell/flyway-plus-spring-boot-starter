package org.flywaydb.spring.boot;

import java.util.ArrayList;
import java.util.List;

import org.flywaydb.spring.boot.ext.FlywayModularizedProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(FlywayMigrationProperties.PREFIX)
public class FlywayMigrationProperties {

	public static final String PREFIX = "spring.flyway";

	private List<FlywayModularizedProperties> modules = new ArrayList<>();
	
	public List<FlywayModularizedProperties> getModules() {
		return modules;
	}

	public void setModules(List<FlywayModularizedProperties> modules) {
		this.modules = modules;
	}

}