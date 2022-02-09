package com.envyful.anti.lag.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigPath("config/EnvyAntiLag/config.yml")
@ConfigSerializable
public class ChunkRedstoneConfig extends AbstractYamlConfig {

    private long resetTimeSeconds = 10;
    private int redstoneEventsBeforeBreak = 60;
    private boolean alertChunk = true;

    public ChunkRedstoneConfig() {
        super();
    }

    public long getResetTimeSeconds() {
        return this.resetTimeSeconds;
    }

    public int getRedstoneEventsBeforeBreak() {
        return this.redstoneEventsBeforeBreak;
    }

    public boolean isAlertChunk() {
        return this.alertChunk;
    }
}
