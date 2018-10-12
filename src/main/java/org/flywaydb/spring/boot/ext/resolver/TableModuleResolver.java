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
package org.flywaydb.spring.boot.ext.resolver;

/**
 * TODO
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class TableModuleResolver {

	private static final String MODULE_PLACEHOLDER = "{module}";
	private final String module;
	
	public TableModuleResolver(String module) {
		this.module = module;
	}

	public String resolveTable(String table) {
		if (usesModuleTable(table)) {
			return table.replace(MODULE_PLACEHOLDER, module);
		}
		return table;
	}

	private boolean usesModuleTable(String table) {
		if (table.contains(MODULE_PLACEHOLDER)) {
			return true;
		}
		return false;
	}
	
}
