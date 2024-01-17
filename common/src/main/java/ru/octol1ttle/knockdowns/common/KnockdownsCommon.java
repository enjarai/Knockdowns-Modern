package ru.octol1ttle.knockdowns.common;

import net.minecraft.SharedConstants;
import net.minecraft.entity.player.PlayerEntity;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;
import ru.octol1ttle.knockdowns.common.events.KnockdownsEvents;
import ru.octol1ttle.knockdowns.common.network.KnockdownsNetwork;
import ru.octol1ttle.knockdowns.common.registries.KnockdownsSoundEvents;

public class KnockdownsCommon {
    public static final String MOD_ID = "knockdowns";
    public static final int REVIVE_WAIT_TIME = 10 * SharedConstants.TICKS_PER_SECOND;

    public static void init() {
        KnockdownsSoundEvents.register();
        KnockdownsNetwork.registerPackets();
        KnockdownsEvents.registerCallbacks();
    }

    public static boolean isKnockedOrReviving(PlayerEntity player) {
        return player instanceof IKnockableDown knockable && (knockable.is_KnockedDown() || knockable.is_Reviving());
    }
}
