package com.ssaini456123.command.commands;

import com.ssaini456123.command.Command;
import com.ssaini456123.command.meta.CommandCategory;
import com.ssaini456123.command.meta.CommandPermission;
import com.ssaini456123.command.meta.RCommandMeta;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;

/**
 * @author Sutinder S. Saini
 */
@RCommandMeta(
        name = "ping",
        description = "Replies with `pong!`",
        category = CommandCategory.MISC,
        permission = CommandPermission.USER
)
public class Ping implements Command {

    public Ping() {

    }

    @Override
    public void execute(MessageReceivedEvent event, ArrayList<String> args) {
        event.getChannel().sendMessage("Hi").queue();
    }
}
