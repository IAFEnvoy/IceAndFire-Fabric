package com.github.alexthe666.citadel.server.world;

import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import java.util.*;

public class CitadelServerData extends PersistentState {
    private static final Map<MinecraftServer, CitadelServerData> dataMap = new HashMap<>();

    private static final String IDENTIFIER = "citadel_world_data";

    private final MinecraftServer server;

    private ServerTickRateTracker tickRateTracker = null;

    public CitadelServerData(MinecraftServer server) {
        super();
        this.server = server;
    }


    public static CitadelServerData get(MinecraftServer server) {
        CitadelServerData fromMap = dataMap.get(server);
        if(fromMap == null){
            PersistentStateManager storage = server.getWorld(World.OVERWORLD).getPersistentStateManager();
            CitadelServerData data = storage.getOrCreate((tag) -> load(server, tag), () -> new CitadelServerData(server), IDENTIFIER);
            if (data != null) {
                data.markDirty();
            }
            dataMap.put(server, data);
            return data;
        }
        return fromMap;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        if(tickRateTracker != null){
            tag.put("TickRateTracker", tickRateTracker.toTag());
        }
        return tag;
    }
    public static CitadelServerData load(MinecraftServer server, NbtCompound tag) {
        CitadelServerData data = new CitadelServerData(server);
        if(tag.contains("TickRateTracker")){
            data.tickRateTracker = new ServerTickRateTracker(server, tag.getCompound("TickRateTracker"));
        }else{
            data.tickRateTracker = new ServerTickRateTracker(server);
        }
        return data;
    }

    public ServerTickRateTracker getOrCreateTickRateTracker(){
        if(tickRateTracker == null){
            tickRateTracker = new ServerTickRateTracker(server);
        }
        return tickRateTracker;
    }

}