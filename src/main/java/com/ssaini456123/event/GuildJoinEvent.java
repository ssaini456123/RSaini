package com.ssaini456123.event;

import com.ssaini456123.ConnectionPool;
import com.ssaini456123.util.Config;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @author Sutinder S. Saini
 */
public class GuildJoinEvent extends ListenerAdapter {
    ConnectionPool connectionPool;

    public GuildJoinEvent(Config config) {
        String configName = config.getConfigName();
        this.connectionPool = new ConnectionPool(configName);
    }

    @Override
    public void onGuildJoin(@NotNull net.dv8tion.jda.api.events.guild.GuildJoinEvent event) {
        String guildId = event.getGuild().getId();
        connectionPool.create(guildId);
    }
}