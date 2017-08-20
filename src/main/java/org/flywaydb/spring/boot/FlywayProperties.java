package org.flywaydb.spring.boot;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.datasource.flyway")
public class FlywayProperties {

	protected Boolean enabled = false;

	// org.flywaydb.core.Flyway 参数

	/**
	 * 在没有数据源的情况下，如果使用flyway需要指定数据库链接参数
	 */
	protected String driver;
	protected String url;
	protected String user;
	protected String password;

	/**
	 * The locations to scan recursively for migrations.
	 * <p/>
	 * <p>
	 * The location type is determined by its prefix. Unprefixed locations or
	 * locations starting with {@code classpath:} point to a package on the
	 * classpath and may contain both sql and java-based migrations. Locations
	 * starting with {@code filesystem:} point to a directory on the filesystem
	 * and may only contain sql migrations.
	 * </p>
	 * <p/>
	 * (default: db/migration)
	 */
	protected String locations = "db/migration";
	/**
	 * The encoding of Sql migrations. (default: UTF-8)
	 */
	protected String encoding = "UTF-8";
	/**
	 * The schemas managed by Flyway. These schema names are case-sensitive.
	 * (default: The default schema for the datasource connection)
	 * <p>
	 * Consequences:
	 * </p>
	 * <ul>
	 * <li>The first schema in the list will be automatically set as the default
	 * one during the migration.</li>
	 * <li>The first schema in the list will also be the one containing the
	 * metadata table.</li>
	 * <li>The schemas will be cleaned in the order of this list.</li>
	 * </ul>
	 */
	protected String[] schemaNames = new String[0];
	/**
	 * <p>
	 * The name of the schema metadata table that will be used by Flyway.
	 * (default: schema_version)
	 * </p>
	 * <p>
	 * By default (single-schema mode) the metadata table is placed in the
	 * default schema for the connection provided by the datasource.
	 * </p>
	 * <p>
	 * When the <i>flyway.schemas</i> property is set (multi-schema mode), the
	 * metadata table is placed in the first schema of the list.
	 * </p>
	 * 指定记录SQL版本的表名称
	 */
	protected String table = "schema_version";
	/**
	 * Whether placeholders should be replaced. (default: true)
	 */
	protected Boolean placeholderReplacement = true;
	/**
	 * The map of &lt;placeholder, replacementValue&gt; to apply to sql
	 * migration scripts. 
	 */
	protected Map<String, String> placeholders = new HashMap<String, String>();
	/**
	 * The prefix of every placeholder. (default: ${ )
	 */
	protected String placeholderPrefix = "${";

	/**
	 * The suffix of every placeholder. (default: } )
	 */
	protected String placeholderSuffix = "}";

	/**
	 * The file name prefix for sql migrations. (default: V)
	 * <p/>
	 * <p>
	 * Sql migrations have the following file name structure:
	 * prefixVERSIONseparatorDESCRIPTIONsuffix , which using the defaults
	 * translates to V1_1__My_description.sql
	 * </p>
	 */
	protected String sqlMigrationPrefix = "V";

	/**
	 * The file name prefix for repeatable sql migrations. (default: R)
	 * <p/>
	 * <p>
	 * Repeatable sql migrations have the following file name structure:
	 * prefixSeparatorDESCRIPTIONsuffix , which using the defaults translates to
	 * R__My_description.sql
	 * </p>
	 */
	protected String repeatableSqlMigrationPrefix = "R";

	/**
	 * The file name separator for sql migrations. (default: __)
	 * <p/>
	 * <p>
	 * Sql migrations have the following file name structure:
	 * prefixVERSIONseparatorDESCRIPTIONsuffix , which using the defaults
	 * translates to V1_1__My_description.sql
	 * </p>
	 */
	protected String sqlMigrationSeparator = "__";

	/**
	 * The file name suffix for sql migrations. (default: .sql)
	 * <p/>
	 * <p>
	 * Sql migrations have the following file name structure:
	 * prefixVERSIONseparatorDESCRIPTIONsuffix , which using the defaults
	 * translates to V1_1__My_description.sql
	 * </p>
	 */
	protected String sqlMigrationSuffix = ".sql";
	protected boolean ignoreMissingMigrations;
	protected boolean ignoreFutureMigrations = true;

	/**
	 * Whether to automatically call validate or not when running migrate.
	 * (default: {@code true}) 在进行版本升级前是否进行SQL版本信息校验
	 */
	protected Boolean validateOnMigrate = true;

	/**
	 * Whether to automatically call clean or not when a validation error
	 * occurs. (default: {@code false})
	 * <p>
	 * This is exclusively intended as a convenience for development. Even tough
	 * we strongly recommend not to change migration scripts once they have been
	 * checked into SCM and run, this provides a way of dealing with this case
	 * in a smooth manner. The database will be wiped clean automatically,
	 * ensuring that the next migration will bring you back to the state checked
	 * into SCM.
	 * </p>
	 * <p>
	 * <b>Warning ! Do not enable in production !</b>
	 * </p>
	 */
	protected boolean cleanOnValidationError = false;

