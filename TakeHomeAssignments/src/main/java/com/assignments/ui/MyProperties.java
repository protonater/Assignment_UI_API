package com.assignments.ui;

import java.util.Properties;

public class MyProperties {
    private Properties properties = new Properties();

    public MyProperties() {
        try {
            this.properties.load(this.getClass().getResourceAsStream("/application.properties"));
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public String getProperty(String str) {
        return this.properties.getProperty(str);
    }
}