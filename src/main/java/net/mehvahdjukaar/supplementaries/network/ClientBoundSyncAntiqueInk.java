package net.mehvahdjukaar.supplementaries.network;

import net.mehvahdjukaar.supplementaries.Supplementaries;
import net.mehvahdjukaar.supplementaries.capabilities.CapabilityHandler;
import net.mehvahdjukaar.supplementaries.world.data.GlobeData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;


public class ClientBoundSyncAntiqueInk {
    private BlockPos pos;
    private boolean ink;

    public ClientBoundSyncAntiqueInk(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.ink = buffer.readBoolean();
    }

    public ClientBoundSyncAntiqueInk(BlockPos pos, boolean ink) {
        this.pos = pos;
        this.ink = ink;
    }

    public static void buffer(ClientBoundSyncAntiqueInk message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos);
        buffer.writeBoolean(message.ink);
    }

    public static void handler(ClientBoundSyncAntiqueInk message, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (!context.getDirection().getReceptionSide().isServer()) {
                //assigns data to client
                Level world = Objects.requireNonNull(context.getSender()).level;

                BlockEntity tile = world.getBlockEntity(message.pos);
                if(tile != null) {
                    tile.getCapability(CapabilityHandler.ANTIQUE_TEXT_CAP).ifPresent(c -> c.setAntiqueInk(message.ink));
                }
            }
        });
        context.setPacketHandled(true);
    }
}