	/**
	 * Whether to disable clean. (default: {@code false})
	 * <p>
	 * This is especially useful for production environments where running clean
	 * can be quite a career limiting move.
	 * </p>
	 * 禁止对数据库进行相关清除操作
	 */
	protected Boolean cleanDisabled = true;
	/**
	 * The description to tag an existing schema with when executing baseline.
	 * (default: &lt;&lt; Flyway Baseline &gt;&gt;)
	 */
	protected String baselineDescription = "<< Flyway Baseline >>";
	/**
	 * <p>
	 * Whether to automatically call baseline when migrate is executed against a
	 * non-empty schema with no metadata table. This schema will then be
	 * initialized with the {@code baselineVersion} before executing the
	 * migrations. Only migrations above {@code baselineVersion} will then be
	 * applied.
	 * </p>
	 * <p>
	 * This is useful for initial Flyway production deployments on projects with
	 * an existing DB.
	 * </p>
	 * <p>
	 * Be careful when enabling this as it removes the safety net that ensures
	 * Flyway does not migrate the wrong database in case of a configuration
	 * mistake! (default: {@code false})
	 * </p>
	 */
	protected Boolean baselineOnMigrate = true;
	/**
	 * 指定SQL起始版本信息
	 */
	protected String baselineVersion = "1.0.0";

	protected String target;
	/**
	 * 如果已经有版本1和3，当发现新的版本2时，如果此参数为true,将会把版本2作为当前版本
	 */
	protected Boolean outOfOrder = false;

	/**
	 * Whether Flyway should skip the default callbacks. If true, only custom
	 * callbacks are used.
	 * <p>
	 * (default: false)
	 * </p>
	 */
	protected boolean skipDefaultCallbacks;

	protected String callbacks;

	/**
	 * Whether Flyway should skip the default resolvers. If true, only custom
	 * resolvers are used.
	 * <p>
	 * (default: false)
	 * </p>
	 */
	protected boolean skipDefaultResolvers;

	protected String resolvers;

	/**
	 * Whether to allow mixing transactional and non-transactional statements
	 * within the same migration.
	 * <p>
	 * {@code true} if mixed migrations should be allowed. {@code false} if an
	 * error should be thrown instead. (default: {@code false})
	 */
	protected boolean mixed;

	/**
	 * Whether to group all pending migrations together in the same transaction
	 * when applying them (only recommended for databases with support for DDL
	 * transactions).
	 *
	 * {@code true} if migrations should be grouped. {@code false} if they
	 * should be applied individually instead. (default: {@code false})
	 */
	protected boolean group;

	/**
	 * The username that will be recorded in the metadata table as having
	 * applied the migration.
	 * <p>
	 * {@code null} for the current database user of the connection. (default:
	 * {@code null}).
	 */
	protected String installedBy;

	// org.flywaydb.spring.boot.ext.FlywayMigration 参数

	/**
	 * Whether SQL should be migrated. (default: true)
	 */
	protected Boolean ignoreMigration = true;
	/**
	 * Whether SQL should be delete after migrated. (default: true)
	 */
	protected Boolean clearMigrated = true;
	/**
	 * Whether SQL should be Rename after migrated. (default: true)
	 */
	protected Boolean renameMigrated = false;
	/**
	 * The file name suffix for sql migrations after migrated. (default: .back)
	 */
	protected String sqlRenameSuffix = ".back";

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLocations() {
		return locations;
	}

