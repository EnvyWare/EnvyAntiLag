package com.envyful.anti.lag.listener;

import com.envyful.anti.lag.EnvyAntiLag;
import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.listener.LazyListener;
import com.envyful.api.forge.player.util.UtilPlayer;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ChunkRedstoneListener extends LazyListener {

    private final Map<ChunkPos, ChunkRedstoneData> chunkRedstoneData = Maps.newHashMap();

    public ChunkRedstoneListener() {
        super();
    }

    @SubscribeEvent
    public void onRedstoneWiring(BlockEvent.NeighborNotifyEvent event) {
        IBlockState blockState = event.getWorld().getBlockState(event.getPos());

        if (!(blockState.getBlock() instanceof BlockRedstoneWire)) {
            return;
        }

        ChunkPos chunkPos = new ChunkPos(event.getPos());
        ChunkRedstoneData data = this.getData(chunkPos);
        int power = blockState.getValue(BlockRedstoneWire.POWER);

        if (power != 15) {
            data.setLastValue(power);
            return;
        }

        if ((System.currentTimeMillis() - data.getLastReset()) >= TimeUnit.SECONDS.toMillis(EnvyAntiLag.getInstance().getConfig().getResetTimeSeconds())) {
            data.setLastReset(System.currentTimeMillis());
            data.setUpdates(0);
            return;
        }

        data.setUpdates(data.getUpdates() + 1);

        if (data.getUpdates() > EnvyAntiLag.getInstance().getConfig().getRedstoneEventsBeforeBreak()) {
            if (EnvyAntiLag.getInstance().getConfig().isRemoveRedstone()) {
                event.setCanceled(true);
                event.getWorld().setBlockToAir(event.getPos());
            }

            UtilConcurrency.runAsync(() -> {
                if (EnvyAntiLag.getInstance().getConfig().isAlertChunk()) {
                    for (EntityPlayer playerEntity :
                            FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
                        if (!Objects.equals(playerEntity.world, event.getWorld())) {
                            continue;
                        }

                        if (playerEntity.chunkCoordX == chunkPos.x && playerEntity.chunkCoordZ == chunkPos.z) {
                            for (String s : EnvyAntiLag.getInstance().getLocale().getLagMachineFound()) {
                                playerEntity.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&', s)));
                            }
                        }
                    }
                }

                for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
                    if (EnvyAntiLag.getInstance().getConfig().isAlertChunk()) {
                        if (!Objects.equals(player.world, event.getWorld())) {
                            continue;
                        }
                        if (player.chunkCoordX == chunkPos.x && player.chunkCoordZ == chunkPos.z) {
                            for (String s : EnvyAntiLag.getInstance().getLocale().getLagMachineFound()) {
                                player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&', s)));
                            }
                        }
                    }

                    if (UtilPlayer.hasPermission(player,
                                                 EnvyAntiLag.getInstance().getConfig().getAdminAlertPermission())) {
                        for (String s : EnvyAntiLag.getInstance().getLocale().getAdminAlert()) {
                            ITextComponent component =
                                    new TextComponentString(UtilChatColour.translateColourCodes('&', s
                                    .replace("%chunk%", chunkPos.x + ", " + chunkPos.z)));

                            component.setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                                                        "/minecraft:tp " + player.getName() + " " + event.getPos().getX() + " " + event.getPos().getY() + " " + event.getPos().getZ())));
                            player.sendMessage(component);
                        }
                    }
                }
            });
        }
    }

    private ChunkRedstoneData getData(ChunkPos pos) {
        return chunkRedstoneData.computeIfAbsent(pos, ___ -> new ChunkRedstoneData(0, System.currentTimeMillis(), 0,
                                                                                   System.currentTimeMillis()
        ));
    }

    public static class ChunkRedstoneData {

        private int lastValue;
        private long lastUpdate;
        private int updates;
        private long lastReset;

        public ChunkRedstoneData(int lastValue, long lastUpdate, int updates, long lastReset) {
            this.lastValue = lastValue;
            this.lastUpdate = lastUpdate;
            this.updates = updates;
            this.lastReset = lastReset;
        }

        public int getLastValue() {
            return this.lastValue;
        }

        public long getLastUpdate() {
            return this.lastUpdate;
        }

        public int getUpdates() {
            return this.updates;
        }

        public long getLastReset() {
            return this.lastReset;
        }

        public void setLastValue(int lastValue) {
            this.lastValue = lastValue;
        }

        public void setLastUpdate(long lastUpdate) {
            this.lastUpdate = lastUpdate;
        }

        public void setUpdates(int updates) {
            this.updates = updates;
        }

        public void setLastReset(long lastReset) {
            this.lastReset = lastReset;
        }
    }
}
