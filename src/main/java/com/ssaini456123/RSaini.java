package com.ssaini456123;

import com.ssaini456123.command.CommandRegistry;
import com.ssaini456123.event.CommandMessageListener;
import com.ssaini456123.event.GuildJoinEvent;
import com.ssaini456123.event.GuildKickEvent;
import com.ssaini456123.event.MessageStarEvent;
import com.ssaini456123.util.Config;
import com.ssaini456123.util.PostgresConnection;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.sql.Statement;
import java.util.List;

/**
 * @author Sutinder S. Saini
 */
public class RSaini {
    private final Config config;
    private final ConnectionPool connPool; // i will NOT shorten this any further.

    public RSaini(Config config) {
        this.config = config;
        this.connPool = new ConnectionPool(config.getConfigName());
    }

    public void start() {
        String token = this.config.getBotToken();

        CommandRegistry commandRegistry = new CommandRegistry();
        commandRegistry.initializeCommands();

        JDA jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .addEventListeners(new CommandMessageListener(this.config, commandRegistry))
                .addEventListeners(new GuildJoinEvent(this.config))
                .addEventListeners(new GuildKickEvent(this.config))
                .addEventListeners(new MessageStarEvent(this.config))
                .build();
        try {
            jda.awaitReady();

            List<Guild> allGuilds = jda.getGuilds();
            for (Guild g : allGuilds) {
                String guildId = g.getId();
                this.connPool.create(guildId);
            }

            System.out.println("Synced current guilds with the connection pool.");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}