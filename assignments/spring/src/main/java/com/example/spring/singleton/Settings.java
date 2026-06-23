package com.example.spring.singleton;

public class Settings {
    private static Settings instance;
    private String theme = "dark";

    private Settings() {

    }

    public static synchronized Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }

        return instance;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
