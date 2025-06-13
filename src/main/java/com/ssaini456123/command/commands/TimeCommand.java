package com.ssaini456123.command.commands;

import com.ssaini456123.ConnectionPool;
import com.ssaini456123.command.Command;
import com.ssaini456123.command.meta.CommandCategory;
import com.ssaini456123.command.meta.CommandPermission;
import com.ssaini456123.command.meta.RCommandMeta;
import com.ssaini456123.util.Config;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sutinder S. Saini
 */
@RCommandMeta(
        name = "time",
        description = "What time is it?",
        usage = {
                "`time` - Find what time it is for you (if set)",
                "`time <user>` - Displays the users current time (if they set one)"
        },
        category = CommandCategory.FUN,
        permission = CommandPermission.USER)
public class TimeCommand implements Command {

    private boolean hasTz(Connection connection, long userId) {
        String query = "SELECT timezone FROM user_settings WHERE id = ?";

        try (PreparedStatement pp = connection.prepareStatement(query)) {

            pp.setLong(1, userId);

            try (ResultSet rs = pp.executeQuery()) {
                if (rs.next()) {
                    String timezone = rs.getString("timezone");
                    return true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    private String pullTz(Connection connection, long userId) {
        String query = "SELECT timezone FROM user_settings WHERE id = ?";

        try (PreparedStatement pp = connection.prepareStatement(query)) {
            pp.setLong(1, userId);

            try (ResultSet rs = pp.executeQuery()) {
                rs.next();
                String tz = rs.getString(1);
                return tz;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    String getTimeEmoji(String marker) {
        if (marker.equals("PM")) {
            return "🌙";
        }

        return "☀️";
    }

    private String getSelfFormattedTime(String timezone) {
        ZonedDateTime current_time = ZonedDateTime.now(ZoneId.of(timezone));
        int hr24 = current_time.getHour();
        int min = current_time.getMinute();

        int hr12 = hr24 % 12 == 0 ? 12 : hr24 % 12;

        String timeMarker = hr24 < 12 ? "AM" : "PM";

        String emoji = this.getTimeEmoji(timeMarker);
        String timeFmt = String.format("%s It is currently **%d:%02d %s** for you.", emoji, hr12, min, timeMarker);

        return timeFmt;
    }

    private String getUserFormattedTime(String timezone, String username) {
        ZonedDateTime current_time = ZonedDateTime.now(ZoneId.of(timezone));
        int hr24 = current_time.getHour();
        int min = current_time.getMinute();

        int hr12 = hr24 % 12 == 0 ? 12 : hr24 % 12;
        String timeMarker = hr24 < 12 ? "AM" : "PM";

        String emoji = this.getTimeEmoji(timeMarker);
        String timeFmt = String.format("%s It is currently **%d:%02d %s** for %s...", emoji, hr12, min, timeMarker,
                username);

        return timeFmt;
    }

    @Override
    public void execute(MessageReceivedEvent event, Config c, ArrayList<String> args) {
        boolean self = args.isEmpty();
        String guildId = event.getGuild().getId();

        long userId = Long.parseLong(event.getAuthor().getId());

        ConnectionPool connectionPool = new ConnectionPool(c.getConfigName());
        Connection conn = connectionPool.getConnection(guildId);

        if(self) {
            if (!this.hasTz(conn, userId)) {
                event.getChannel().sendMessage("You don't have a timezone on record. See: `$help settz`").queue();
                return;
            }

            String tz = this.pullTz(conn, userId);

            if (tz == null) {
                event.getChannel().sendMessage("Something went wrong :(").queue();
                return;
            }

            String timeFmt = this.getSelfFormattedTime(tz);
            event.getChannel().sendMessage(timeFmt).queue();
            return;
        }

        List<User> userPing = event.getMessage().getMentions().getUsers();
        if (userPing.isEmpty()) {
            event.getChannel().sendMessage("You need to mention someone.").queue();
        } else {
            User user = userPing.getFirst();
            long mentionedId = user.getIdLong();
            boolean hasTz = this.hasTz(conn, mentionedId);

            if (!hasTz) {
                event.getChannel().sendMessage("I dont have that persons timezone in my records.").queue();
                return;
            }

            String userName = user.getName();
            String tz = this.pullTz(conn, mentionedId);
            String fmt = this.getUserFormattedTime(tz, userName);

            event.getChannel().sendMessage(fmt).queue();
        }
    }
}
