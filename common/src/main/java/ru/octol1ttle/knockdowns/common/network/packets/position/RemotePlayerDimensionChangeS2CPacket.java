package ru.octol1ttle.knockdowns.common.network.packets.position;

import dev.architectury.networking.NetworkManager;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.PacketByteBuf;
import ru.octol1ttle.knockdowns.common.events.KnockdownsClientEvents;
import ru.octol1ttle.knockdowns.common.network.packets.KnockdownsPacket;

public class RemotePlayerDimensionChangeS2CPacket extends KnockdownsPacket {
    private final UUID targetUuid;

    public RemotePlayerDimensionChangeS2CPacket(PacketByteBuf buf) {
        this(buf.readUuid());
    }

    public RemotePlayerDimensionChangeS2CPacket(UUID targetUuid) {
        this.targetUuid = targetUuid;
    }

    @Override
    public void encode(PacketByteBuf buf) {
        buf.writeUuid(this.targetUuid);
    }

    @Override
    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext context = contextSupplier.get();
        context.queue(() -> KnockdownsClientEvents.remotePlayers.put(targetUuid, Optional.empty()));
    }
}
