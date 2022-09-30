package org.example;

import org.apache.commons.configuration.PropertiesConfiguration;

import javax.naming.ConfigurationException;

public class ApplicationConfig {
    private static String userName;
    private static String passWord;
    private static String host;

    private static final String DEVELOPER_APPLICATION_SETTINGS = "application-private.properties";
    private static final String APPLICATION_SETTINGS = "application.properties";

    public static void readConfig() throws ConfigurationException{
        PropertiesConfiguration config = new PropertiesConfiguration();
        try {
            config.load(DEVELOPER_APPLICATION_SETTINGS);
        } catch (org.apache.commons.configuration.ConfigurationException e) {

        }

        try {
            config.load(APPLICATION_SETTINGS);
        } catch (org.apache.commons.configuration.ConfigurationException e) {
            throw new ConfigurationException("could not load any application.properties");
        }

        userName = config.getProperty("userName").toString();
        passWord = config.getProperty("passWord").toString();
        host = config.getProperty("host").toString();

        if(userName.equals("") || passWord.equals("") || host.equals("")) {
            throw new ConfigurationException("error: missing value for host, username or password in config file");
        }
    }

    public static String getUserName() {
        return userName;
    }

    public static String getPassWord() {
        return passWord;
    }

    public static String getHost() {
        return host;
    }
}
