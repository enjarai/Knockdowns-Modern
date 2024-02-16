package ru.octol1ttle.knockdowns.common;

import net.minecraft.SharedConstants;
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
}
