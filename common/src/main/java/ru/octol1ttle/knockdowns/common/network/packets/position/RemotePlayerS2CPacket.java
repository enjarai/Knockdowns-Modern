package ru.octol1ttle.knockdowns.common.network.packets.position;

import dev.architectury.networking.NetworkManager;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import ru.octol1ttle.knockdowns.common.api.RemotePlayer;
import ru.octol1ttle.knockdowns.common.events.KnockdownsClientEvents;
import ru.octol1ttle.knockdowns.common.network.packets.KnockdownsPacket;

public class RemotePlayerS2CPacket extends KnockdownsPacket {
    private final UUID targetUuid;
    private final Vec3d eyePosition;
    private final boolean knockedDown;

    public RemotePlayerS2CPacket(PacketByteBuf buf) {
        this(buf.readUuid(), new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()), buf.readBoolean());
    }

    public RemotePlayerS2CPacket(UUID targetUuid, Vec3d eyePosition, boolean knockedDown) {
        this.targetUuid = targetUuid;
        this.eyePosition = eyePosition;
        this.knockedDown = knockedDown;
    }

    @Override
    public void encode(PacketByteBuf buf) {
        buf.writeUuid(this.targetUuid);
        buf.writeDouble(this.eyePosition.x);
        buf.writeDouble(this.eyePosition.y);
        buf.writeDouble(this.eyePosition.z);
        buf.writeBoolean(this.knockedDown);
    }

    @Override
    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext context = contextSupplier.get();
        context.queue(() -> {
            //noinspection DataFlowIssue
            if (MinecraftClient.getInstance().player.getUuid().equals(targetUuid)) {
                KnockdownsClientEvents.remotePlayers.clear();
            } else {
                KnockdownsClientEvents.remotePlayers.put(targetUuid, Optional.of(new RemotePlayer(this.eyePosition, this.knockedDown)));
            }
        });
    }
}
