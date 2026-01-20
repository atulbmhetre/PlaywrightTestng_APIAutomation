package com.api.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    static {
        properties = new Properties();
        try {
            String env = System.getProperty("env","qa");
            System.out.println("Environment to run API tests on : " + env);
            FileInputStream fis = new FileInputStream("./src/test/resources/"+ env +".config.properties");
            properties.load(fis);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Config.properties File not found exception occurred.");
        } catch (IOException e) {
            throw new RuntimeException("Config.properties File found but exception occurred while loading file.");
        }
    }

    public static String getProperties(String key) {
        return properties.getProperty(key);
    }
}
