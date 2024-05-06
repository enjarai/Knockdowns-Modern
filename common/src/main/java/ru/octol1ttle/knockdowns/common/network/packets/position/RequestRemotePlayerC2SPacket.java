package ru.octol1ttle.knockdowns.common.network.packets.position;

import dev.architectury.networking.NetworkManager;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;
import ru.octol1ttle.knockdowns.common.network.KnockdownsNetwork;
import ru.octol1ttle.knockdowns.common.network.packets.KnockdownsPacket;

public class RequestRemotePlayerC2SPacket extends KnockdownsPacket {
    private final UUID targetUuid;

    public RequestRemotePlayerC2SPacket(PacketByteBuf buf) {
        this(buf.readUuid());
    }

    public RequestRemotePlayerC2SPacket(UUID targetUuid) {
        this.targetUuid = targetUuid;
    }

    @Override
    public void encode(PacketByteBuf buf) {
        buf.writeUuid(this.targetUuid);
    }

    @Override
    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext context = contextSupplier.get();
        context.queue(() -> {
            PlayerEntity sender = context.getPlayer();
            PlayerEntity target = sender.getEntityWorld().getPlayerByUuid(targetUuid);
            if (target != null) {
                KnockdownsNetwork.sendToPlayer(context.getPlayer(), new RemotePlayerS2CPacket(targetUuid, target.getEyePos(), ((IKnockableDown)target).is_KnockedDown()));
            }
        });
    }
}
