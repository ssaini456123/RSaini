package com.ssaini456123;

import com.ssaini456123.util.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.nio.file.Path;

/**
 * @author Sutinder S. Saini
 */
public class RSaini {
    private final Config config;

    public RSaini(Config config) {
        this.config = config;
    }

    public void start() {

        String token = this.config.getBotToken();

        JDA jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .build();

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}