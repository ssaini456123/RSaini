package com.ssaini456123;

public class Main {

    private static final String CONFIG_FILE_NAME = "Config.json";

    public static void main(String[] args) {
        Config c = new Config(CONFIG_FILE_NAME);
        System.out.println(c.getBotPrefix());
        System.out.println(c.getBotToken());
    }
}
