package com.example.spring.singleton;

public class Settings {
    private static Settings instance;
    private String theme = "dark";

    private Settings() {

    }

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }

        return instance;
    }

    String getTheme() {
        return theme;
    }

    void setTheme(String theme) {
        this.theme = theme;
    }
}
