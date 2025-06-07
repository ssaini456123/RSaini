package com.ssaini456123;

import com.ssaini456123.util.Config;

public class Driver {
    public static void main(String[] args) {
        Config c = new Config("Config.json");
        RSaini rSaini = new RSaini(c);
        rSaini.start();
    }
}