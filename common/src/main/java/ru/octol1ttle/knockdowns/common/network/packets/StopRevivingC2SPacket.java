package ru.octol1ttle.knockdowns.common.network.packets;

import dev.architectury.networking.NetworkManager;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;

public class StopRevivingC2SPacket extends KnockdownsPacket {
    private final UUID targetUuid;

    public StopRevivingC2SPacket(PacketByteBuf buf) {
        this(buf.readUuid());
    }

    public StopRevivingC2SPacket(UUID targetUuid) {
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
            PlayerEntity player = context.getPlayer();
            IKnockableDown playerKnockable = (IKnockableDown) player;
            IKnockableDown targetKnockable = (IKnockableDown) player.getWorld().getPlayerByUuid(this.targetUuid);
            if (playerKnockable.is_Reviving() && targetKnockable != null) {
                playerKnockable.set_Reviving(false);
                if (targetKnockable.is_KnockedDown()) {
                    targetKnockable.set_ReviverCount(targetKnockable.get_ReviverCount() - 1);
                }
            }
        });
    }
}
