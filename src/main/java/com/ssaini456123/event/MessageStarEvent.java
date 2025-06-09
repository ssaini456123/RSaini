package com.ssaini456123.event;

import com.ssaini456123.ConnectionPool;
import com.ssaini456123.util.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Sutinder S. Saini
 */
public class MessageStarEvent extends ListenerAdapter {
    private ConnectionPool connectionPool;

    private static final Emoji STAR_EMOJI = Emoji.fromUnicode("⭐");

    private static final String[] PHASES = {
            "⭐", "✨", "🌠", "🌟"
    };

    public MessageStarEvent(Config c) {
        String configName = c.getConfigName();
        this.connectionPool = new ConnectionPool(configName);
    }

    public String computePhase(final long amount) {
        if (amount <= 1) return PHASES[0];
        else if (amount <= 10) return PHASES[1];
        else if (amount <= 15) return PHASES[2];
        else {
            return PHASES[3];
        }
    }

    private boolean starboardExists(Connection conn, long guildId) {
        String query = "SELECT 1 FROM starboard WHERE id = ? LIMIT 1";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setLong(1, guildId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                boolean validRow = rs.next();
                return validRow;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private long getStarboardChannelId(Connection conn, long guildId) {
        String query = "SELECT channel FROM starboard WHERE id = ? LIMIT 1";

        try (PreparedStatement pp = conn.prepareStatement(query)) {
            pp.setLong(1, guildId);
            long channelId = 0;

            try (ResultSet rs = pp.executeQuery()) {

                if (rs.next()) {
                    channelId = rs.getLong(1);
                }

                return channelId;

            } catch (SQLException e) {
                return -1;
            }

        } catch (SQLException e) {
            return -1;
        }
    }

    private boolean addStarer(Connection conn, long messageId, long userId) {
        String query = "INSERT INTO starers (user_id, msg_id) VALUES (?, ?)";

        try (PreparedStatement pp = conn.prepareStatement(query)) {
            pp.setLong(1, userId);
            pp.setLong(2, messageId);
            pp.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean alreadyStarred(Connection conn, long userId, long messageId) {
        String query = "SELECT 1 FROM starers WHERE user_id = ? AND msg_id = ?";
        try (PreparedStatement pp = conn.prepareStatement(query)) {
            pp.setLong(1, userId);
            pp.setLong(2, messageId);

            try (ResultSet rs = pp.executeQuery()) {
                if (rs.next()) return true;
                else return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public long getStars(Connection conn, long userId, long messageId) {
        String query = "SELECT stars FROM starboard_entries WHERE msg_id=? LIMIT 1";
        try (PreparedStatement pp = conn.prepareStatement(query)) {
            pp.setLong(1, messageId);

            try (ResultSet rs = pp.executeQuery()) {
                if (rs.next()) {
                    long stars = rs.getLong(1);
                    return stars;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    public long getBotContentId(Connection conn, long messageId) {
        String query = "SELECT bot_message_id FROM starboard_entries WHERE msg_id = ?";
        try (PreparedStatement pp = conn.prepareStatement(query)) {
            pp.setLong(1, messageId);

            try (ResultSet resultSet = pp.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(1);
            }
        } catch (SQLException e) {
            return -1;
        }
    }

    private boolean addEntry(Connection conn, long messageId, long botMessageId, long channelOriginId,
                             int stars, long botContentId) {

        String query = "INSERT INTO starboard_entries (msg_id, bot_message_id, channel, stars, bot_content_id)" +
                "VALUES(?, ?, ?, ?, ?)";

        try (PreparedStatement pp = conn.prepareStatement(query)) {
            pp.setLong(1, messageId);
            pp.setLong(2, botMessageId);
            pp.setLong(3, channelOriginId);
            pp.setInt(4, stars);
            pp.setLong(5, botContentId);

            pp.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean addStar(Connection conn, long messageId) {
        String query = "UPDATE starboard_entries SET stars = starboard_entries.stars + 1 \n" +
                "                        WHERE msg_id = ?";

        try (PreparedStatement pp = conn.prepareStatement(query)) {
            pp.setLong(1, messageId);
            pp.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean removeStar(Connection conn, long messageId) {
        String query = "UPDATE starboard_entries SET stars = starboard_entries.stars - 1 \n" +
                "                        WHERE msg_id = ?";

        try (PreparedStatement pp = conn.prepareStatement(query)) {
            pp.setLong(1, messageId);
            pp.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean removeStarer(Connection conn, long userId, long messageId) {
        String query = "DELETE FROM starers WHERE user_id = ? AND msg_id = ?;";

        try (PreparedStatement pp = conn.prepareStatement(query)) {
            pp.setLong(1, userId);
            pp.setLong(2, messageId);
            pp.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private int getThreshold(Connection conn, long guildId) {
        String query = "SELECT threshold FROM starboard WHERE id = ?;";

        try (PreparedStatement pp = conn.prepareStatement(query)) {

            pp.setLong(1, guildId);

            try (ResultSet rs = pp.executeQuery()) {
                rs.next();
                int threshold = rs.getInt(1);
                return threshold;
            }
        } catch (SQLException exception) {
            System.out.println("THRESHOLD ERR");
            exception.printStackTrace();
            return -1;
        }
    }

    private boolean purgeRecord(Connection conn, long messageId) throws SQLException {
        String entryQuery =  "DELETE FROM starboard_entries WHERE msg_id = ?;";
        String starerQuery = "DELETE FROM starers WHERE msg_id = ?;";

        conn.setAutoCommit(false); // for atomic deletes

        try (PreparedStatement ep = conn.prepareStatement(entryQuery);
             PreparedStatement sp = conn.prepareStatement(starerQuery)
        ) {

            ep.setLong(1, messageId);
            sp.setLong(1, messageId);

            ep.executeUpdate();
            sp.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            conn.rollback();
            return false;

        } finally {
            conn.setAutoCommit(true);
        }
    }

    private MessageEmbed makeEmbed(String authorName, String desc, boolean hasImages,
                                   List<Message.Attachment> attachment) {
        EmbedBuilder emb = new EmbedBuilder();

        emb.addField(authorName, desc, false);

        if (hasImages) {
            String firstAttachment = attachment.getFirst().getUrl();
            emb.setImage(firstAttachment);
        }

        emb.setColor(Color.YELLOW);

        return emb.build();
    }

    private String makeContentHeader(long starCount, String jumpLink) {
        String emoji = this.computePhase(starCount);
        String heading = "%s `%d` | %s";

        return String.format(heading, emoji, starCount, jumpLink);
    }

    @Override
    public void onMessageReactionAdd( MessageReactionAddEvent event) {
        long guildId = event.getGuild().getIdLong();
        long reactorId = event.getUserIdLong();
        long messageId = Long.parseLong(event.getMessageId());

        Emoji reacted = event.getEmoji();

        if (!(reacted.equals(STAR_EMOJI))) {
            return;
        }

        String gIdStr = event.getGuild().getId();
        Connection conn = this.connectionPool.getConnection(gIdStr);

        if (!this.starboardExists(conn, guildId)) return;

        // bruh
        event.getChannel().retrieveMessageById(event.getMessageId()).queue(message -> {
            int reactionCount = message.getReactions().stream()
                    .filter(r -> r.getEmoji().equals(STAR_EMOJI))
                    .findFirst()
                    .map(r -> r.getCount())
                    .orElse(0);

            String jumpLink = message.getJumpUrl();
            long starboardChannelId = this.getStarboardChannelId(conn, guildId);

            int threshold = this.getThreshold(conn, guildId);

            TextChannel starboardChannel = event.getGuild().getTextChannelById(starboardChannelId);
            TextChannel originChannel = event.getGuild().getTextChannelById(event.getChannel().getId());

            if (reactionCount == threshold) {
                try {

                    if (this.alreadyStarred(conn, reactorId, messageId)) return;

                    originChannel.retrieveMessageById(messageId).queue((msg) -> {
                        String messageContent = msg.getContentRaw();
                        String authorName = msg.getAuthor().getName();

                        boolean hasAttachments = !msg.getAttachments().isEmpty();
                        MessageEmbed m = this.makeEmbed(authorName, messageContent, hasAttachments, msg.getAttachments());
                        String heading = this.makeContentHeader(threshold, jumpLink);

                        starboardChannel.sendMessage(heading)
                                .setEmbeds(m)
                                .queue(sentMessage -> {

                                    this.addEntry(
                                            conn,
                                            msg.getIdLong(),
                                            sentMessage.getIdLong(),
                                            originChannel.getIdLong(),
                                            threshold,
                                            sentMessage.getIdLong()
                                    );

                                    }, fail -> {
                                    System.out.println("Something went wrong again idfk.");
                                });

                    }, fail -> {
                        System.out.println("Something went wrong idk");
                    });

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                // make sure this mf in particular doesnt flood with react/unreacts
                boolean starerSuccessful = this.addStarer(conn, messageId, reactorId);

                if (!starerSuccessful) {
                    System.out.println("!!Internal error when adding starer!!");
                    return;
                }
            }

            boolean alreadyStarredMessage = this.alreadyStarred(conn, reactorId, messageId);

            if (alreadyStarredMessage) return;

            long stars = (this.getStars(conn, reactorId, messageId)) + 1; // for display

            this.addStar(conn, messageId);
            this.addStarer(conn, messageId, reactorId);

            String heading = this.makeContentHeader(stars, jumpLink);
            long botContentId = this.getBotContentId(conn, messageId);

            if (botContentId == -1) {
                return;
            }

            starboardChannel.retrieveMessageById(botContentId).queue(botMessage -> {
                botMessage.editMessage(heading).queue();
            });
        });
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        long guildId = event.getGuild().getIdLong();
        String gIdStr = event.getGuild().getId();
        Emoji reacted = event.getEmoji();
        Connection conn = this.connectionPool.getConnection(gIdStr);

        if (!(reacted.equals(STAR_EMOJI))) {
            return;
        }

        if (!this.starboardExists(conn, guildId)) return;

        long reactorId = event.getUserIdLong();
        long messageId = Long.parseLong(event.getMessageId());
        long starboardId = this.getStarboardChannelId(conn, guildId);

        TextChannel starboardTextChannel = event.getJDA().getTextChannelById(starboardId);

        event.getChannel().retrieveMessageById(event.getMessageId()).queue(message -> {

            int reactionCount = message.getReactions().stream()
                    .filter(r -> r.getEmoji().equals(STAR_EMOJI))
                    .findFirst()
                    .map(MessageReaction::getCount)
                    .orElse(0);

            long threshold = this.getThreshold(conn, guildId);

            if (reactionCount < threshold) {

                long botMessageId = this.getBotContentId(conn, messageId);

                starboardTextChannel.retrieveMessageById(botMessageId).queue(
                        msg -> {
                            try {
                                msg.delete().queue();
                                this.purgeRecord(conn, messageId);

                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }, failure -> System.out.println(failure.getMessage())
                );

                return;
            }

            long starCount = this.getStars(conn, reactorId, messageId);
            starCount -= 1;

            String jumpLink = message.getJumpUrl();
            String heading = this.makeContentHeader(starCount, jumpLink);

            long botMsgId = this.getBotContentId(conn, messageId);

            starboardTextChannel.retrieveMessageById(botMsgId).queue(msg -> {
                msg.editMessage(heading).queue();
            });

            this.removeStar(conn, messageId);
            this.removeStarer(conn, reactorId, messageId);

        }, fail -> {
            System.out.println("idk dude");
        });
    }
}
