package ru.octol1ttle.knockdowns.common.network.packets;

import dev.architectury.networking.NetworkManager;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import ru.octol1ttle.knockdowns.common.network.KnockdownsNetwork;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;

public class ReviveStatusPacket {
    public static class SendS2C extends KnockdownsPacket {
        private final UUID playerUuid;
        private final boolean beingRevived;

        public SendS2C(PacketByteBuf buf) {
            this(buf.readUuid(), buf.readBoolean());
        }

        public SendS2C(UUID playerUuid, boolean beingRevived) {
            this.playerUuid = playerUuid;
            this.beingRevived = beingRevived;
        }

        @Override
        public void encode(PacketByteBuf buf) {
            buf.writeUuid(this.playerUuid);
            buf.writeBoolean(this.beingRevived);
        }

        @Override
        public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
            NetworkManager.PacketContext context = contextSupplier.get();
            context.queue(() -> {
                IKnockableDown knockableDown = (IKnockableDown) context.getPlayer().getWorld().getPlayerByUuid(this.playerUuid);
                if (knockableDown != null) {
                    knockableDown.knockdowns$setBeingRevived(this.beingRevived);
                }
            });
        }
    }

    public static class SendC2S extends KnockdownsPacket {
        private final UUID playerUuid;
        private final boolean beingRevived;

        public SendC2S(PacketByteBuf buf) {
            this(buf.readUuid(), buf.readBoolean());
        }

        public SendC2S(UUID playerUuid, boolean beingRevived) {
            this.playerUuid = playerUuid;
            this.beingRevived = beingRevived;
        }

        @Override
        public void encode(PacketByteBuf buf) {
            buf.writeUuid(this.playerUuid);
            buf.writeBoolean(this.beingRevived);
        }

        @Override
        public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
            NetworkManager.PacketContext context = contextSupplier.get();
            context.queue(() -> {
                IKnockableDown knockableDown = (IKnockableDown) context.getPlayer().getWorld().getPlayerByUuid(this.playerUuid);
                if (knockableDown != null) {
                    knockableDown.knockdowns$setBeingRevived(this.beingRevived);
                    KnockdownsNetwork.sendToListenersAndSelf(context.getPlayer(), new ReviveStatusPacket.SendS2C(this.playerUuid, this.beingRevived));
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
                    KnockdownsNetwork.sendToPlayer(context.getPlayer(), new ReviveStatusPacket.SendS2C(this.playerUuid, knockableDown.knockdowns$isBeingRevived()));
                }
            });
        }
    }

    public static class RevivedC2S extends KnockdownsPacket {
        private final UUID playerUuid;

        public RevivedC2S(PacketByteBuf buf) {
            this(buf.readUuid());
        }

        public RevivedC2S(UUID playerUuid) {
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
                PlayerEntity reviving = context.getPlayer().getWorld().getPlayerByUuid(this.playerUuid);
                IKnockableDown knockableDown = (IKnockableDown) reviving;
                if (knockableDown == null || !knockableDown.knockdowns$isKnockedDown()) {
                    return;
                }

                reviving.setInvulnerable(false);
                reviving.setGlowing(false);
                reviving.setHealth(6.0f);

                knockableDown.knockdowns$setKnockedDown(false);
                KnockdownsNetwork.sendToListenersAndSelf(reviving, new KnockedDownStatusPacket.SendS2C(reviving.getUuid(), false));
            });
        }
    }
}
