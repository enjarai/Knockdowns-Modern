package ru.octol1ttle.knockdowns.common.packets;

import dev.architectury.networking.NetworkManager;
import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import ru.octol1ttle.knockdowns.common.registries.KnockdownsSoundEvents;
import ru.octol1ttle.knockdowns.common.registries.KnockedDownSoundInstance;

public class PlayKnockedDownSoundS2CPacket extends KnockdownsPacket {
    private final double x;
    private final double y;
    private final double z;

    public PlayKnockedDownSoundS2CPacket(PacketByteBuf buf) {
        this(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public PlayKnockedDownSoundS2CPacket(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void encode(PacketByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }

    @Override
    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext context = contextSupplier.get();
        context.queue(() -> MinecraftClient.getInstance().getSoundManager().play(
                new KnockedDownSoundInstance(KnockdownsSoundEvents.KNOCKED_DOWN.get(), new Vec3d(this.x, this.y, this.z))
        ));
    }
}
