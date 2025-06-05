package com.ssaini456123;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Sutinder S. Saini
 */
public class Config {
    private String botToken;
    private String botPrefix;
    private boolean useAutoUpdater;

    public Config(String fileName) {
        File f = new File(fileName);
        new Config(f);
    }

    public Config(File file) {
        if (!file.exists()) {
            System.out.println("Config file does not exist! Creating...");
            try {
                Path configPath = Path.of(file.getPath());
                Files.createFile(configPath);

                //write defaults
                this.writeJsonOpener(file);

                System.out.println("Config file created. Press <ENTER> and relaunch the bot.");
                System.in.read();

            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeJsonOpener(File file) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write('{');
            fileWriter.write('}');
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}