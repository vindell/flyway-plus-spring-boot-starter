package org.flywaydb.spring.boot;

import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(FlywayMigrationProperties.PREFIX)
public class FlywayMigrationProperties {

	public static final String PREFIX = "flyway";

	// org.flywaydb.core.Flyway 参数

	/**
	 * 在没有数据源的情况下，如果使用flyway需要指定数据库链接参数
	 */
	private String driver;

	/**
	 * Whether to automatically call validate or not when running migrate. (default:
	 * {@code true}) 在进行版本升级前是否进行SQL版本信息校验
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
    private boolean cleanOnValidationError = false;
    
	/**
	 * Whether to disable clean. (default: {@code false})
	 * <p>
	 * This is especially useful for production environments where running clean can
	 * be quite a career limiting move.
	 * </p>
	 * 禁止对数据库进行相关清除操作
	 */
	private boolean cleanDisabled = true;

	// org.flywaydb.spring.boot.ext.FlywayMigration 参数
	
	/**
	 * 是否使用安全模式：防止数据库因为配置不当导致的数据表被删除. (default: true)
	 */
	private boolean safeMode = true;
	/**
	 * Whether SQL should be migrated. (default: true)
	 */
	private boolean ignoreMigration = true;
	/**
	 * Whether SQL should be delete after migrated. (default: true)
	 */
	private boolean clearMigrated = false;
	/**
	 * Whether SQL should be Rename after migrated. (default: true)
	 */
	private boolean renameMigrated = false;
	/**
	 * The file name suffix for sql migrations after migrated. (default: .back)
	 */
	private String sqlRenameSuffix = ".back";

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
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

	public boolean isSafeMode() {
		return safeMode;
	}

	public void setSafeMode(boolean safeMode) {
		this.safeMode = safeMode;
	}

	public boolean isIgnoreMigration() {
		return ignoreMigration;
	}

	public void setIgnoreMigration(boolean ignoreMigration) {
		this.ignoreMigration = ignoreMigration;
	}

	public boolean isClearMigrated() {
		return clearMigrated;
	}

	public void setClearMigrated(boolean clearMigrated) {
		this.clearMigrated = clearMigrated;
	}

	public boolean isRenameMigrated() {
		return renameMigrated;
	}

	public void setRenameMigrated(boolean renameMigrated) {
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
		//notNullAdd(properties, "cleanDisabled", this.cleanDisabled);
		if(safeMode) {
			notNullAdd(properties, "cleanOnValidationError", false);
			notNullAdd(properties, "cleanDisabled", false);
		} else {
			notNullAdd(properties, "cleanOnValidationError", this.cleanOnValidationError);
			notNullAdd(properties, "cleanDisabled", this.cleanDisabled);
		}
		notNullAdd(properties, "validateOnMigrate", this.validateOnMigrate);
		return properties;
	}

	public void notNullAdd(Properties properties, String key, Object value) {
		if (value != null) {
			properties.setProperty("flyway." + key, value.toString());
		}
	}

}