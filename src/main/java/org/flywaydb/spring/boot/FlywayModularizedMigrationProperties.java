package org.flywaydb.spring.boot;

import java.util.ArrayList;
import java.util.List;

import org.flywaydb.spring.boot.ext.FlywayModularizedProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(FlywayModularizedMigrationProperties.PREFIX)
public class FlywayModularizedMigrationProperties {

	public static final String PREFIX = "spring.flyway";

	private List<FlywayModularizedProperties> modules = new ArrayList<>();
	
	private boolean moduleable = false;
	
	public List<FlywayModularizedProperties> getModules() {
		return modules;
	}

	public void setModules(List<FlywayModularizedProperties> modules) {
		this.modules = modules;
	}

	public boolean isModuleable() {
		return moduleable;
	}

	public void setModuleable(boolean moduleable) {
		this.moduleable = moduleable;
	}

}