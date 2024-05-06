package ru.octol1ttle.knockdowns.common.events;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import ru.octol1ttle.knockdowns.common.KnockdownsClient;
import ru.octol1ttle.knockdowns.common.KnockdownsCommon;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;
import ru.octol1ttle.knockdowns.common.api.RemotePlayer;
import ru.octol1ttle.knockdowns.common.network.KnockdownsNetwork;
import ru.octol1ttle.knockdowns.common.network.packets.position.RequestRemotePlayerC2SPacket;
import ru.octol1ttle.knockdowns.common.util.RendererUtilsCopy;
import ru.octol1ttle.knockdowns.common.util.ScreenSpaceTransformResult;

public class KnockdownsClientEvents {
    public static void onHudRender(DrawContext context) {
        renderReviveText(context);
        renderPlayerIcons(context);
    }

    private static void renderReviveText(DrawContext context) {
        IKnockableDown reviving = (IKnockableDown) KnockdownsClient.reviving;
        MinecraftClient client = MinecraftClient.getInstance();
        if (reviving == null) {
            reviving = (IKnockableDown) client.player;
            if (reviving == null || reviving.get_ReviveTimer() == KnockdownsCommon.REVIVE_WAIT_TIME) {
                return;
            }
        }

        TextRenderer renderer = client.textRenderer;

        String timerText = String.format("%.1f", reviving.get_ReviveTimer() / (float) SharedConstants.TICKS_PER_SECOND);
        int timerX = (context.getScaledWindowWidth() - renderer.getWidth(timerText)) / 2;

        int reviverCount = reviving.get_ReviverCount();
        Integer color;
        if (reviverCount == 0) {
            color = Formatting.RED.getColorValue();
        } else if (reviverCount == 1) {
            color = Formatting.WHITE.getColorValue();
        } else {
            color = Formatting.GREEN.getColorValue();
        }

        String reviverCountText = "x" + reviverCount;
        int reviveCountX = (context.getScaledWindowWidth() - renderer.getWidth(reviverCountText)) / 2;

        if (color != null) {
            context.drawTextWithShadow(renderer, timerText, timerX, context.getScaledWindowHeight() / 2 + 5, color);
            context.drawTextWithShadow(renderer, reviverCountText, reviveCountX, context.getScaledWindowHeight() / 2 + 14, color);
        }
    }

    public static final Map<UUID, Optional<RemotePlayer>> remotePlayers = new HashMap<>();
    private static final Identifier KNOCKED_ICON_ID = new Identifier("knockdowns", "textures/knocked_icon.png");
    @SuppressWarnings("DataFlowIssue")
    private static void renderPlayerIcons(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        Collection<PlayerListEntry> entries = client.player.networkHandler.getListedPlayerListEntries();
        for (PlayerListEntry entry : entries) {
            UUID id = entry.getProfile().getId();
            PlayerEntity player = client.world.getPlayerByUuid(id);
            if (client.player.equals(player)) {
                continue;
            }

            Vec3d eyePosition;
            boolean knockedDown;
            if (player != null) {
                remotePlayers.remove(id);

                eyePosition = player.getEyePos();
                knockedDown = ((IKnockableDown) player).is_KnockedDown();
            } else {
                Optional<RemotePlayer> remote = remotePlayers.get(id);
                if (remote != null) {
                    if (remote.isEmpty()) {
                        continue;
                    }

                    eyePosition = remote.get().eyePosition();
                    knockedDown = remote.get().knockedDown();
                } else {
                    remotePlayers.put(id, Optional.empty());
                    KnockdownsNetwork.sendToServer(new RequestRemotePlayerC2SPacket(id));
                    continue;
                }
            }

            ScreenSpaceTransformResult result = RendererUtilsCopy.worldSpaceToScreenSpace(eyePosition);
            int size = 16;

            int width = context.getScaledWindowWidth();
            int x = MathHelper.clamp(MathHelper.floor(result.vec().x - size * 0.5), size + 5, width - size - 5);

            int height = context.getScaledWindowHeight();
            int y = MathHelper.clamp(MathHelper.floor(result.vec().y - size * 0.5), size + 5, height - size - 5);

            if (result.type() != ScreenSpaceTransformResult.TransformType.ON_SCREEN
                    || client.player.getEyePos().distanceTo(eyePosition) > 64.0
                    || client.world
                        .raycast(new RaycastContext(
                                client.getEntityRenderDispatcher().camera.getPos(),
                                eyePosition,
                                RaycastContext.ShapeType.VISUAL,
                                RaycastContext.FluidHandling.SOURCE_ONLY,
                                client.player
                        )).getType() == HitResult.Type.BLOCK)
            {
                PlayerSkinDrawer.draw(context, entry.getSkinTexture(), x, y, size, true, false);
            }

            if (knockedDown) {
                context.drawTexture(KNOCKED_ICON_ID, x, y, size, size, 0, 0, 18, 18, 18, 18);
            }
            if (client.player.age % 20 == 0 && remotePlayers.containsKey(id)) {
                KnockdownsNetwork.sendToServer(new RequestRemotePlayerC2SPacket(id));
            }
        }
    }
}
