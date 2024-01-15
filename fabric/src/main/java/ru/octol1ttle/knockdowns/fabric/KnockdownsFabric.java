package ru.octol1ttle.knockdowns.fabric;

import ru.octol1ttle.knockdowns.common.KnockdownsCommon;
import net.fabricmc.api.ModInitializer;

public class KnockdownsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        KnockdownsCommon.init();
    }
}
