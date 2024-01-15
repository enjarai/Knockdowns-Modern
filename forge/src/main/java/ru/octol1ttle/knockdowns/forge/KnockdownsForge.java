package ru.octol1ttle.knockdowns.forge;

import dev.architectury.platform.forge.EventBuses;
import ru.octol1ttle.knockdowns.common.KnockdownsCommon;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(KnockdownsCommon.MOD_ID)
public class KnockdownsForge {
    public KnockdownsForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(KnockdownsCommon.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        KnockdownsCommon.init();
    }
}
