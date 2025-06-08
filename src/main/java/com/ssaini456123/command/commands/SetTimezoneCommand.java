package com.ssaini456123.command.commands;

import com.ssaini456123.ConnectionPool;
import com.ssaini456123.command.Command;
import com.ssaini456123.command.meta.CommandCategory;
import com.ssaini456123.command.meta.CommandPermission;
import com.ssaini456123.command.meta.RCommandMeta;
import com.ssaini456123.util.Config;
import com.ssaini456123.util.PostgresConnection;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.pagination.ReactionPaginationAction;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Sutinder S. Saini
 */

@RCommandMeta(
        name = "settz",
        description = "Set your timezone!",
        usage = "`settz <tz>` - Set the timezone to the target `tz`. If `tz` is invalid, a prompt of the closest " +
                "match will be sent.",
        category = CommandCategory.FUN,
        permission = CommandPermission.USER
)
public class SetTimezoneCommand implements Command {

    private String findClosest(String ident) {
        String[] matches = TimeZone.getAvailableIDs();
        String closestMatch = null;

        int minDistance = Integer.MAX_VALUE;

        for (String current : matches) {
            int distance = LevenshteinDistance.getDefaultInstance().apply(ident, current);

            if (distance < minDistance) {
                minDistance = distance;
                closestMatch = current;
            }
        }

        return closestMatch;
    }

    private boolean isValid(String timezone) {
        String[] tzs = TimeZone.getAvailableIDs();
        boolean match = false;

        for (String tz : tzs) {
            if (tz.equals(timezone)) {
                match = true;
                break;
            }
        }

        return match;
    }

    private boolean placeOrChangeTz(Connection conn, long userId, String tz) {
        String sql = "INSERT INTO user_settings (id, timezone) \n" +
                "VALUES (?, ?)\n" +
                "ON CONFLICT (id) DO UPDATE \n" +
                "SET timezone = EXCLUDED.timezone;";

        try (PreparedStatement pp = conn.prepareStatement(sql)) {
            pp.setLong(1, userId);
            pp.setString(2, tz);
            pp.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    @Override
    public void execute(MessageReceivedEvent event, Config c, ArrayList<String> args) {
        if (args.isEmpty()) {
            event.getChannel().sendMessage("You must supply a timezone.").queue();
            return;
        }

        String guildId = event.getGuild().getId();

        String tz = args.getFirst();

        boolean valid = this.isValid(tz);
        if (!valid) {
            String closestMatch = this.findClosest(tz);
            event.getChannel().sendMessage("Unknown timezone. Did you mean `"+closestMatch+"`...?").queue();
            return;
        }

        ConnectionPool connectionPool = new ConnectionPool(c.getConfigName());
        Connection conn = connectionPool.getConnection(guildId);

        long userId = Long.parseLong(event.getAuthor().getId());

        boolean status = placeOrChangeTz(conn, userId, tz);
        if (status) {
            event.getChannel().sendMessage("✅ Timezone set to `"+tz+"`.").queue();
        } else {
            event.getChannel().sendMessage("Something went horribly wrong.").queue();
        }

    }
}
