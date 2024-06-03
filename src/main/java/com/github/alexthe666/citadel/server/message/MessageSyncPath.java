package com.github.alexthe666.citadel.server.message;


import com.github.alexthe666.citadel.client.render.pathfinding.PathfindingDebugRenderer;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.MNode;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Message to sync some path over to the client.
 */
public class MessageSyncPath {
    /**
     * Set of visited nodes.
     */
    public Set<MNode> lastDebugNodesVisited = new HashSet<>();

    /**
     * Set of not visited nodes.
     */
    public Set<MNode> lastDebugNodesNotVisited = new HashSet<>();

    /**
     * Set of chosen nodes for the path.
     */
    public Set<MNode> lastDebugNodesPath = new HashSet<>();

    /**
     * Create a new path message with the filled pathpoints.
     */
    public MessageSyncPath(final Set<MNode> lastDebugNodesVisited, final Set<MNode> lastDebugNodesNotVisited, final Set<MNode> lastDebugNodesPath) {
        super();
        this.lastDebugNodesVisited = lastDebugNodesVisited;
        this.lastDebugNodesNotVisited = lastDebugNodesNotVisited;
        this.lastDebugNodesPath = lastDebugNodesPath;
    }

    public void write(final PacketByteBuf buf) {
        buf.writeInt(lastDebugNodesVisited.size());
        for (final MNode MNode : lastDebugNodesVisited) {
            MNode.serializeToBuf(buf);
        }

        buf.writeInt(lastDebugNodesNotVisited.size());
        for (final MNode MNode : lastDebugNodesNotVisited) {
            MNode.serializeToBuf(buf);
        }

        buf.writeInt(lastDebugNodesPath.size());
        for (final MNode MNode : lastDebugNodesPath) {
            MNode.serializeToBuf(buf);
        }
    }

    public static MessageSyncPath read(final PacketByteBuf buf) {
        int size = buf.readInt();

        Set<MNode> lastDebugNodesVisited = new HashSet<>();
        for (int i = 0; i < size; i++) {
            lastDebugNodesVisited.add(new MNode(buf));
        }

        size = buf.readInt();
        Set<MNode> lastDebugNodesNotVisited = new HashSet<>();
        for (int i = 0; i < size; i++) {
            lastDebugNodesNotVisited.add(new MNode(buf));
        }

        size = buf.readInt();
        Set<MNode> lastDebugNodesPath = new HashSet<>();
        for (int i = 0; i < size; i++) {
            lastDebugNodesPath.add(new MNode(buf));
        }

        return new MessageSyncPath(lastDebugNodesVisited, lastDebugNodesNotVisited, lastDebugNodesPath);
    }

    public static class Handler {
        public Handler() {
        }

        public static boolean handle(MessageSyncPath message, Supplier<NetworkEvent.Context> contextSupplier) {
            contextSupplier.get().enqueueWork(() -> {
                contextSupplier.get().setPacketHandled(true);

                if (contextSupplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                    PathfindingDebugRenderer.lastDebugNodesVisited = message.lastDebugNodesVisited;
                    PathfindingDebugRenderer.lastDebugNodesNotVisited = message.lastDebugNodesNotVisited;
                    PathfindingDebugRenderer.lastDebugNodesPath = message.lastDebugNodesPath;
                }
            });
            return true;
        }
    }

}