package com.ssaini456123.command;

import java.util.HashMap;
import java.util.Set;

import com.ssaini456123.command.meta.RCommandMeta;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

/**
 * @author Sutinder S. Saini
 */
public class CommandRegistry {
    @NotNull
    private static final HashMap<String, Class<? extends Command>> commandHashMap = new HashMap<String, Class<? extends Command>>();
    @NotNull
    private static final HashMap<String, RCommandMeta> metaHashMap = new HashMap<String, RCommandMeta>();

    public CommandRegistry() {
    }

    public void initializeCommands() {
        Reflections reflections = new Reflections("com.ssaini456123.command.commands");
        Set<Class<? extends Command>> subTypes = reflections.getSubTypesOf(Command.class);

        for (Class<? extends Command> ty : subTypes) {
            RCommandMeta commandMeta = ty.getAnnotation(RCommandMeta.class);
            System.out.println(commandMeta.name());
        }
    }

    @NotNull
    public static HashMap<String, Class<? extends Command>> getCommandHashMap() {
        return commandHashMap;
    }

    @NotNull
    public static HashMap<String, RCommandMeta> getMetaHashMap() {
        return metaHashMap;
    }
}
