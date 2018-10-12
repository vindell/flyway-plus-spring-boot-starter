/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.StringUtils;

/**
 * Flyway模块化配置：locations和table参数特别需要注意，每个模块不能相同
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class FlywayModuleProperties {
	
	private static final String DEFAULT_FLYWAY_MODULE_PATH = "classpath:db/migration/{module}/{vendor}";
	
	/**
     * The module of Sql migrations. (default: module)
     */
	private String module = "module";
	
    /**
	 * The locations of migrations scripts. Can contain the special "{vendor}" placeholder
	 * to use vendor-specific locations.
	 */
	private List<String> locations = new ArrayList<>(Collections.singletonList(DEFAULT_FLYWAY_MODULE_PATH));

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
     * The maximum number of retries when attempting to connect to the database. After each failed attempt, Flyway will
     * wait 1 second before attempting to connect again, up to the maximum number of times specified by connectRetries.
     * (default: 0)
     */
    private int connectRetries;

    /**
	 * SQL statements to execute to initialize a connection immediately after obtaining
	 * it.
	 */
	private List<String> initSqls = new ArrayList<>();

    /**
     * The encoding of Sql migrations. (default: UTF-8)
     */
    private String encoding = "UTF-8";

    /**
     * The schemas managed by Flyway.  These schema names are case-sensitive. (default: The default schema for the datasource connection)
     * <p>Consequences:</p>
     * <ul>
     * <li>The first schema in the list will be automatically set as the default one during the migration.</li>
     * <li>The first schema in the list will also be the one containing the schema history table.</li>
     * <li>The schemas will be cleaned in the order of this list.</li>
     * </ul>
     */
    private String[] schemaNames = {};

    /**
     * <p>The name of the schema schema history table that will be used by Flyway. (default: flyway_module_schema_history)</p><p> By default
     * (single-schema mode) the schema history table is placed in the default schema for the connection provided by the
     * datasource. </p> <p> When the <i>flyway.schemas</i> property is set (multi-schema mode), the schema history table is
     * placed in the first schema of the list. </p>
     */
    private String table = "flyway_{module}_schema_history";

    /**
     * The target version up to which Flyway should consider migrations. Migrations with a higher version number will
     * be ignored. The special value {@code current} designates the current version of the schema (default: the latest version)
     */
    private String target;

    /**
     * Whether placeholders should be replaced. (default: true)
     */
    private boolean placeholderReplacement = true;

    /**
     * The map of &lt;placeholder, replacementValue&gt; to apply to sql migration scripts.
     */
    private Map<String, String> placeholders = new HashMap<>();

    /**
     * The prefix of every placeholder. (default: ${ )
     */
    private String placeholderPrefix = "${";

    /**
     * The suffix of every placeholder. (default: } )
     */
    private String placeholderSuffix = "}";

    /**
     * The file name prefix for versioned SQL migrations. (default: V)
     * <p>
     * <p>Versioned SQL migrations have the following file name structure: prefixVERSIONseparatorDESCRIPTIONsuffix ,
     * which using the defaults translates to V1_1__My_description.sql</p>
     */
    private String sqlMigrationPrefix = "V";


    /**
     * The file name prefix for repeatable SQL migrations. (default: R)
     * <p>
     * <p>Repeatable sql migrations have the following file name structure: prefixSeparatorDESCRIPTIONsuffix ,
     * which using the defaults translates to R__My_description.sql</p>
     */
    private String repeatableSqlMigrationPrefix = "R";

    /**
     * The file name separator for sql migrations. (default: __)
     * <p>
     * <p>Sql migrations have the following file name structure: prefixVERSIONseparatorDESCRIPTIONsuffix ,
     * which using the defaults translates to V1_1__My_description.sql</p>
     */
    private String sqlMigrationSeparator = "__";

    /**
     * The file name suffixes for SQL migrations. (default: .sql)
     * <p>SQL migrations have the following file name structure: prefixVERSIONseparatorDESCRIPTIONsuffix ,
     * which using the defaults translates to V1_1__My_description.sql</p>
     * <p>Multiple suffixes (like .sql,.pkg,.pkb) can be specified for easier compatibility with other tools such as
     * editors with specific file associations.</p>
     */
    private String[] sqlMigrationSuffixes = {".sql"};

    /**
     * Ignore missing migrations when reading the schema history table. These are migrations that were performed by an
     * older deployment of the application that are no longer available in this version. For example: we have migrations
     * available on the classpath with versions 1.0 and 3.0. The schema history table indicates that a migration with version 2.0
     * (unknown to us) has also been applied. Instead of bombing out (fail fast) with an exception, a
     * warning is logged and Flyway continues normally. This is useful for situations where one must be able to deploy
     * a newer version of the application even though it doesn't contain migrations included with an older one anymore.
     * Note that if the most recently applied migration is removed, Flyway has no way to know it is missing and will
     * mark it as future instead.
     * <p>
     * {@code true} to continue normally and log a warning, {@code false} to fail fast with an exception.
     * (default: {@code false})
     */
    private boolean ignoreMissingMigrations;

    /**
     * Ignore ignored migrations when reading the schema history table. These are migrations that were added in between
     * already migrated migrations in this version. For example: we have migrations available on the classpath with
     * versions from 1.0 to 3.0. The schema history table indicates that version 1 was finished on 1.0.15, and the next
     * one was 2.0.0. But with the next release a new migration was added to version 1: 1.0.16. Such scenario is ignored
     * by migrate command, but by default is rejected by validate. When ignoreIgnoredMigrations is enabled, such case
     * will not be reported by validate command. This is useful for situations where one must be able to deliver
     * complete set of migrations in a delivery package for multiple versions of the product, and allows for further
     * development of older versions.
     * <p>
     * {@code true} to continue normally, {@code false} to fail fast with an exception.
     * (default: {@code false})
     */
    private boolean ignoreIgnoredMigrations;

    /**
     * Ignore pending migrations when reading the schema history table. These are migrations that are available on the
     * classpath but have not yet been performed by an application deployment.
     * This can be useful for verifying that in-development migration changes don't contain any validation-breaking changes
     * of migrations that have already been applied to a production environment, e.g. as part of a CI/CD process, without
     * failing because of the existence of new migration versions.
     * <p>
     * {@code true} to continue normally, {@code false} to fail fast with an exception.
     * (default: {@code false})
     */
    private boolean ignorePendingMigrations;

    /**
     * Ignore future migrations when reading the schema history table. These are migrations that were performed by a
     * newer deployment of the application that are not yet available in this version. For example: we have migrations
     * available on the classpath up to version 3.0. The schema history table indicates that a migration to version 4.0
     * (unknown to us) has already been applied. Instead of bombing out (fail fast) with an exception, a
     * warning is logged and Flyway continues normally. This is useful for situations where one must be able to redeploy
     * an older version of the application after the database has been migrated by a newer one. (default: {@code true})
     */
    private boolean ignoreFutureMigrations = true;

    /**
     * Whether to automatically call validate or not when running migrate. (default: {@code true})
     */
    private boolean validateOnMigrate = true;

    /**
     * Whether to automatically call clean or not when a validation error occurs. (default: {@code false})
     * <p> This is exclusively intended as a convenience for development. Even tough we
     * strongly recommend not to change migration scripts once they have been checked into SCM and run, this provides a
     * way of dealing with this case in a smooth manner. The database will be wiped clean automatically, ensuring that
     * the next migration will bring you back to the state checked into SCM.</p>
     * <p><b>Warning ! Do not enable in production !</b></p>
     */
    private boolean cleanOnValidationError;

    /**
     * Whether to disable clean. (default: {@code false})
     * <p>This is especially useful for production environments where running clean can be quite a career limiting move.</p>
     */
    private boolean cleanDisabled;

    /**
     * The version to tag an existing schema with when executing baseline. (default: 1)
     */
    private String baselineVersion = "1";

    /**
     * The description to tag an existing schema with when executing baseline. (default: &lt;&lt; Flyway Baseline &gt;&gt;)
     */
    private String baselineDescription = "<< Flyway Baseline >>";

    /**
     * <p>
     * Whether to automatically call baseline when migrate is executed against a non-empty schema with no schema history table.
     * This schema will then be initialized with the {@code baselineVersion} before executing the migrations.
     * Only migrations above {@code baselineVersion} will then be applied.
     * </p>
     * <p>
     * This is useful for initial Flyway production deployments on projects with an existing DB.
     * </p>
     * <p>
     * Be careful when enabling this as it removes the safety net that ensures
     * Flyway does not migrate the wrong database in case of a configuration mistake! (default: {@code false})
     * </p>
     */
    private boolean baselineOnMigrate;

    /**
     * Allows migrations to be run "out of order".
     * <p>If you already have versions 1 and 3 applied, and now a version 2 is found,
     * it will be applied too instead of being ignored.</p>
     * <p>(default: {@code false})</p>
     */
    private boolean outOfOrder;

    /**
     * Whether Flyway should skip the default callbacks. If true, only custom callbacks are used.
     * <p>(default: false)</p>
     */
    private boolean skipDefaultCallbacks;

    /**
     * Whether Flyway should skip the default resolvers. If true, only custom resolvers are used.
     * <p>(default: false)</p>
     */
    private boolean skipDefaultResolvers;

    /**
     * Whether to allow mixing transactional and non-transactional statements within the same migration.
     * <p>
     * {@code true} if mixed migrations should be allowed. {@code false} if an error should be thrown instead. (default: {@code false})
     */
    private boolean mixed;

    /**
     * Whether to group all pending migrations together in the same transaction when applying them (only recommended for databases with support for DDL transactions).
     * <p>
     * {@code true} if migrations should be grouped. {@code false} if they should be applied individually instead. (default: {@code false})
     */
    private boolean group;
    
    /**
     * The username that will be recorded in the schema history table as having applied the migration.
     * <p>
     * {@code null} for the current database user of the connection. (default: {@code null}).
     */
    private String installedBy;

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public List<String> getLocations() {
		return locations;
	}

	public void setLocations(List<String> locations) {
		this.locations = locations;
	}

	public boolean isCheckLocation() {
		return checkLocation;
	}

	public void setCheckLocation(boolean checkLocation) {
		this.checkLocation = checkLocation;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getUser() {
		return user;
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
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getConnectRetries() {
		return connectRetries;
	}

	public void setConnectRetries(int connectRetries) {
		this.connectRetries = connectRetries;
	}

	public List<String> getInitSqls() {
		return initSqls;
	}

	public void setInitSqls(List<String> initSqls) {
		this.initSqls = initSqls;
	}
	
	public boolean isCreateDataSource() {
		return this.url != null || this.user != null;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String[] getSchemaNames() {
		return schemaNames;
	}

	public void setSchemaNames(String[] schemaNames) {
		this.schemaNames = schemaNames;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public boolean isPlaceholderReplacement() {
		return placeholderReplacement;
	}

	public void setPlaceholderReplacement(boolean placeholderReplacement) {
		this.placeholderReplacement = placeholderReplacement;
	}

	public Map<String, String> getPlaceholders() {
		return placeholders;
	}

	public void setPlaceholders(Map<String, String> placeholders) {
		this.placeholders = placeholders;
	}

	public String getPlaceholderPrefix() {
		return placeholderPrefix;
	}

	public void setPlaceholderPrefix(String placeholderPrefix) {
		this.placeholderPrefix = placeholderPrefix;
	}

	public String getPlaceholderSuffix() {
		return placeholderSuffix;
	}

	public void setPlaceholderSuffix(String placeholderSuffix) {
		this.placeholderSuffix = placeholderSuffix;
	}

	public String getSqlMigrationPrefix() {
		return sqlMigrationPrefix;
	}

	public void setSqlMigrationPrefix(String sqlMigrationPrefix) {
		this.sqlMigrationPrefix = sqlMigrationPrefix;
	}

	public String getRepeatableSqlMigrationPrefix() {
		return repeatableSqlMigrationPrefix;
	}

	public void setRepeatableSqlMigrationPrefix(String repeatableSqlMigrationPrefix) {
		this.repeatableSqlMigrationPrefix = repeatableSqlMigrationPrefix;
	}

	public String getSqlMigrationSeparator() {
		return sqlMigrationSeparator;
	}

	public void setSqlMigrationSeparator(String sqlMigrationSeparator) {
		this.sqlMigrationSeparator = sqlMigrationSeparator;
	}

	public String[] getSqlMigrationSuffixes() {
		return sqlMigrationSuffixes;
	}

	public void setSqlMigrationSuffixes(String[] sqlMigrationSuffixes) {
		this.sqlMigrationSuffixes = sqlMigrationSuffixes;
	}

	public boolean isIgnoreMissingMigrations() {
		return ignoreMissingMigrations;
	}

	public void setIgnoreMissingMigrations(boolean ignoreMissingMigrations) {
		this.ignoreMissingMigrations = ignoreMissingMigrations;
	}

	public boolean isIgnoreIgnoredMigrations() {
		return ignoreIgnoredMigrations;
	}

	public void setIgnoreIgnoredMigrations(boolean ignoreIgnoredMigrations) {
		this.ignoreIgnoredMigrations = ignoreIgnoredMigrations;
	}

	public boolean isIgnorePendingMigrations() {
		return ignorePendingMigrations;
	}

	public void setIgnorePendingMigrations(boolean ignorePendingMigrations) {
		this.ignorePendingMigrations = ignorePendingMigrations;
	}

	public boolean isIgnoreFutureMigrations() {
		return ignoreFutureMigrations;
	}

	public void setIgnoreFutureMigrations(boolean ignoreFutureMigrations) {
		this.ignoreFutureMigrations = ignoreFutureMigrations;
	}

	public boolean isValidateOnMigrate() {
		return validateOnMigrate;
	}

	public void setValidateOnMigrate(boolean validateOnMigrate) {
		this.validateOnMigrate = validateOnMigrate;
	}

	public boolean isCleanOnValidationError() {
		return cleanOnValidationError;
	}

	public void setCleanOnValidationError(boolean cleanOnValidationError) {
		this.cleanOnValidationError = cleanOnValidationError;
	}

	public boolean isCleanDisabled() {
		return cleanDisabled;
	}

	public void setCleanDisabled(boolean cleanDisabled) {
		this.cleanDisabled = cleanDisabled;
	}

	public String getBaselineVersion() {
		return baselineVersion;
	}

	public void setBaselineVersion(String baselineVersion) {
		this.baselineVersion = baselineVersion;
	}

	public String getBaselineDescription() {
		return baselineDescription;
	}

	public void setBaselineDescription(String baselineDescription) {
		this.baselineDescription = baselineDescription;
	}

	public boolean isBaselineOnMigrate() {
		return baselineOnMigrate;
	}

	public void setBaselineOnMigrate(boolean baselineOnMigrate) {
		this.baselineOnMigrate = baselineOnMigrate;
	}

	public boolean isOutOfOrder() {
		return outOfOrder;
	}

	public void setOutOfOrder(boolean outOfOrder) {
		this.outOfOrder = outOfOrder;
	}

	public boolean isSkipDefaultCallbacks() {
		return skipDefaultCallbacks;
	}

	public void setSkipDefaultCallbacks(boolean skipDefaultCallbacks) {
		this.skipDefaultCallbacks = skipDefaultCallbacks;
	}

	public boolean isSkipDefaultResolvers() {
		return skipDefaultResolvers;
	}

	public void setSkipDefaultResolvers(boolean skipDefaultResolvers) {
		this.skipDefaultResolvers = skipDefaultResolvers;
	}

	public boolean isMixed() {
		return mixed;
	}

	public void setMixed(boolean mixed) {
		this.mixed = mixed;
	}

	public boolean isGroup() {
		return group;
	}

	public void setGroup(boolean group) {
		this.group = group;
	}

	public String getInstalledBy() {
		return installedBy;
	}

	public void setInstalledBy(String installedBy) {
		this.installedBy = installedBy;
	}
	
	public FlywayClassicConfiguration toConfiguration() {
		
		// 构建模块初始化配置
		FlywayClassicConfiguration configuration = new FlywayClassicConfiguration(this.getModule());
		
		if(StringUtils.hasText(this.getBaselineDescription())) {
			configuration.setBaselineDescription(this.getBaselineDescription());
		}
		configuration.setBaselineOnMigrate(this.isBaselineOnMigrate());
		if(StringUtils.hasText(this.getBaselineVersion())) {
			configuration.setBaselineVersionAsString(this.getBaselineVersion());
		}
		configuration.setCleanDisabled(this.isCleanDisabled());
		configuration.setCleanOnValidationError(this.isCleanOnValidationError());
		configuration.setConnectRetries(this.getConnectRetries());
		if(StringUtils.hasText(this.getEncoding())) {
			configuration.setEncodingAsString(this.getEncoding());
		}
		configuration.setGroup(this.isGroup());
		configuration.setIgnoreFutureMigrations(this.isIgnoreFutureMigrations());
		configuration.setIgnoreIgnoredMigrations(this.isIgnoreIgnoredMigrations());
		configuration.setIgnoreMissingMigrations(this.isIgnoreMissingMigrations());
		configuration.setIgnorePendingMigrations(this.isIgnorePendingMigrations());
		if(StringUtils.hasText(this.getInstalledBy())) {
			configuration.setInstalledBy(this.getInstalledBy());
		}
		configuration.setMixed(this.isMixed());
		configuration.setOutOfOrder(this.isOutOfOrder());
		
		if(StringUtils.hasText(this.getPlaceholderPrefix()) && StringUtils.hasText(this.getPlaceholderSuffix())) {
			configuration.setPlaceholderPrefix(this.getPlaceholderPrefix());
			configuration.setPlaceholderReplacement(this.isPlaceholderReplacement());
			configuration.setPlaceholders(this.getPlaceholders());
			configuration.setPlaceholderSuffix(this.getPlaceholderSuffix());
		}
		if(StringUtils.hasText(this.getRepeatableSqlMigrationPrefix())) {
			configuration.setRepeatableSqlMigrationPrefix(this.getRepeatableSqlMigrationPrefix());
		}
		if(ArrayUtils.isNotEmpty(this.getSchemaNames())) {
			configuration.setSchemas(this.getSchemaNames());
		}
		configuration.setSkipDefaultCallbacks(this.isSkipDefaultCallbacks());
		configuration.setSkipDefaultResolvers(this.isSkipDefaultResolvers());
		if(StringUtils.hasText(this.getRepeatableSqlMigrationPrefix())) {
			configuration.setSqlMigrationPrefix(this.getRepeatableSqlMigrationPrefix());
		}
		if(StringUtils.hasText(this.getSqlMigrationSeparator())) {
			configuration.setSqlMigrationSeparator(this.getSqlMigrationSeparator());
		}
		if(ArrayUtils.isNotEmpty(this.getSqlMigrationSuffixes())) {
			configuration.setSqlMigrationSuffixes(this.getSqlMigrationSuffixes());
		}
		if(StringUtils.hasText(this.getTable())) {
			configuration.setTable(this.getTable());
		}
		if(StringUtils.hasText(this.getTarget())) {
			configuration.setTargetAsString(this.getTarget());
		}
		configuration.setValidateOnMigrate(this.isValidateOnMigrate());
		
		return configuration;
	}
	
}
