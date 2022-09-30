package org.example;

import org.apache.commons.configuration.PropertiesConfiguration;

import javax.naming.ConfigurationException;

public class ApplicationConfig {
    private static String userName;
    private static String passWord;
    private static String host;

    private static final String DEVELOPER_APPLICATION_SETTINGS = "application-private.yml";
    private static final String APPLICATION_SETTINGS = "application.yml";

    public static void readConfig() throws ConfigurationException{
        PropertiesConfiguration config = new PropertiesConfiguration();
        boolean configFound = false;
        try {
            config.load(DEVELOPER_APPLICATION_SETTINGS);
            configFound = true;
            System.out.println("using " + DEVELOPER_APPLICATION_SETTINGS);
        } catch (org.apache.commons.configuration.ConfigurationException e) {

        }

        if(!configFound){
            try {
                config.load(APPLICATION_SETTINGS);
                System.out.println("using " + APPLICATION_SETTINGS);
            } catch (org.apache.commons.configuration.ConfigurationException e) {
                throw new ConfigurationException("could not load any application.yml");
            }
        }

        userName = (String)config.getProperty("userName");
        passWord  = (String)config.getProperty("passWord");
        host = (String)config.getProperty("host");

        if(userName == null || passWord == null || host == null) {
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
