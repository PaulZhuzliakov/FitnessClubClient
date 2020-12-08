package org.demo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigInit {
    public static String getProperty(String propertyKey) {
        Properties prop = new Properties();
        String result = "";
        try (InputStream inputStream = ConfigInit.class
                .getClassLoader().getResourceAsStream("config.properties")) {
            prop.load(inputStream);
            result = prop.getProperty(propertyKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
