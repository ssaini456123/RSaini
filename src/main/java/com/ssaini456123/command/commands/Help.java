package com.ssaini456123.command.commands;

import com.ssaini456123.command.Command;
import com.ssaini456123.command.CommandRegistry;
import com.ssaini456123.command.meta.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;

/**
 * @author Sutinder S. Saini
 */
@RCommandMeta(
        name = "help",
        description = "Lists valid commands for R. Saini",
        usage = "help <command name>",
        category = CommandCategory.UTILITY,
        permission = CommandPermission.USER
)
public class Help implements Command {
    @Override
    public void execute(MessageReceivedEvent event, ArrayList<String> args) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("List of commands");

        HashMap<String, RCommandMeta> metaMap = CommandRegistry.getMetaHashMap();
        List<RCommandMeta> metaList = new ArrayList<>(metaMap.values());

        for (int i = 0; i < metaList.size(); i++) {
            String cmdName = metaList.get(i).name();
            String cmdDesc = metaList.get(i).description();
            CommandPermission perm = metaList.get(i).permission();
            cmdName = "`" + cmdName + "`";
            if (perm.equals(CommandPermission.ADMIN)) {
                cmdName = cmdName + " - (Admin)";
            }

            cmdDesc = "┗━━ `" + cmdDesc + "`\t";
            embedBuilder.addField(cmdName, cmdDesc, true);
        }

        embedBuilder.setFooter("you can also use help <name> to get details on a command name.");
        MessageEmbed embed = embedBuilder.build();
        event.getChannel().sendMessageEmbeds(embed).queue();
    }
}