package ru.octol1ttle.knockdowns.common;

import ru.octol1ttle.knockdowns.common.events.KnockdownsClientEvents;
import ru.octol1ttle.knockdowns.common.events.KnockdownsEvents;
import ru.octol1ttle.knockdowns.common.registries.KnockdownsSoundEvents;

public class KnockdownsCommon {
    public static final String MOD_ID = "knockdowns";

    public static void init() {
        KnockdownsSoundEvents.register();
        KnockdownsNetwork.registerPackets();
        KnockdownsClientEvents.registerCallbacks();
        KnockdownsEvents.registerCallbacks();
    }
}
