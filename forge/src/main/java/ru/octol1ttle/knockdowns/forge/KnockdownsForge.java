package ru.octol1ttle.knockdowns.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ru.octol1ttle.knockdowns.common.KnockdownsClient;
import ru.octol1ttle.knockdowns.common.KnockdownsCommon;

@SuppressWarnings("unused")
@Mod(KnockdownsCommon.MOD_ID)
public class KnockdownsForge {
    public KnockdownsForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(KnockdownsCommon.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        KnockdownsCommon.init();
    }

    @SubscribeEvent
    public void onInitializeClient(FMLClientSetupEvent event) {
        KnockdownsClient.init();
    }
}
