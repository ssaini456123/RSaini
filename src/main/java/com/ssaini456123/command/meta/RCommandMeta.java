package com.ssaini456123.command.meta;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Sutinder S. Saini
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