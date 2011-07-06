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
import org.apache.commons.lang.StringUtils;

/**
 * Builder for node filters
 * 
 * @author Vincent Behar
 */
public class NodeFiltersBuilder {

    private final Properties filters;

    /**
     * Build a new instance. At the end, use {@link #toProperties()}.
     */
    public NodeFiltersBuilder() {
        filters = new Properties();
    }

    /**
     * Include nodes matching the given hostname
     * 
     * @param hostname
     * @return this, for method chaining
     * @see #excludeHostname(String)
     */
    public NodeFiltersBuilder hostname(String hostname) {
        if (StringUtils.isNotBlank(hostname)) {
            filters.put("hostname", hostname);
        }
        return this;
    }

    /**
     * Include nodes matching the given type
     * 
     * @param type
     * @return this, for method chaining
     * @see #excludeType(String)
     */
    public NodeFiltersBuilder type(String type) {
        if (StringUtils.isNotBlank(type)) {
            filters.put("type", type);
        }
        return this;
    }

    /**
     * Include nodes matching the given tags
     * 
     * @param tags
     * @return this, for method chaining
     * @see #excludeTags(String)
     */
    public NodeFiltersBuilder tags(String tags) {
        if (StringUtils.isNotBlank(tags)) {
            filters.put("tags", tags);
        }
        return this;
    }

    /**
     * Include nodes matching the given name
     * 
     * @param name
     * @return this, for method chaining
     * @see #excludeName(String)
     */
    public NodeFiltersBuilder name(String name) {
        if (StringUtils.isNotBlank(name)) {
            filters.put("name", name);
        }
        return this;
    }

    /**
     * Include nodes matching the given OS-name
     * 
     * @param osName
     * @return this, for method chaining
     * @see #excludeOsName(String)
     */
    public NodeFiltersBuilder osName(String osName) {
        if (StringUtils.isNotBlank(osName)) {
            filters.put("os-name", osName);
        }
        return this;
    }

    /**
     * Include nodes matching the given OS-family
     * 
     * @param osFamily
     * @return this, for method chaining
     * @see #excludeOsFamily(String)
     */
    public NodeFiltersBuilder osFamily(String osFamily) {
        if (StringUtils.isNotBlank(osFamily)) {
            filters.put("os-family", osFamily);
        }
        return this;
    }

    /**
     * Include nodes matching the given OS-arch
     * 
     * @param osArch
     * @return this, for method chaining
     * @see #excludeOsArch(String)
     */
    public NodeFiltersBuilder osArch(String osArch) {
        if (StringUtils.isNotBlank(osArch)) {
            filters.put("os-arch", osArch);
        }
        return this;
    }

    /**
     * Include nodes matching the given OS-version
     * 
     * @param osVersion
     * @return this, for method chaining
     * @see #excludeOsVersion(String)
     */
    public NodeFiltersBuilder osVersion(String osVersion) {
        if (StringUtils.isNotBlank(osVersion)) {
            filters.put("os-version", osVersion);
        }
        return this;
    }

    /**
     * Exclude nodes matching the given hostname
     * 
     * @param hostname
     * @return this, for method chaining
     * @see #hostname(String)
     * @see #excludePrecedence(boolean)
     */
    public NodeFiltersBuilder excludeHostname(String hostname) {
        if (StringUtils.isNotBlank(hostname)) {
            filters.put("exclude-hostname", hostname);
        }
        return this;
    }

    /**
     * Exclude nodes matching the given type
     * 
     * @param type
     * @return this, for method chaining
     * @see #type(String)
     * @see #excludePrecedence(boolean)
     */
    public NodeFiltersBuilder excludeType(String type) {
        if (StringUtils.isNotBlank(type)) {
            filters.put("exclude-type", type);
        }
        return this;
    }

    /**
     * Exclude nodes matching the given tags
     * 
     * @param tags
     * @return this, for method chaining
     * @see #tags(String)
     * @see #excludePrecedence(boolean)
     */
    public NodeFiltersBuilder excludeTags(String tags) {
        if (StringUtils.isNotBlank(tags)) {
            filters.put("exclude-tags", tags);
        }
        return this;
    }

    /**
     * Exclude nodes matching the given name
     * 
     * @param name
     * @return this, for method chaining
     * @see #name(String)
     * @see #excludePrecedence(boolean)
     */
    public NodeFiltersBuilder excludeName(String name) {
        if (StringUtils.isNotBlank(name)) {
            filters.put("exclude-name", name);
        }
        return this;
    }

    /**
     * Exclude nodes matching the given OS-name
     * 
     * @param osName
     * @return this, for method chaining
     * @see #osName(String)
     * @see #excludePrecedence(boolean)
     */
    public NodeFiltersBuilder excludeOsName(String osName) {
        if (StringUtils.isNotBlank(osName)) {
            filters.put("exclude-os-name", osName);
        }
        return this;
    }

    /**
     * Exclude nodes matching the given OS-family
     * 
     * @param osFamily
     * @return this, for method chaining
     * @see #osFamily(String)
     * @see #excludePrecedence(boolean)
     */
    public NodeFiltersBuilder excludeOsFamily(String osFamily) {
        if (StringUtils.isNotBlank(osFamily)) {
            filters.put("exclude-os-family", osFamily);
        }
        return this;
    }

    /**
     * Exclude nodes matching the given OS-arch
     * 
     * @param osArch
     * @return this, for method chaining
     * @see #osArch(String)
     * @see #excludePrecedence(boolean)
     */
    public NodeFiltersBuilder excludeOsArch(String osArch) {
        if (StringUtils.isNotBlank(osArch)) {
            filters.put("exclude-os-arch", osArch);
        }
        return this;
    }

    /**
     * Exclude nodes matching the given OS-version
     * 
     * @param osVersion
     * @return this, for method chaining
     * @see #osVersion(String)
     * @see #excludePrecedence(boolean)
     */
    public NodeFiltersBuilder excludeOsVersion(String osVersion) {
        if (StringUtils.isNotBlank(osVersion)) {
            filters.put("exclude-os-version", osVersion);
        }
        return this;
    }

    /**
     * Whether exclusion filters take precedence (default to yes).
     * 
     * @param excludePrecedence
     * @return this, for method chaining
     */
    public NodeFiltersBuilder excludePrecedence(boolean excludePrecedence) {
        filters.put("exclude-precedence", Boolean.toString(excludePrecedence));
        return this;
    }

    /**
     * @return a new {@link Properties} instance
     */
    public Properties toProperties() {
        Properties filters = new Properties();
        filters.putAll(this.filters);
        return filters;
    }

}
