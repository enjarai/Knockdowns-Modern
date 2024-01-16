package ru.octol1ttle.knockdowns.common.events;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.InteractionEvent;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import ru.octol1ttle.knockdowns.common.KnockdownsNetwork;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;
import ru.octol1ttle.knockdowns.common.packets.KnockedDownStatusPacket;
import ru.octol1ttle.knockdowns.common.packets.ReviveStatusPacket;

public class KnockdownsClientEvents {
    private static final int REVIVAL_WAIT_TIME = 10 * SharedConstants.TICKS_PER_SECOND;
    private static IKnockableDown reviving = null;
    private static int revivalTimer = -1;

    public static void registerCallbacks() {
        registerOnEntityLoad();
        registerOnEntityUse();
        registerOnWorldTick();
        registerOnHudRender();
    }

    private static void registerOnEntityLoad() {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(player -> {
            UUID playerUuid = player.getUuid();
            KnockdownsNetwork.sendToServer(new KnockedDownStatusPacket.RequestC2S(playerUuid));
            KnockdownsNetwork.sendToServer(new ReviveStatusPacket.RequestC2S(playerUuid));
        });
    }

    private static void registerOnEntityUse() {
        InteractionEvent.INTERACT_ENTITY.register((player, entity, hand) -> {
            if (!(entity instanceof IKnockableDown knockableEntity) || !knockableEntity.knockdowns$isKnockedDown()
                    || knockableEntity.knockdowns$isBeingRevived()) {
                return EventResult.pass();
            }

            IKnockableDown self = (IKnockableDown) player;
            if (self.knockdowns$isKnockedDown()) {
                return EventResult.interruptFalse();
            }

            knockableEntity.knockdowns$setBeingRevived(true);
            KnockdownsNetwork.sendToServer(new ReviveStatusPacket.SendC2S(entity.getUuid(), true));

            reviving = knockableEntity;
            revivalTimer = REVIVAL_WAIT_TIME;

            return EventResult.interruptTrue();
        });
    }

    private static void registerOnWorldTick() {
        ClientTickEvent.ClientLevel.CLIENT_LEVEL_POST.register(world -> {
            boolean revived = false;
            revivalTimer--;
            if (revivalTimer <= 0) {
                revivalTimer = -1;
                revived = true;
            }

            if (reviving == null) {
                return;
            }

            HitResult crosshairTarget = MinecraftClient.getInstance().crosshairTarget;
            if (revived || crosshairTarget == null || crosshairTarget.getType() != HitResult.Type.ENTITY
                    || !((EntityHitResult) crosshairTarget).getEntity().getUuid().equals(reviving.knockdowns$getUuid())) {
                reviving.knockdowns$setBeingRevived(false);

                KnockdownsNetwork.sendToServer(new ReviveStatusPacket.SendC2S(reviving.knockdowns$getUuid(), false));
                if (revived) {
                    reviving.knockdowns$setKnockedDown(false);

                    KnockdownsNetwork.sendToServer(new ReviveStatusPacket.RevivedC2S(reviving.knockdowns$getUuid()));
                }

                reviving = null;
                revivalTimer = -1;
            }
        });
    }

    private static void registerOnHudRender() {
        ClientGuiEvent.RENDER_HUD.register((drawContext, tickDelta) -> {
            if (revivalTimer == -1) {
                return;
            }

            TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
            String text = String.format("%.1f", revivalTimer / (float) SharedConstants.TICKS_PER_SECOND);
            int x = (drawContext.getScaledWindowWidth() - renderer.getWidth(text)) / 2;

            drawContext.drawTextWithShadow(renderer, text, x, drawContext.getScaledWindowHeight() / 2 + 15, 0xFFFFFF);
        });
    }
}
