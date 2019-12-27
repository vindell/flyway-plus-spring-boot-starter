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
package org.flywaydb.spring.boot.ext.resolver;

import java.util.Arrays;
import java.util.Collection;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.util.StringUtils;

/**
 * TODO
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
public class LocationVendorResolver {
	
	private static final String VENDOR_PLACEHOLDER = "{vendor}";

	private final DataSource dataSource;

	public LocationVendorResolver(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String[] resolveLocations(Collection<String> locations) {
		return resolveLocations(StringUtils.toStringArray(locations));
	}

	public String[] resolveLocations(String[] locations) {
		if (usesVendorLocation(locations)) {
			DatabaseDriver databaseDriver = getDatabaseDriver();
			return replaceVendorLocations(locations, databaseDriver);
		}
		return locations;
	}

	private String[] replaceVendorLocations(String[] locations,
			DatabaseDriver databaseDriver) {
		if (databaseDriver == DatabaseDriver.UNKNOWN) {
			return locations;
		}
		String vendor = databaseDriver.getId();
		return Arrays.stream(locations)
				.map((location) -> location.replace(VENDOR_PLACEHOLDER, vendor))
				.toArray(String[]::new);
	}

	private DatabaseDriver getDatabaseDriver() {
		try {
			String url = JdbcUtils.extractDatabaseMetaData(this.dataSource, "getURL");
			return DatabaseDriver.fromJdbcUrl(url);
		}
		catch (MetaDataAccessException ex) {
			throw new IllegalStateException(ex);
		}

	}

	private boolean usesVendorLocation(String... locations) {
		for (String location : locations) {
			if (location.contains(VENDOR_PLACEHOLDER)) {
				return true;
			}
		}
		return false;
	}
	
}
