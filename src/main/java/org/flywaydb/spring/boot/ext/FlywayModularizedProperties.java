/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.flywaydb.spring.boot.ext;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.convert.DurationUnit;

/**
 * Flyway模块化配置：locations和table参数特别需要注意，每个模块不能相同
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Getter
@Setter
@ToString
public class FlywayModularizedProperties {
	
	private static final String DEFAULT_FLYWAY_MODULE_PATH = "classpath:db/migration/{module}/{vendor}";
	private static final String DEFAULT_FLYWAY_MODULE_TABLE = "flyway_{module}_schema_history";
	
	/**
     * The module of Sql migrations. (default: module)
     */
	private String module = "module";

	/**
	 * Whether to enable flyway module supports.
	 */
	private boolean enabled = true;

    /**
	 * Whether to check that migration scripts location exists.
	 */
	private boolean checkLocation = true;

	/**
	 * Whether to fail if a location of migration scripts doesn't exist.
	 */
	private boolean failOnMissingLocations;

	/**
	 * Locations of migrations scripts. Can contain the special "{vendor}" placeholder to
	 * use vendor-specific locations.
	 */
	private List<String> locations = new ArrayList<>(Collections.singletonList(DEFAULT_FLYWAY_MODULE_PATH));

	/**
	 * Encoding of SQL migrations.
	 */
	private Charset encoding = StandardCharsets.UTF_8;

	/**
	 * Maximum number of retries when attempting to connect to the database.
	 */
	private int connectRetries;

	/**
	 * Maximum time between retries when attempting to connect to the database. If a
	 * duration suffix is not specified, seconds will be used.
	 */
	@DurationUnit(ChronoUnit.SECONDS)
	private Duration connectRetriesInterval = Duration.ofSeconds(120);

	/**
	 * Maximum number of retries when trying to obtain a lock.
	 */
	private int lockRetryCount = 50;

	/**
	 * Default schema name managed by Flyway (case-sensitive).
	 */
	private String defaultSchema;

	/**
	 * Scheme names managed by Flyway (case-sensitive).
	 */
	private List<String> schemas = new ArrayList<>();

	/**
	 * Whether Flyway should attempt to create the schemas specified in the schemas
	 * property.
	 */
	private boolean createSchemas = true;

	/**
	 * Name of the schema history table that will be used by Flyway.
	 */
	private String table = DEFAULT_FLYWAY_MODULE_TABLE;

	/**
	 * Tablespace in which the schema history table is created. Ignored when using a
	 * database that does not support tablespaces. Defaults to the default tablespace of
	 * the connection used by Flyway.
	 */
	private String tablespace;

	/**
	 * Description to tag an existing schema with when applying a baseline.
	 */
	private String baselineDescription = "<< Flyway Modularized Baseline >>";

	/**
	 * Version to tag an existing schema with when executing baseline.
	 */
	private String baselineVersion = "1";

	/**
	 * Username recorded in the schema history table as having applied the migration.
	 */
	private String installedBy;

	/**
	 * Placeholders and their replacements to apply to sql migration scripts.
	 */
	private Map<String, String> placeholders = new HashMap<>();

	/**
	 * Prefix of placeholders in migration scripts.
	 */
	private String placeholderPrefix = "${";

	/**
	 * Suffix of placeholders in migration scripts.
	 */
	private String placeholderSuffix = "}";

	/**
	 * Separator of default placeholders.
	 */
	private String placeholderSeparator = ":";

	/**
	 * Perform placeholder replacement in migration scripts.
	 */
	private boolean placeholderReplacement = true;

	/**
	 * File name prefix for SQL migrations.
	 */
	private String sqlMigrationPrefix = "V";

	/**
	 * File name suffix for SQL migrations.
	 */
	private List<String> sqlMigrationSuffixes = new ArrayList<>(Collections.singleton(".sql"));

	/**
	 * File name separator for SQL migrations.
	 */
	private String sqlMigrationSeparator = "__";

	/**
	 * File name prefix for repeatable SQL migrations.
	 */
	private String repeatableSqlMigrationPrefix = "R";

	/**
	 * Target version up to which migrations should be considered.
	 */
	private String target = "latest";

	/**
	 * Login user of the database to migrate.
	 */
	private String user;

	/**
	 * Login password of the database to migrate.
	 */
	private String password;

	/**
	 * Fully qualified name of the JDBC driver. Auto-detected based on the URL by default.
	 */
	private String driverClassName;

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

	/**
	 * Whether to automatically call baseline when migrating a non-empty schema.
	 */
	private boolean baselineOnMigrate;

	/**
	 * Whether to disable cleaning of the database.
	 */
	private boolean cleanDisabled = true;

	/**
	 * Whether to automatically call clean when a validation error occurs.
	 */
	private boolean cleanOnValidationError;

	/**
	 * Whether to group all pending migrations together in the same transaction when
	 * applying them.
	 */
	private boolean group;

	/**
	 * Whether to allow mixing transactional and non-transactional statements within the
	 * same migration.
	 */
	private boolean mixed;

	/**
	 * Whether to allow migrations to be run out of order.
	 */
	private boolean outOfOrder;

	/**
	 * Whether to skip default callbacks. If true, only custom callbacks are used.
	 */
	private boolean skipDefaultCallbacks;

	/**
	 * Whether to skip default resolvers. If true, only custom resolvers are used.
	 */
	private boolean skipDefaultResolvers;

	/**
	 * Whether to validate migrations and callbacks whose scripts do not obey the correct
	 * naming convention.
	 */
	private boolean validateMigrationNaming = false;

	/**
	 * Whether to automatically call validate when performing a migration.
	 */
	private boolean validateOnMigrate = true;

	/**
	 * Prefix of placeholders in migration scripts.
	 */
	private String scriptPlaceholderPrefix = "FP__";

	/**
	 * Suffix of placeholders in migration scripts.
	 */
	private String scriptPlaceholderSuffix = "__";

	/**
	 * Whether Flyway should execute SQL within a transaction.
	 */
	private boolean executeInTransaction = true;

	/**
	 * Loggers Flyway should use.
	 */
	private String[] loggers = { "slf4j" };

	/**
	 * Whether to batch SQL statements when executing them. Requires Flyway Teams.
	 */
	private Boolean batch;

	/**
	 * File to which the SQL statements of a migration dry run should be output. Requires
	 * Flyway Teams.
	 */
	private File dryRunOutput;

	/**
	 * Rules for the built-in error handling to override specific SQL states and error
	 * codes. Requires Flyway Teams.
	 */
	private String[] errorOverrides;

	/**
	 * Whether to stream SQL migrations when executing them. Requires Flyway Teams.
	 */
	private Boolean stream;

	/**
	 * Properties to pass to the JDBC driver. Requires Flyway Teams.
	 */
	private Map<String, String> jdbcProperties = new HashMap<>();

	/**
	 * Path of the Kerberos config file. Requires Flyway Teams.
	 */
	private String kerberosConfigFile;

	/**
	 * Whether Flyway should output a table with the results of queries when executing
	 * migrations. Requires Flyway Teams.
	 */
	private Boolean outputQueryResults;

	/**
	 * Whether Flyway should skip executing the contents of the migrations and only update
	 * the schema history table. Requires Flyway teams.
	 */
	private Boolean skipExecutingMigrations;

	/**
	 * Ignore migrations that match this comma-separated list of patterns when validating
	 * migrations. Requires Flyway Teams.
	 */
	private List<String> ignoreMigrationPatterns;

	/**
	 * Whether to attempt to automatically detect SQL migration file encoding. Requires
	 * Flyway Teams.
	 */
	private Boolean detectEncoding;

	private final FlywayProperties.Oracle oracle = new FlywayProperties.Oracle();

	private final FlywayProperties.Postgresql postgresql = new FlywayProperties.Postgresql();

	private final FlywayProperties.Sqlserver sqlserver = new FlywayProperties.Sqlserver();

	public boolean isCreateDataSource() {
		return this.url != null || this.user != null;
	}
	
}
