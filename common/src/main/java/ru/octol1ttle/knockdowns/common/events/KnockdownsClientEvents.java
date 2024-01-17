package ru.octol1ttle.knockdowns.common.events;

import dev.architectury.event.events.client.ClientGuiEvent;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Formatting;
import ru.octol1ttle.knockdowns.common.KnockdownsClient;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;

public class KnockdownsClientEvents {
    public static void registerCallbacks() {
        registerOnHudRender();
    }

    private static void registerOnHudRender() {
        ClientGuiEvent.RENDER_HUD.register((drawContext, tickDelta) -> {
            IKnockableDown reviving = (IKnockableDown) KnockdownsClient.reviving;
            MinecraftClient client = MinecraftClient.getInstance();
            if (reviving == null) {
                reviving = (IKnockableDown) client.player;
                if (reviving == null || reviving.get_ReviverCount() == 0) {
                    return;
                }
            }

            TextRenderer renderer = client.textRenderer;

            String timerText = String.format("%.1f", reviving.get_ReviveTimer() / (float) SharedConstants.TICKS_PER_SECOND);
            int timerX = (drawContext.getScaledWindowWidth() - renderer.getWidth(timerText)) / 2;

            int reviverCount = reviving.get_ReviverCount();
            Integer color = reviverCount > 1 ? Formatting.GREEN.getColorValue() : Formatting.WHITE.getColorValue();

            String reviverCountText = "x" + reviverCount;
            int reviveCountX = (drawContext.getScaledWindowWidth() - renderer.getWidth(reviverCountText)) / 2;

            if (color != null) {
                drawContext.drawTextWithShadow(renderer, timerText, timerX, drawContext.getScaledWindowHeight() / 2 + 5, color);
                drawContext.drawTextWithShadow(renderer, reviverCountText, reviveCountX, drawContext.getScaledWindowHeight() / 2 + 14, color);
            }
        });
    }
}
