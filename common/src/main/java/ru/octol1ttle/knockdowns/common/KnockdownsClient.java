package ru.octol1ttle.knockdowns.common;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import ru.octol1ttle.knockdowns.common.events.KnockdownsClientEvents;
import ru.octol1ttle.knockdowns.common.registries.KnockdownsSoundEvents;
import ru.octol1ttle.knockdowns.common.registries.KnockedDownSoundInstance;

public class KnockdownsClient {
    public static void init() {
        KnockdownsClientEvents.registerCallbacks();
    }

    public static void playKnockedDownSound(Vec3d pos) {
        MinecraftClient.getInstance().getSoundManager().play(
                new KnockedDownSoundInstance(KnockdownsSoundEvents.KNOCKED_DOWN.get(), pos)
        );
    }
}
