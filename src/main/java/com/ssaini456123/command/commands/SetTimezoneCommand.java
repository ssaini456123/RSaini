package com.ssaini456123.command.commands;

import com.ssaini456123.ConnectionPool;
import com.ssaini456123.command.Command;
import com.ssaini456123.command.meta.CommandCategory;
import com.ssaini456123.command.meta.CommandPermission;
import com.ssaini456123.command.meta.RCommandMeta;
import com.ssaini456123.util.Config;
import com.ssaini456123.util.PostgresConnection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.pagination.ReactionPaginationAction;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private static final int MAX_TIMEZONES_PER_PAGE = 5;

    String[] pageControlEmojis = {
            "◀️",
            "▶️",
            "🛑",
            "⏩",
            "⏪"
    };

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

    public HashMap<Integer, MessageEmbed> createPages(String[] timezones) {
        final int maxPerPage = MAX_TIMEZONES_PER_PAGE;

        HashMap<Integer, MessageEmbed> embedMap = new HashMap<>();

        int pageCount = 1;
        int i = 0;
        int thresholdCounter = 1;

        EmbedBuilder currentBuilder = new EmbedBuilder();

        while (i < timezones.length) {
            String tzFmt = String.format("`%s`", timezones[i]);

            currentBuilder.setTitle("List of timezones (Page " + pageCount + ")");
            currentBuilder.addField(tzFmt, "", true);

            if ((i + 1) % maxPerPage == 0) {
                currentBuilder.setTitle("List of Timezones (Page " + pageCount + ")");
                embedMap.put(pageCount, currentBuilder.build());
                currentBuilder = new EmbedBuilder();
                pageCount++;
            }

            thresholdCounter++;
            i++;
        }

        System.out.println(embedMap.toString());
        return embedMap;
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
            String[] tzs = TimeZone.getAvailableIDs();
            HashMap<Integer, MessageEmbed> pages = this.createPages(tzs);

            // page 1 -> onward
            event.getChannel().sendMessageEmbeds(pages.get(1)).queue( embed -> {
                int[] page = {1};

                int maximumPages = pages.keySet().size();
                String[] emojis = this.pageControlEmojis;

                // Form the control panel
                for (int i = 0; i < emojis.length; i++) {
                    String unicode = emojis[i];
                    Emoji emoji = Emoji.fromUnicode(unicode);
                    embed.addReaction(emoji).queue();
                }

                long embedMessageId = embed.getIdLong();
                long authorId = event.getAuthor().getIdLong();

                ListenerAdapter emojiListener = new ListenerAdapter() {
                    @Override
                    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent e) {
                        MessageEmbed currentPage;

                        if (e.getMessageIdLong() != embedMessageId) return;
                        if (e.getUser().getIdLong() != authorId) return;

                        String selected = e.getReaction().getEmoji().getName();

                        if (selected.equals(emojis[0]) && page[0] > 1) {
                            // go backward
                            page[0]--;
                            currentPage = pages.get(page[0]);
                        } else if (selected.equals(emojis[1]) && page[0] < maximumPages) {
                            // go forward
                            page[0]++;
                            currentPage = pages.get(page[0]);
                        } else if (selected.equals(emojis[2])) {
                            //stop
                            embed.clearReactions().queue();
                            event.getJDA().removeEventListener(this);
                            return;
                        } else if (selected.equals(emojis[3])) {
                            //fast forward
                            page[0] += 5;
                            currentPage = pages.get(page[0]);
                        } else if (selected.equals(emojis[4])) {
                            //fast backward
                            page[0] -= 5;
                            currentPage = pages.get(page[0]);
                        } else {
                            return;
                        }

                        embed.editMessageEmbeds(currentPage).queue();
                        e.getReaction().removeReaction(e.getUser()).queue();
                    }
                };

                event.getJDA().addEventListener(emojiListener);
                ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

                timer.schedule(() -> {
                    embed.clearReactions().queue();
                    event.getJDA().removeEventListener(emojiListener);
                }, 60, TimeUnit.SECONDS);
            });

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
