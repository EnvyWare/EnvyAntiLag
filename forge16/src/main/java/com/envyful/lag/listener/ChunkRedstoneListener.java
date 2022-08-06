package com.envyful.lag.listener;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.listener.LazyListener;
import com.envyful.api.forge.player.util.UtilPlayer;
import com.envyful.lag.EnvyAntiLag;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

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
        BlockState blockState = event.getWorld().getBlockState(event.getPos());

        if (!(blockState.getBlock() instanceof RedstoneWireBlock)) {
            return;
        }

        ChunkPos chunkPos = new ChunkPos(event.getPos());
        ChunkRedstoneData data = this.getData(chunkPos);
        int power = blockState.get(RedstoneWireBlock.POWER);

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
                event.getWorld().setBlockState(event.getPos(), Blocks.AIR.getDefaultState(), Constants.BlockFlags.BLOCK_UPDATE);
            }

            UtilConcurrency.runAsync(() -> {
                if (EnvyAntiLag.getInstance().getConfig().isAlertChunk()) {
                    for (ServerPlayerEntity playerEntity : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                        if (!Objects.equals(playerEntity.world, event.getWorld())) {
                            continue;
                        }

                        if (playerEntity.chunkCoordX == chunkPos.x && playerEntity.chunkCoordZ == chunkPos.z) {
                            for (String s : EnvyAntiLag.getInstance().getLocale().getLagMachineFound()) {
                                playerEntity.sendMessage(UtilChatColour.colour(s), Util.DUMMY_UUID);
                            }
                        }
                    }
                }

                for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                    if (EnvyAntiLag.getInstance().getConfig().isAlertChunk()) {
                        if (!Objects.equals(player.world, event.getWorld())) {
                            continue;
                        }
                        if (player.chunkCoordX == chunkPos.x && player.chunkCoordZ == chunkPos.z) {
                            for (String s : EnvyAntiLag.getInstance().getLocale().getLagMachineFound()) {
                                player.sendMessage(UtilChatColour.colour(s), Util.DUMMY_UUID);
                            }
                        }
                    }

                    if (UtilPlayer.hasPermission(player,
                                                 EnvyAntiLag.getInstance().getConfig().getAdminAlertPermission())) {
                        for (String s : EnvyAntiLag.getInstance().getLocale().getAdminAlert()) {
                            ITextComponent component = UtilChatColour.colour(s.replace("%chunk%", chunkPos.x + ", " + chunkPos.z)
                                    .replace("%x%", event.getPos().getX() + "")
                                    .replace("%y%", event.getPos().getY() + "")
                                    .replace("%z%", event.getPos().getZ() + "")
                            );
                            player.sendMessage(component, Util.DUMMY_UUID);
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
