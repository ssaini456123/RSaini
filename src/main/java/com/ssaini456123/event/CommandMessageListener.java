package com.ssaini456123.event;

import com.ssaini456123.command.CommandRegistry;
import com.ssaini456123.util.Config;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

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

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentRaw();
        String botPrefix = config.getBotPrefix();

        if ((!messageContent.startsWith(botPrefix) || (event.getAuthor().isBot()))) {
            return;
        }

    }
}