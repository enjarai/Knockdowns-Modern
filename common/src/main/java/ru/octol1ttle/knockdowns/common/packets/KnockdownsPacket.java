package ru.octol1ttle.knockdowns.common.packets;

import dev.architectury.networking.NetworkManager;
import java.util.function.Supplier;
import net.minecraft.network.PacketByteBuf;

public abstract class KnockdownsPacket {
    public KnockdownsPacket(PacketByteBuf buf) {
        // Decode data into a message
    }

    public KnockdownsPacket(/* args here */) {
        // Message creation
    }

    public abstract void encode(PacketByteBuf buf);

    public abstract void apply(Supplier<NetworkManager.PacketContext> contextSupplier);
}
