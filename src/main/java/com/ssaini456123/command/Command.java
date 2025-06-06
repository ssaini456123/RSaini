package com.ssaini456123.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;

/**
 * @author Sutinder S. Saini
 */
public interface Command {
    void execute(MessageReceivedEvent event, ArrayList<String> args);
}