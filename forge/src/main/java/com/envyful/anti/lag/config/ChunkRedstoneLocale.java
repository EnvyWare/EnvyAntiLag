package com.envyful.anti.lag.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.google.common.collect.Lists;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigPath("config/EnvyAntiLag/locale.yml")
@ConfigSerializable
public class ChunkRedstoneLocale extends AbstractYamlConfig {

    private List<String> lagMachineFound = Lists.newArrayList(
            " ",
            "&c&l(!) &cRedstone clock&7 was found in this chunk! Has been broken now. Please refrain from doing this",
            " "
    );

    private List<String> adminAlert = Lists.newArrayList(
            "&c&lALERT!!! LAG MACHINE FOUND IN %chunk% &7CLICK ME TO TP PLES"
    );

    private String reloadedMessage = "&e&l(!) &eReloaded...";

    public ChunkRedstoneLocale() {
        super();
    }

    public List<String> getLagMachineFound() {
        return this.lagMachineFound;
    }

    public List<String> getAdminAlert() {
        return this.adminAlert;
    }

    public String getReloadedMessage() {
        return this.reloadedMessage;
    }
}