	public void setLocations(String locations) {
		this.locations = locations;
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

	public Boolean getPlaceholderReplacement() {
		return placeholderReplacement;
	}

	public void setPlaceholderReplacement(Boolean placeholderReplacement) {
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

	public String getSqlMigrationSuffix() {
		return sqlMigrationSuffix;
	}

	public void setSqlMigrationSuffix(String sqlMigrationSuffix) {
		this.sqlMigrationSuffix = sqlMigrationSuffix;
	}

	public boolean isIgnoreMissingMigrations() {
		return ignoreMissingMigrations;
	}

	public void setIgnoreMissingMigrations(boolean ignoreMissingMigrations) {
		this.ignoreMissingMigrations = ignoreMissingMigrations;
	}

	public boolean isIgnoreFutureMigrations() {
		return ignoreFutureMigrations;
	}

	public void setIgnoreFutureMigrations(boolean ignoreFutureMigrations) {
		this.ignoreFutureMigrations = ignoreFutureMigrations;
	}

	public Boolean getValidateOnMigrate() {
		return validateOnMigrate;
	}

	public void setValidateOnMigrate(Boolean validateOnMigrate) {
		this.validateOnMigrate = validateOnMigrate;
	}

	public boolean isCleanOnValidationError() {
		return cleanOnValidationError;
	}

	public void setCleanOnValidationError(boolean cleanOnValidationError) {
		this.cleanOnValidationError = cleanOnValidationError;
	}

	public Boolean getCleanDisabled() {
		return cleanDisabled;
	}

	public void setCleanDisabled(Boolean cleanDisabled) {
		this.cleanDisabled = cleanDisabled;
	}

	public String getBaselineDescription() {
		return baselineDescription;
	}

	public void setBaselineDescription(String baselineDescription) {
		this.baselineDescription = baselineDescription;
	}

	public Boolean getBaselineOnMigrate() {
		return baselineOnMigrate;
	}

	public void setBaselineOnMigrate(Boolean baselineOnMigrate) {
		this.baselineOnMigrate = baselineOnMigrate;
	}

	public String getBaselineVersion() {
		return baselineVersion;
	}

	public void setBaselineVersion(String baselineVersion) {
		this.baselineVersion = baselineVersion;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Boolean getOutOfOrder() {
		return outOfOrder;
	}

	public void setOutOfOrder(Boolean outOfOrder) {
		this.outOfOrder = outOfOrder;
	}

	public boolean isSkipDefaultCallbacks() {
		return skipDefaultCallbacks;
	}

	public void setSkipDefaultCallbacks(boolean skipDefaultCallbacks) {
		this.skipDefaultCallbacks = skipDefaultCallbacks;
	}

	public String getCallbacks() {
		return callbacks;
	}

	public void setCallbacks(String callbacks) {
		this.callbacks = callbacks;
	}

	public boolean isSkipDefaultResolvers() {
		return skipDefaultResolvers;
	}

	public void setSkipDefaultResolvers(boolean skipDefaultResolvers) {
		this.skipDefaultResolvers = skipDefaultResolvers;
	}

	public String getResolvers() {
		return resolvers;
	}

	public void setResolvers(String resolvers) {
		this.resolvers = resolvers;
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

	public Boolean getIgnoreMigration() {
		return ignoreMigration;
	}

	public void setIgnoreMigration(Boolean ignoreMigration) {
		this.ignoreMigration = ignoreMigration;
	}

	public Boolean getClearMigrated() {
		return clearMigrated;
	}

	public void setClearMigrated(Boolean clearMigrated) {
		this.clearMigrated = clearMigrated;
	}

	public Boolean getRenameMigrated() {
		return renameMigrated;
	}

	public void setRenameMigrated(Boolean renameMigrated) {
		this.renameMigrated = renameMigrated;
	}

	public String getSqlRenameSuffix() {
		return sqlRenameSuffix;
	}

	public void setSqlRenameSuffix(String sqlRenameSuffix) {
		this.sqlRenameSuffix = sqlRenameSuffix;
	}

	public Properties toProperties() {

		Properties properties = new Properties();

		notNullAdd(properties, "locations", this.locations);
		notNullAdd(properties, "placeholderReplacement", this.placeholderReplacement);
		notNullAdd(properties, "placeholderPrefix", this.placeholderPrefix);
		notNullAdd(properties, "placeholderSuffix", this.placeholderSuffix);
		notNullAdd(properties, "sqlMigrationPrefix", this.sqlMigrationPrefix);
		notNullAdd(properties, "repeatableSqlMigrationPrefix", this.repeatableSqlMigrationPrefix);
		notNullAdd(properties, "sqlMigrationSeparator", this.sqlMigrationSeparator);
		notNullAdd(properties, "sqlMigrationSuffix", this.sqlMigrationSuffix);

		notNullAdd(properties, "encoding", this.encoding);
		notNullAdd(properties, "schemas", this.schemaNames);
		notNullAdd(properties, "table", this.table);
		notNullAdd(properties, "cleanOnValidationError", this.cleanOnValidationError);
		notNullAdd(properties, "cleanDisabled", this.cleanDisabled);
		notNullAdd(properties, "validateOnMigrate", this.validateOnMigrate);
		notNullAdd(properties, "baselineVersion", this.baselineVersion);
		notNullAdd(properties, "baselineDescription", this.baselineDescription);
		notNullAdd(properties, "baselineOnMigrate", this.baselineOnMigrate);
		notNullAdd(properties, "ignoreMissingMigrations", this.ignoreMissingMigrations);
		notNullAdd(properties, "ignoreFutureMigrations", this.ignoreFutureMigrations);
		notNullAdd(properties, "outOfOrder", this.outOfOrder);

		notNullAdd(properties, "resolvers", this.resolvers);
		notNullAdd(properties, "skipDefaultResolvers", this.skipDefaultResolvers);
		notNullAdd(properties, "callbacks", this.callbacks);
		notNullAdd(properties, "skipDefaultCallbacks", this.skipDefaultCallbacks);

		notNullAdd(properties, "mixed", this.mixed);
		notNullAdd(properties, "group", this.group);
		notNullAdd(properties, "installedBy", this.installedBy);

		return properties;
	}

	public void notNullAdd(Properties properties, String key, Object value) {
		if (value != null) {
			properties.setProperty("flyway." + key, value.toString());
		}
	}
}