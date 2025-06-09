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

/**
 * @author Sutinder S. Saini
 */
@RCommandMeta(
        name = "changethreshold",
        description = "Changes the starboard threshold (The amount of stars needed to display on the board)",
        usage = "`changethreshold <amount>` - Sets a requirement on how many stars are needed to display on the starboard. (Default is **13**)",
        category = CommandCategory.UTILITY,
        permission = CommandPermission.ADMIN
)
public class ChangeThresholdCommand implements Command {

    private boolean changeThreshold(Connection conn, long amount, long guildId) {
        String query = "INSERT INTO starboard (id, threshold) " +
                        "VALUES (?, ?) " +
                        "ON CONFLICT(id) DO UPDATE SET threshold = EXCLUDED.threshold";

        try (PreparedStatement pp = conn.prepareStatement(query)) {

            pp.setLong(1, guildId);
            pp.setLong(2, amount);

            pp.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void execute(MessageReceivedEvent event, Config c, ArrayList<String> args) {
        MessageChannelUnion eventChannel = event.getChannel();
        String guildId = event.getGuild().getId();

        if (args.isEmpty()) {
            eventChannel.sendMessage("For this command to function you need to supply an `amount`.").queue();
            return;
        }

        boolean isAdmin = event.getMember().hasPermission(Permission.ADMINISTRATOR);

        if (!isAdmin) {
            eventChannel.sendMessage("This command requires `Permission.ADMIN` in order to execute.").queue();
            return;
        }

        String threshold = args.getFirst();
        long thresholdLong;

        try {
            thresholdLong = Long.parseLong(threshold);
        } catch (NumberFormatException e) {
            // High chance this was a massive number :3
            eventChannel.sendMessage("https://www.youtube.com/watch?v=LrGWB-_L4fQ").queue();
            return;
        }

        if (thresholdLong <= 0) {
            eventChannel.sendMessage("Supplied amount must be atleast 1.").queue();
            return;
        }

        long guildIdLong = Long.parseLong(guildId);

        ConnectionPool connectionPool = new ConnectionPool(c.getConfigName());
        Connection conn = connectionPool.getConnection(guildId);

        boolean status = changeThreshold(conn, thresholdLong, guildIdLong);

        if (status) {
            eventChannel.sendMessage("✅").queue();
        } else {
            eventChannel.sendMessage("Something went terribly wrong.").queue();
        }
    }
}
