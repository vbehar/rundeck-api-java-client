package org.rundeck.api.util;

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
