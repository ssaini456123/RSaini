package com.ssaini456123.command.commands;

import com.ssaini456123.command.Command;
import com.ssaini456123.command.meta.CommandCategory;
import com.ssaini456123.command.meta.CommandPermission;
import com.ssaini456123.command.meta.RCommandMeta;
import com.ssaini456123.util.Config;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;

/**
 * @author Sutinder S. Saini
 */
@RCommandMeta(
        name = "ping",
        description = "Replies with pong!",
        usage = "`ping` - Replies with pong.",
        category = CommandCategory.MISC,
        permission = CommandPermission.ADMIN
)
public class PingCommand implements Command {

    public PingCommand() {

    }

    @Override
    public void execute(MessageReceivedEvent event, Config c, ArrayList<String> args) {
        event.getChannel().sendMessage("Hi").queue();
        event.getMessage().addReaction(Emoji.fromUnicode("⭐")).queue();
    }
}
