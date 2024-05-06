package ru.octol1ttle.knockdowns.common.network;

import dev.architectury.networking.NetworkChannel;
import dev.architectury.networking.NetworkManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import ru.octol1ttle.knockdowns.common.KnockdownsCommon;
import ru.octol1ttle.knockdowns.common.network.packets.PlayKnockedDownSoundS2CPacket;
import ru.octol1ttle.knockdowns.common.network.packets.position.RemotePlayerDimensionChangeS2CPacket;
import ru.octol1ttle.knockdowns.common.network.packets.position.RemotePlayerS2CPacket;
import ru.octol1ttle.knockdowns.common.network.packets.position.RequestRemotePlayerC2SPacket;
import ru.octol1ttle.knockdowns.common.network.packets.reviving.RequestStartRevivingC2SPacket;
import ru.octol1ttle.knockdowns.common.network.packets.reviving.StopRevivingC2SPacket;

public class KnockdownsNetwork {
    private static final NetworkChannel CHANNEL = NetworkChannel.create(new Identifier(KnockdownsCommon.MOD_ID, "main"));
    public static void registerPackets() {
        CHANNEL.register(PlayKnockedDownSoundS2CPacket.class, PlayKnockedDownSoundS2CPacket::encode, PlayKnockedDownSoundS2CPacket::new, PlayKnockedDownSoundS2CPacket::apply);

        CHANNEL.register(RequestStartRevivingC2SPacket.class, RequestStartRevivingC2SPacket::encode, RequestStartRevivingC2SPacket::new, RequestStartRevivingC2SPacket::apply);
        CHANNEL.register(StopRevivingC2SPacket.class, StopRevivingC2SPacket::encode, StopRevivingC2SPacket::new, StopRevivingC2SPacket::apply);

        CHANNEL.register(RequestRemotePlayerC2SPacket.class, RequestRemotePlayerC2SPacket::encode, RequestRemotePlayerC2SPacket::new, RequestRemotePlayerC2SPacket::apply);
        CHANNEL.register(RemotePlayerS2CPacket.class, RemotePlayerS2CPacket::encode, RemotePlayerS2CPacket::new, RemotePlayerS2CPacket::apply);
        CHANNEL.register(RemotePlayerDimensionChangeS2CPacket.class, RemotePlayerDimensionChangeS2CPacket::encode, RemotePlayerDimensionChangeS2CPacket::new, RemotePlayerDimensionChangeS2CPacket::apply);
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
