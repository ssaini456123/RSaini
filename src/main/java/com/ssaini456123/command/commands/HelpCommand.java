package com.ssaini456123.command.commands;

import com.ssaini456123.command.Command;
import com.ssaini456123.command.CommandRegistry;
import com.ssaini456123.command.meta.*;
import com.ssaini456123.util.Config;
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
        usage = {"`help` ━ Gives you the whole list of valid commands.", "`help <command>` ━ Displays usage tips on a specific command."},
        category = CommandCategory.UTILITY,
        permission = CommandPermission.USER
)
public class HelpCommand implements Command {

    private String boldenText(String str) {
        return "**" + str + "**";
    }

    @Override
    public void execute(MessageReceivedEvent event, Config c, ArrayList<String> args) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        HashMap<String, RCommandMeta> metaMap = CommandRegistry.getMetaHashMap();
        List<RCommandMeta> metaList = new ArrayList<>(metaMap.values());

        String commandName;

        if (!args.isEmpty()) {
            commandName = args.getFirst();
            RCommandMeta meta = metaMap.get(commandName);

            if (meta == null) {
                event.getChannel().sendMessage("Unknown command `" + commandName + "`.").queue();
                return;
            }

            String cmdDesc = meta.description();

            commandName = "`" + commandName + "`";
            cmdDesc = "`" + cmdDesc + "`";

            String[] usageList = meta.usage();

            embedBuilder.setTitle(commandName);
            embedBuilder.addField("Description", cmdDesc, false);

            embedBuilder.addField("Usages:", "", true);

            if (usageList.length > 1) {
                for (int i = 0; i < usageList.length; i++) {
                    String usage = usageList[i];
                    embedBuilder.addField("", usage, true);
                }
            } else {
                embedBuilder.addField("", usageList[0],true);
            }

            MessageEmbed embed = embedBuilder.build();
            event.getChannel().sendMessageEmbeds(embed).queue();
            return;
        }


        embedBuilder.setTitle("List of commands");

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