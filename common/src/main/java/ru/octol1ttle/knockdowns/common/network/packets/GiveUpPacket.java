package ru.octol1ttle.knockdowns.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

import java.util.function.Supplier;

public class GiveUpPacket extends KnockdownsPacket {
    public GiveUpPacket(PacketByteBuf buf) {

    }

    public GiveUpPacket() {

    }

    @Override
    public void encode(PacketByteBuf buf) {

    }

    @Override
    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext context = contextSupplier.get();
        context.queue(() -> {
            PlayerEntity player = context.getPlayer();
            if (player.getRecentDamageSource() == null) {
                player.kill();
            } else {
                player.damage(player.getRecentDamageSource(), Float.MAX_VALUE);
            }
        });
    }
}
