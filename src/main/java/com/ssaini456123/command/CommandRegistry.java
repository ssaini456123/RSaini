package com.ssaini456123.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.ssaini456123.command.meta.RCommandMeta;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

/**
 * @author Sutinder S. Saini
 */
public class CommandRegistry {
    @NotNull
    private static final HashMap<String, Command> commandHashMap = new HashMap<String, Command>();
    @NotNull
    private static final HashMap<String, RCommandMeta> metaHashMap = new HashMap<String, RCommandMeta>();

    public CommandRegistry() {
    }

    public void initializeCommands() {
        Reflections reflections = new Reflections("com.ssaini456123.command.commands");
        Set<Class<? extends Command>> subTypes = reflections.getSubTypesOf(Command.class);

        for (Class<? extends Command> ty : subTypes) {
            RCommandMeta commandMeta = ty.getAnnotation(RCommandMeta.class);
            String commandName = commandMeta.name();

            try {
                Command commandInstance = ty.newInstance();

                commandHashMap.put(commandName, commandInstance);
                metaHashMap.put(commandName, commandMeta);

            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            System.out.println(commandMeta.name());
        }
    }

    public boolean commandExists(String commandName) {
        if (commandHashMap.get(commandName) == null) {
            return false;
        }

        return true;
    }

    public Command getCommandInstance(String name) {
        return commandHashMap.get(name);
    }

    @NotNull
    public static HashMap<String, RCommandMeta> getMetaHashMap() {
        return metaHashMap;
    }

    @NotNull
    public static HashMap<String, Command> getCommandHashMap() {
        return commandHashMap;
    }
}
