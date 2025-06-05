package com.ssaini456123.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The metadata that is used to create commands in RSaini. <br>
 * A command in RSaini must <b>NOT</b> have the possibility of containing aliases.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RCommandMeta {
    /**
     * The main name of this command.
     * @return The command name.
     */
    String name();

    /**
     * A descriptive sentence about the command, and what it does
     * @return The description
     */
    String description();

    /**
     * The category the command fits
     * @return The category the command fits in
     */
    CommandCategory category();

    /**
     * What kind of permissions the user must have before executing
     * the command.
     * @return The permission required for execution.
     */
    CommandPermission permission();
}