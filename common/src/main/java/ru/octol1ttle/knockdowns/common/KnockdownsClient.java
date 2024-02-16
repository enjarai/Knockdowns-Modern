package ru.octol1ttle.knockdowns.common;

import dev.architectury.event.EventResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;
import ru.octol1ttle.knockdowns.common.events.KnockdownsClientEvents;
import ru.octol1ttle.knockdowns.common.network.KnockdownsNetwork;
import ru.octol1ttle.knockdowns.common.network.packets.RequestStartRevivingC2SPacket;
import ru.octol1ttle.knockdowns.common.network.packets.StopRevivingC2SPacket;
import ru.octol1ttle.knockdowns.common.registries.KnockdownsSoundEvents;
import ru.octol1ttle.knockdowns.common.registries.KnockedDownSoundInstance;

public class KnockdownsClient {
    @Nullable
    public static Entity reviving;

    public static void init() {
        KnockdownsClientEvents.registerCallbacks();
    }

    public static void playKnockedDownSound(Vec3d pos) {
        MinecraftClient.getInstance().getSoundManager().play(
                new KnockedDownSoundInstance(KnockdownsSoundEvents.KNOCKED_DOWN.get(), pos)
        );
    }

    public static EventResult onEntityUse(PlayerEntity player, Entity entity) {
        if (KnockdownsUtils.isKnockedOrReviving(player) || !(entity instanceof IKnockableDown knockable) || !knockable.is_KnockedDown()) {
            return EventResult.pass();
        }

        KnockdownsNetwork.sendToServer(new RequestStartRevivingC2SPacket(entity.getUuid()));
        reviving = entity;

        return EventResult.interruptTrue();
    }

    public static void onPlayerTick(PlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!player.equals(client.player) || reviving == null) {
            return;
        }

        boolean playerKnocked = ((IKnockableDown) player).is_KnockedDown();
        boolean revivingTargeted = client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.ENTITY
                && ((EntityHitResult) client.crosshairTarget).getEntity().equals(reviving);

        if (!(reviving instanceof IKnockableDown knockable)) {
            return;
        }

        if (!knockable.is_KnockedDown() || playerKnocked || !revivingTargeted) {
            KnockdownsNetwork.sendToServer(new StopRevivingC2SPacket(reviving.getUuid()));
            reviving = null;
        }
    }
}
