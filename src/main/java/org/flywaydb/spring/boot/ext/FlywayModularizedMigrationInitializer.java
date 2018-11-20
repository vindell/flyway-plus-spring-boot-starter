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

import java.util.List;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

/**
 * TODO
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class FlywayModularizedMigrationInitializer implements InitializingBean, Ordered {

	private final List<Flyway> flyways;

	private final FlywayMigrationStrategy migrationStrategy;

	private int order = 0;

	/**
	 * Create a new {@link FlywayModularizedMigrationInitializer} instance.
	 * @param flyways the flyway list instance
	 */
	public FlywayModularizedMigrationInitializer(List<Flyway> flyways) {
		this(flyways, null);
	}

	/**
	 * Create a new {@link FlywayModularizedMigrationInitializer} instance.
	 * @param flyways the flyway list instance
	 * @param migrationStrategy the migration strategy or {@code null}
	 */
	public FlywayModularizedMigrationInitializer(List<Flyway> flyways,
			FlywayMigrationStrategy migrationStrategy) {
		Assert.notNull(flyways, "Flyways must not be null");
		this.flyways = flyways;
		this.migrationStrategy = migrationStrategy;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.migrationStrategy != null) {
			for (Flyway flyway : flyways) {
				this.migrationStrategy.migrate(flyway);
			}
		}
		else {
			for (Flyway flyway : flyways) {
				flyway.migrate();
			}
		}
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	
}
