package com.envyful.anti.lag.listener;

import com.envyful.api.forge.listener.LazyListener;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;
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

        ChunkRedstoneData data = this.getData(new ChunkPos(event.getPos()));
        int power = blockState.getValue(BlockRedstoneWire.POWER);

        if (power != 15) {
            data.setLastValue(power);
            return;
        }

        if ((System.currentTimeMillis() - data.getLastReset()) >= TimeUnit.SECONDS.toMillis(10)) {
            data.setLastReset(System.currentTimeMillis());
            data.setUpdates(0);
            return;
        }

        data.setUpdates(data.getUpdates() + 1);

        if (data.getUpdates() > 30) {
            event.setCanceled(true);
            event.getWorld().setBlockToAir(event.getPos());
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
