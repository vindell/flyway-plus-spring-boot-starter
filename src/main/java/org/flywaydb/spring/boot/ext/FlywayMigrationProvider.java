package org.flywaydb.spring.boot.ext;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * TODO
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class FlywayMigrationProvider implements FlywayMigrationStrategy, ApplicationEventPublisherAware {
	
	protected static Logger LOG = LoggerFactory.getLogger(FlywayMigrationProvider.class);

	private ApplicationEventPublisher applicationEventPublisher;
	 
	@Override
	public void migrate(Flyway flyway) {
		
        try {
        	LOG.info("[Start] Flyway Migration run .. ");
        	// 执行migrate操作
			flyway.migrate();
			// 推送迁移完成后的通知
			getApplicationEventPublisher().publishEvent(new FlywayMigratedEvent(this));
			
			LOG.info("[End] Flyway Migration run .. ");
			
		} catch (Exception e) {
			LOG.error("Flyway Migrated Error . ", e);
		}
		
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	public ApplicationEventPublisher getApplicationEventPublisher() {
		return applicationEventPublisher;
	}
	
}
