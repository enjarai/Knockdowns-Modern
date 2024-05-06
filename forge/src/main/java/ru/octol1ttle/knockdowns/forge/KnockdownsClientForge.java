package ru.octol1ttle.knockdowns.forge;

import net.minecraftforge.client.event.RenderGuiEvent;

public class KnockdownsClientForge {
    public static void onHudRender(RenderGuiEvent.Pre event) {
        ru.octol1ttle.knockdowns.common.events.KnockdownsClientEvents.onHudRender(event.getGuiGraphics());
    }
}
