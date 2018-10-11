package org.flywaydb.spring.boot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.flywaydb.spring.boot.ext.FlywayModuleProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(FlywayMigrationProperties.PREFIX)
public class FlywayMigrationProperties {

	public static final String PREFIX = "spring.flyway";

	/**
	 * The locations of migrations scripts. Can contain the special "{vendor}" placeholder
	 * to use vendor-specific locations.
	 */
	private List<String> locations = new ArrayList<>(
			Collections.singletonList("classpath:db/migration"));

	/**
	 * Whether to check that migration scripts location exists.
	 */
	private boolean checkLocation = true;

	/**
	 * Whether to enable flyway.
	 */
	private boolean enabled = true;

	/**
	 * Login user of the database to migrate.
	 */
	private String user;

	/**
	 * JDBC password to use if you want Flyway to create its own DataSource.
	 */
	private String password;

	/**
	 * JDBC url of the database to migrate. If not set, the primary configured data source
	 * is used.
	 */
	private String url;

	/**
	 * SQL statements to execute to initialize a connection immediately after obtaining
	 * it.
	 */
	private List<String> initSqls = new ArrayList<>();
	
	private List<FlywayModuleProperties> modules = new ArrayList<>();

	public void setLocations(List<String> locations) {
		this.locations = locations;
	}

	public List<String> getLocations() {
		return this.locations;
	}

	public void setCheckLocation(boolean checkLocation) {
		this.checkLocation = checkLocation;
	}

	public boolean isCheckLocation() {
		return this.checkLocation;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return (this.password != null) ? this.password : "";
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<String> getInitSqls() {
		return this.initSqls;
	}

	public void setInitSqls(List<String> initSqls) {
		this.initSqls = initSqls;
	}

	public boolean isCreateDataSource() {
		return this.url != null || this.user != null;
	}
	
	public List<FlywayModuleProperties> getModules() {
		return modules;
	}

	public void setModules(List<FlywayModuleProperties> modules) {
		this.modules = modules;
	}

}