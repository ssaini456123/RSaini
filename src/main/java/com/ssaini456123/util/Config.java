package com.ssaini456123.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Sutinder S. Saini
 */
public class Config {
    private String botToken;
    private String botPrefix;

    public Config(String fileName) {
        File f = new File(fileName);
        loadFromFile(f);
    }

    private void loadFromFile(File file){
        if (!file.exists()) {
            System.out.println("Config file does not exist! Creating...");
            try {
                Path configPath = Path.of(file.getPath());
                Files.createFile(configPath);

                System.out.println("Config file created. Press <ENTER> and fill the config file in.");
                System.in.read();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Gson g = new Gson();

        try (FileReader reader = new FileReader(file)) {
            Config loaded = g.fromJson(reader, Config.class);

            this.botToken = loaded.getBotToken();
            this.botPrefix = loaded.getBotPrefix();
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    public String getBotPrefix() {
        return botPrefix;
    }

    public void setBotPrefix(String botPrefix) {
        this.botPrefix = botPrefix;
    }

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String toString() {
        String botToken = this.getBotToken();
        String botPrefix = this.getBotPrefix();
        return String.format("Config { \"token\": \"%s\", \"prefix\": \"%s\" }", botToken, botPrefix);
    }
}