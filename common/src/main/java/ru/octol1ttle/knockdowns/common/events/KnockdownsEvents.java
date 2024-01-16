package ru.octol1ttle.knockdowns.common.events;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Hand;
import net.minecraft.world.GameRules;
import ru.octol1ttle.knockdowns.common.KnockdownsNetwork;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;
import ru.octol1ttle.knockdowns.common.packets.KnockedDownStatusPacket;
import ru.octol1ttle.knockdowns.common.packets.PlayKnockedDownSoundS2CPacket;

public class KnockdownsEvents {
    public static void registerCallbacks() {
        registerOnLivingDeath();
        registerOnPlayerInteractions();
    }

    private static void registerOnLivingDeath() {
        EntityEvent.LIVING_DEATH.register((entity, source) -> {
            if (!(entity instanceof IKnockableDown knockableDown) || knockableDown.knockdowns$isKnockedDown()) {
                return EventResult.pass();
            }

            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) entity;
            // TODO: timer
            if (!serverPlayer.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
                serverPlayer.getInventory().dropAll();
            }
            entity.setHealth(1.0f);
            entity.setInvulnerable(true);
            entity.setGlowing(true);
            entity.setAir(entity.getMaxAir());
            entity.extinguish();
            entity.setFrozenTicks(0);
            entity.setOnFire(false);
            entity.clearStatusEffects();

            knockableDown.knockdowns$setKnockedDown(true);

            KnockdownsNetwork.sendToListenersAndSelf(serverPlayer, new KnockedDownStatusPacket.SendS2C(serverPlayer.getUuid(), true));
            KnockdownsNetwork.sendToWorld(serverPlayer.getServerWorld(), new PlayKnockedDownSoundS2CPacket(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ()));

            TranslatableTextContent content = (TranslatableTextContent) entity.getDamageTracker().getDeathMessage().getContent();
            Text replaced = Text.translatableWithFallback(content.getKey().replace("death.", "knockdown."), content.getKey(), content.getArgs());
            MinecraftServer server = serverPlayer.getServer();
            if (server != null) {
                server.getPlayerManager().broadcast(replaced, false);
            }

            return EventResult.interruptFalse();
        });
    }

    private static void registerOnPlayerInteractions() {
        InteractionEvent.LEFT_CLICK_BLOCK.register((player, hand, pos, direction) -> {
            if (player instanceof IKnockableDown && ((IKnockableDown) player).knockdowns$isKnockedDown()) {
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        PlayerEvent.ATTACK_ENTITY.register((player, world, hand, entity, hitResult) -> {
            if (player instanceof IKnockableDown && ((IKnockableDown) player).knockdowns$isKnockedDown()) {
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        InteractionEvent.RIGHT_CLICK_ITEM.register((player, hand) -> {
            if (player instanceof IKnockableDown && ((IKnockableDown) player).knockdowns$isKnockedDown()) {
                return CompoundEventResult.interruptFalse(hand == Hand.MAIN_HAND ? player.getMainHandStack() : player.getOffHandStack());
            }
            return CompoundEventResult.pass();
        });
        InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, direction) -> {
            if (player instanceof IKnockableDown && ((IKnockableDown) player).knockdowns$isKnockedDown()) {
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
    }
}
