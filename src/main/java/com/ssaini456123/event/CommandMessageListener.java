package com.ssaini456123.event;

import com.ssaini456123.command.Command;
import com.ssaini456123.command.CommandRegistry;
import com.ssaini456123.util.Config;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sutinder S. Saini
 */
public class CommandMessageListener extends ListenerAdapter {

    private final Config config;
    private final CommandRegistry registry;

    public CommandMessageListener(Config config, CommandRegistry commandRegistry) {
        this.config = config;
        this.registry = commandRegistry;
    }

    private String removePrefix(String str, String prefix) {
        if (str.startsWith(prefix)) {
            String subStr = str.substring(1);
            String[] subParts = subStr.split(" ");
            return subParts[0];
        } else {
            return "";
        }
    }

    private ArrayList<String> formArgs(String str) {
        String[] test = str.split(" ");
        ArrayList<String> argumentsList = new ArrayList<>();

        for (int i = 1; i < test.length; i++) {
            argumentsList.add(test[i]);
        }

        return argumentsList;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentRaw();
        String botPrefix = config.getBotPrefix();

        if ((!messageContent.startsWith(botPrefix) || (event.getAuthor().isBot()))) {
            return;
        }

        String prefix = this.config.getBotPrefix();
        String possibleCommandName = removePrefix(messageContent, prefix);

        if (possibleCommandName.isEmpty()) {
            return;
        }

        ArrayList<String> argsList = formArgs(messageContent);
        boolean isCommand = registry.commandExists(possibleCommandName);

        if (isCommand) {
            Command instance = registry.getCommandInstance(possibleCommandName);
            instance.execute(event, this.config, argsList);
        }
    }
}