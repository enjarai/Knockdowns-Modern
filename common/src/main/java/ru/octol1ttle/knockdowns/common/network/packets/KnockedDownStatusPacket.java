package ru.octol1ttle.knockdowns.common.network.packets;

import dev.architectury.networking.NetworkManager;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.PacketByteBuf;
import ru.octol1ttle.knockdowns.common.network.KnockdownsNetwork;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;

public class KnockedDownStatusPacket {
    public static class SendS2C extends KnockdownsPacket {
        private final UUID playerUuid;
        private final boolean knockedDown;

        public SendS2C(PacketByteBuf buf) {
            this(buf.readUuid(), buf.readBoolean());
        }

        public SendS2C(UUID playerUuid, boolean knockedDown) {
            this.playerUuid = playerUuid;
            this.knockedDown = knockedDown;
        }

        @Override
        public void encode(PacketByteBuf buf) {
            buf.writeUuid(this.playerUuid);
            buf.writeBoolean(this.knockedDown);
        }

        @Override
        public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
            NetworkManager.PacketContext context = contextSupplier.get();
            context.queue(() -> {
                IKnockableDown knockableDown = (IKnockableDown) context.getPlayer().getWorld().getPlayerByUuid(this.playerUuid);
                if (knockableDown != null) {
                    knockableDown.knockdowns$setKnockedDown(this.knockedDown);
                }
            });
        }
    }

    public static class RequestC2S extends KnockdownsPacket {
        private final UUID playerUuid;

        public RequestC2S(PacketByteBuf buf) {
            this(buf.readUuid());
        }

        public RequestC2S(UUID playerUuid) {
            this.playerUuid = playerUuid;
        }

        @Override
        public void encode(PacketByteBuf buf) {
            buf.writeUuid(this.playerUuid);
        }

        @Override
        public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
            NetworkManager.PacketContext context = contextSupplier.get();
            context.queue(() -> {
                IKnockableDown knockableDown = (IKnockableDown) context.getPlayer().getWorld().getPlayerByUuid(this.playerUuid);
                if (knockableDown != null) {
                    KnockdownsNetwork.sendToPlayer(context.getPlayer(), new SendS2C(this.playerUuid, knockableDown.knockdowns$isKnockedDown()));
                }
            });
        }
    }
}
