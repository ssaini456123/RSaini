package com.ssaini456123.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Sutinder S. Saini
 */
public class Config {
    @Getter
    @Setter
    private String botToken;

    @Getter
    @Setter
    private String botPrefix;

    @Getter
    @Setter
    private String jdbcUrl;

    @Getter
    @Setter
    private String configName;

    public Config(String fileName) {
        File f = new File(fileName);
        this.configName = fileName;
        loadFromFile(f);
    }

    private void loadFromFile(File file){
        if (!file.exists()) {
            System.out.println("Config file does not exist! Creating...");
            try {
                Path configPath = Path.of(file.getPath());
                Files.createFile(configPath);
                System.out.println("Config file created. You may now close this window.");
                System.in.read();

                return;
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
            this.jdbcUrl = loaded.getJdbcUrl();
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
    }


    public String toString() {
        String botToken = this.getBotToken();
        String botPrefix = this.getBotPrefix();
        return String.format("Config { \"token\": \"%s\", \"prefix\": \"%s\" }", botToken, botPrefix);
    }
}