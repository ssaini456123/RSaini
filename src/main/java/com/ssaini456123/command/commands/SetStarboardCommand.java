package com.ssaini456123.command.commands;

import com.ssaini456123.ConnectionPool;
import com.ssaini456123.command.Command;
import com.ssaini456123.command.meta.CommandCategory;
import com.ssaini456123.command.meta.CommandPermission;
import com.ssaini456123.command.meta.RCommandMeta;
import com.ssaini456123.util.Config;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Sutinder S. Saini
 */
@RCommandMeta(
        name = "setsb",
        description = "Marks a starboard channel",
        usage = "`setsb <channel ID>` - Sets the target channel ID as a starboard channel.",
        category = CommandCategory.UTILITY,
        permission = CommandPermission.ADMIN
)
public class SetStarboardCommand implements Command {
    private boolean changeSb(Connection conn, long channelId, long guildId) {
        String query = "         INSERT INTO starboard (id, channel)\n" +
                "                VALUES(?, ?)\n" +
                "                ON CONFLICT(id) DO UPDATE\n" +
                "                SET channel = EXCLUDED.channel;";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setLong(1, guildId);
            preparedStatement.setLong(2, channelId);
            preparedStatement.executeUpdate();
            return true;
        } catch (RuntimeException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void execute(MessageReceivedEvent event, Config c, ArrayList<String> args) {
        MessageChannelUnion eventChannel = event.getChannel();
        String guildId = event.getGuild().getId();

        if (args.isEmpty()) {
            eventChannel.sendMessage("You must provide a channel ID.").queue();
            return;
        }

        boolean isAdmin = Objects.requireNonNull(event.getMember()).hasPermission(Permission.ADMINISTRATOR);

        if (!isAdmin) {
            eventChannel.sendMessage("This command requires `Permission.ADMIN` in order to execute.").queue();
            return;
        }

        String channelId = args.getFirst();
        List<GuildChannel> gc = event.getGuild().getChannels();
        boolean channelFound = false;

        for (GuildChannel chann : gc) {
            if (chann.getId().equals(channelId)) {
                channelFound = true;
            }
        }

        if (!channelFound) {
            eventChannel.sendMessage("That channel does not exist.").queue();
            return;
        }

        ConnectionPool connectionPool = new ConnectionPool(c.getConfigName());
        Connection conn = connectionPool.getConnection(guildId);

        long channelIdLong;
        long guildIdLong = Long.parseLong(guildId);

        try {
            channelIdLong = Long.parseLong(channelId);
        } catch (NumberFormatException e) {
            // alr bro
            eventChannel.sendMessage("You must provide an ID, not a channel jump-link.").queue();
            return;
        }

        boolean status = this.changeSb(conn, channelIdLong, guildIdLong);

        if (status) {
            eventChannel.sendMessage("✅").queue();
        } else {
            eventChannel.sendMessage("Something went terribly wrong.").queue();
        }
    }
}