package com.envyful.anti.lag;

import com.envyful.anti.lag.listener.ChunkRedstoneListener;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.player.ForgePlayerManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

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

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        new ChunkRedstoneListener();
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {

    }

}
