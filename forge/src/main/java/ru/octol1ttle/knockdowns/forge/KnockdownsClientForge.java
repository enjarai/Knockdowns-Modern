package ru.octol1ttle.knockdowns.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import ru.octol1ttle.knockdowns.common.KnockdownsClient;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = "knockdowns", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KnockdownsClientForge {
    @SubscribeEvent
    public void onInitializeClient(FMLClientSetupEvent event) {
        KnockdownsClient.init();
    }

    @SubscribeEvent
    public static void onHudRender(RenderGuiEvent.Pre event) {
        ru.octol1ttle.knockdowns.common.events.KnockdownsClientEvents.onHudRender(event.getGuiGraphics());
    }
}
