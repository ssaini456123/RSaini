package com.ssaini456123.event;

import com.ssaini456123.command.Command;
import com.ssaini456123.command.CommandRegistry;
import com.ssaini456123.util.Config;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        int prefixLength = prefix.length();
        if (str.startsWith(prefix)) {
            return str.substring(1);
        } else {
            return "";
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentRaw();
        String botPrefix = config.getBotPrefix();

        if ((!messageContent.startsWith(botPrefix) || (event.getAuthor().isBot()))) {
            return;
        }

        String possibleCommandName = removePrefix(messageContent, "$");

        if (possibleCommandName.equals("")) {
            return;
        }

        boolean isCommand = registry.commandExists(possibleCommandName);
        if (isCommand) {
            Command instance = registry.getCommandInstance(possibleCommandName);
            instance.execute(event, null);
        }
    }
}