package com.envyful.anti.lag;

import com.envyful.anti.lag.config.ChunkRedstoneConfig;
import com.envyful.anti.lag.listener.ChunkRedstoneListener;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.player.ForgePlayerManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.IOException;

@Mod(
        modid = EnvyAntiLag.MOD_ID,
        version = EnvyAntiLag.MOD_VERSION,
        name = EnvyAntiLag.MOD_NAME,
        acceptableRemoteVersions = "*"
)
public class EnvyAntiLag {

    public static final String MOD_ID = "envyantilag";
    public static final String MOD_VERSION = "0.0.1";
    public static final String MOD_NAME = "EnvyAntiLag";

    @Mod.Instance(MOD_ID)
    private static EnvyAntiLag instance;

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private ChunkRedstoneConfig config;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        this.reloadConfig();

        new ChunkRedstoneListener();
    }

    public void reloadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(ChunkRedstoneConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {

    }

}
