package com.ssaini456123;

import com.ssaini456123.util.Config;

public class Driver {

    private static final String CONFIG_FILE_NAME = "Config.json";

    public static void main(String[] args) {
        Config c = new Config(CONFIG_FILE_NAME);
        RSaini rSaini = new RSaini(c);
        rSaini.start();
    }
}