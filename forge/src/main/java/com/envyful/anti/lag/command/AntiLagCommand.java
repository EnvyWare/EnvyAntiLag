package com.envyful.anti.lag.command;

import com.envyful.anti.lag.EnvyAntiLag;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;

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
    public void onCommand(@Sender ICommandSender sender, String[] args) {
        EnvyAntiLag.getInstance().reloadConfig();
        sender.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes(
                '&',
                EnvyAntiLag.getInstance().getLocale().getReloadedMessage()
        )));
    }
}
