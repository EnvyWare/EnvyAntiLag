package com.envyful.lag;

import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.lag.command.AntiLagCommand;
import com.envyful.lag.config.ChunkRedstoneConfig;
import com.envyful.lag.config.ChunkRedstoneLocale;
import com.envyful.lag.listener.ChunkRedstoneListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import java.io.IOException;

@Mod(EnvyAntiLag.MOD_ID)
public class EnvyAntiLag {

    public static final String MOD_ID = "envyantilag";

    private static EnvyAntiLag instance;

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private ChunkRedstoneConfig config;
    private ChunkRedstoneLocale locale;

    public EnvyAntiLag() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onInit(FMLServerStartingEvent event) {
        instance = this;
        this.reloadConfig();

        new ChunkRedstoneListener();
    }

    public void reloadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(ChunkRedstoneConfig.class);
            this.locale = YamlConfigFactory.getInstance(ChunkRedstoneLocale.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onServerStarting(RegisterCommandsEvent event) {
        this.commandFactory.registerCommand(event.getDispatcher(), new AntiLagCommand());
    }

    public static EnvyAntiLag getInstance() {
        return instance;
    }

    public ChunkRedstoneConfig getConfig() {
        return this.config;
    }

    public ChunkRedstoneLocale getLocale() {
        return this.locale;
    }
}
