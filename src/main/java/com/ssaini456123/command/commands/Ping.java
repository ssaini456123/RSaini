package com.ssaini456123.command.commands;

import com.ssaini456123.command.CommandCategory;
import com.ssaini456123.command.CommandPermission;
import com.ssaini456123.command.RCommandMeta;

@RCommandMeta(
        name = "ping",
        description = "Replies with `pong!`",
        category = CommandCategory.MISC,
        permission = CommandPermission.USER
)
public class Ping {
}
