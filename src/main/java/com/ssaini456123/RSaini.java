package com.ssaini456123;

import com.ssaini456123.command.CommandRegistry;
import com.ssaini456123.event.CommandMessageListener;
import com.ssaini456123.util.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

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

        CommandRegistry commandRegistry = new CommandRegistry();
        commandRegistry.initializeCommands();

        JDA jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .addEventListeners(new CommandMessageListener(this.config, commandRegistry))
                .build();


        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}