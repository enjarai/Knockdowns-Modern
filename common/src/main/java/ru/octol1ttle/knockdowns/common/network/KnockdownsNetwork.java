package ru.octol1ttle.knockdowns.common.network;

import dev.architectury.networking.NetworkChannel;
import dev.architectury.networking.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.EntityTrackingListener;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.Identifier;
import ru.octol1ttle.knockdowns.common.KnockdownsCommon;
import ru.octol1ttle.knockdowns.common.network.packets.KnockedDownStatusPacket;
import ru.octol1ttle.knockdowns.common.network.packets.PlayKnockedDownSoundS2CPacket;
import ru.octol1ttle.knockdowns.common.network.packets.ReviveStatusPacket;

public class KnockdownsNetwork {
    private static final NetworkChannel CHANNEL = NetworkChannel.create(new Identifier(KnockdownsCommon.MOD_ID, "main"));
    public static void registerPackets() {
        CHANNEL.register(KnockedDownStatusPacket.SendS2C.class, KnockedDownStatusPacket.SendS2C::encode, KnockedDownStatusPacket.SendS2C::new, KnockedDownStatusPacket.SendS2C::apply);
        CHANNEL.register(KnockedDownStatusPacket.RequestC2S.class, KnockedDownStatusPacket.RequestC2S::encode, KnockedDownStatusPacket.RequestC2S::new, KnockedDownStatusPacket.RequestC2S::apply);

        CHANNEL.register(PlayKnockedDownSoundS2CPacket.class, PlayKnockedDownSoundS2CPacket::encode, PlayKnockedDownSoundS2CPacket::new, PlayKnockedDownSoundS2CPacket::apply);

        CHANNEL.register(ReviveStatusPacket.SendS2C.class, ReviveStatusPacket.SendS2C::encode, ReviveStatusPacket.SendS2C::new, ReviveStatusPacket.SendS2C::apply);
        CHANNEL.register(ReviveStatusPacket.SendC2S.class, ReviveStatusPacket.SendC2S::encode, ReviveStatusPacket.SendC2S::new, ReviveStatusPacket.SendC2S::apply);
        CHANNEL.register(ReviveStatusPacket.RequestC2S.class, ReviveStatusPacket.RequestC2S::encode, ReviveStatusPacket.RequestC2S::new, ReviveStatusPacket.RequestC2S::apply);
        CHANNEL.register(ReviveStatusPacket.RevivedC2S.class, ReviveStatusPacket.RevivedC2S::encode, ReviveStatusPacket.RevivedC2S::new, ReviveStatusPacket.RevivedC2S::apply);
    }

    public static <T> void sendToServer(T message) {
        if (CHANNEL.canServerReceive(message.getClass())) {
            CHANNEL.sendToServer(message);
        }
    }

    public static <T> void sendToPlayer(PlayerEntity player, T message) {
        Packet<?> packet = CHANNEL.toPacket(NetworkManager.Side.S2C, message);
        Class<?> messageClass = message.getClass();

        sendToPlayer(player, packet, messageClass);
    }

    public static void sendToPlayer(PlayerEntity player, Packet<?> packet, Class<?> messageClass) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            throw new IllegalArgumentException("Cannot send to client players");
        }
        if (CHANNEL.canPlayerReceive(serverPlayer, messageClass)) {
            serverPlayer.networkHandler.sendPacket(packet);
        }
    }

    // TODO: PR to Architectury API
    public static <T> void sendToListeners(Entity entity, T message) {
        Packet<?> packet = CHANNEL.toPacket(NetworkManager.Side.S2C, message);
        Class<?> messageClass = message.getClass();

        sendToListeners(entity, packet, messageClass);
    }

    private static void sendToListeners(Entity entity, Packet<?> packet, Class<?> messageClass) {
        ServerChunkManager chunkManager = (ServerChunkManager) entity.getWorld().getChunkManager();
        ThreadedAnvilChunkStorage.EntityTracker entityTracker = chunkManager.threadedAnvilChunkStorage.entityTrackers.get(entity.getId());

        for (EntityTrackingListener listener : entityTracker.listeners) {
            sendToPlayer(listener.getPlayer(), packet, messageClass);
        }
    }

    public static <T> void sendToListenersAndSelf(PlayerEntity player, T message) {
        Packet<?> packet = CHANNEL.toPacket(NetworkManager.Side.S2C, message);
        Class<?> messageClass = message.getClass();
        sendToPlayer(player, packet, messageClass);
        sendToListeners(player, packet, messageClass);
    }

    public static <T> void sendToWorld(ServerWorld world, T message) {
        Packet<?> packet = CHANNEL.toPacket(NetworkManager.Side.S2C, message);
        Class<?> messageClass = message.getClass();

        sendToWorld(world, packet, messageClass);
    }

    private static void sendToWorld(ServerWorld world, Packet<?> packet, Class<?> messageClass) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            sendToPlayer(player, packet, messageClass);
        }
    }
}
