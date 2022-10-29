package com.nphase.service;

import com.nphase.exception.ConfigPropertiesReadingException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigPropertiesService {

    private static final String CONFIG_PROPERTIES_FILENAME = "config.properties";

    private final Properties configProperties;

    public ConfigPropertiesService() {
        configProperties = new Properties();
        configProperties.putAll(System.getProperties());
    }

    public String getConfigProperty(String key) {
        return configProperties.getProperty(key);
    }

    private void loadProperties() {
        try {
            configProperties.load(new FileInputStream(CONFIG_PROPERTIES_FILENAME));
        } catch (IOException e) {
            throw new ConfigPropertiesReadingException("Error reading configuration properties", e);
        }
    }
}
