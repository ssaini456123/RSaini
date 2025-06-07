package com.ssaini456123.event;

import com.ssaini456123.ConnectionPool;
import com.ssaini456123.util.Config;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @author Sutinder S. Saini
 */
public class GuildKickEvent extends ListenerAdapter {
    ConnectionPool connectionPool;

    public GuildKickEvent(Config c) {
        String configName = c.getConfigName();
        this.connectionPool = new ConnectionPool(configName);
    }

    @Override
    public void onGuildLeave(@NotNull net.dv8tion.jda.api.events.guild.GuildLeaveEvent event) {
        String guildId = event.getGuild().getId();
        System.out.println("Removing connection: " + guildId);
        this.connectionPool.ditchConnection(guildId);
    }
}
