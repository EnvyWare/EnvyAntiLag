package com.envyful.lag.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.lag.EnvyAntiLag;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;

@Command(
        value = "envyantilag",
        aliases = {
                "antilag",
                "antilagreload",
                "waterdudenerd"
        },
        description = "Reloads antilag config"
)
@Permissible("envyware.anti.lag.command")
public class AntiLagCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSource sender, String[] args) {
        EnvyAntiLag.getInstance().reloadConfig();
        sender.sendMessage(UtilChatColour.colour(
                EnvyAntiLag.getInstance().getLocale().getReloadedMessage()
        ), Util.DUMMY_UUID);
    }
}
