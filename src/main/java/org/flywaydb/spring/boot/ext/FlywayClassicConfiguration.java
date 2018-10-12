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
import java.util.Arrays;
import java.util.List;

import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.spring.boot.ext.resolver.LocationModuleResolver;

/**
 * 增加模块名称参数
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class FlywayClassicConfiguration extends ClassicConfiguration{

	/**
     * The module of Sql migrations. (default: module)
     */
	private String module = "module";
	/**
	 * The locations of migrations scripts.
	 */
	private List<String> locationAsStrings = new ArrayList<>();

    /**
     * Creates a new default configuration.
     * @param module The module of Sql migrations.
     */
    public FlywayClassicConfiguration(String module) {
    	super();
    	 this.module = module;
    }

    /**
     * Creates a new default configuration with this classloader.
     *
     * @param classLoader The ClassLoader to use for loading migrations, resolvers, etc from the classpath. (default: Thread.currentThread().getContextClassLoader() )
     * @param module The module of Sql migrations.
     */
    public FlywayClassicConfiguration(ClassLoader classLoader, String module) {
    	super(classLoader);
    	 this.module = module;
    }

    /**
     * Creates a new configuration with the same values as this existing one.
     *
     * @param configuration The configuration to use.
     * @param module The module of Sql migrations.
     */
    public FlywayClassicConfiguration(Configuration configuration, String module) {
        super(configuration);
        this.module = module;
    }
    
    @Override
    public void setLocationsAsStrings(String... locations) {
		String[] moduleLocations = new LocationModuleResolver(this.getModule())
				.resolveLocations(locations);
		this.locationAsStrings = Arrays.asList(moduleLocations);
    	super.setLocationsAsStrings(moduleLocations);
    }

	public String getModule() {
		return module;
	}

	public List<String> getLocationAsStrings() {
		return locationAsStrings;
	}
	
	
	
}
