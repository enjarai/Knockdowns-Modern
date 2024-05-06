package ru.octol1ttle.knockdowns.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ru.octol1ttle.knockdowns.common.KnockdownsClient;
import ru.octol1ttle.knockdowns.common.KnockdownsCommon;
import ru.octol1ttle.knockdowns.common.events.KnockdownsClientEvents;

@SuppressWarnings("unused")
@Mod(KnockdownsCommon.MOD_ID)
public class KnockdownsForge {
    public KnockdownsForge() {
        // Submit our event bus to let architectury register our content on the right time
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(KnockdownsCommon.MOD_ID, modEventBus);
        modEventBus.addListener(this::onInitializeClient);
        MinecraftForge.EVENT_BUS.register(this);

        KnockdownsCommon.init();
    }

    public void onInitializeClient(FMLClientSetupEvent event) {
        KnockdownsClient.init();
    }

    @SubscribeEvent
    public void onHudRender(RenderGuiEvent.Pre event) {
        KnockdownsClientEvents.onHudRender(event.getGuiGraphics());
    }
}
