/*
 * Copyright 2011 Vincent Behar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rundeck.api;

import java.util.Properties;

/**
 * Builder for job options
 * 
 * @author Vincent Behar
 */
public class OptionsBuilder {

    private final Properties options;

    /**
     * Build a new instance. Use {@link #addOption(Object, Object)} to add some options, and then
     * {@link #toProperties()} when you're done !
     */
    public OptionsBuilder() {
        options = new Properties();
    }

    /**
     * Add an option
     * 
     * @param key of the option
     * @param value of the option
     * @return this, for method chaining
     */
    public OptionsBuilder addOption(Object key, Object value) {
        options.put(key, value);
        return this;
    }

    /**
     * @return a new {@link Properties} instance
     */
    public Properties toProperties() {
        Properties options = new Properties();
        options.putAll(this.options);
        return options;
    }

}
