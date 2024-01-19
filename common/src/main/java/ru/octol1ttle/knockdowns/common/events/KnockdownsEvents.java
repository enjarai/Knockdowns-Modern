package ru.octol1ttle.knockdowns.common.events;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import java.util.Objects;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Hand;
import ru.octol1ttle.knockdowns.common.KnockdownsClient;
import ru.octol1ttle.knockdowns.common.KnockdownsCommon;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;
import ru.octol1ttle.knockdowns.common.network.KnockdownsNetwork;
import ru.octol1ttle.knockdowns.common.network.packets.PlayKnockedDownSoundS2CPacket;

public class KnockdownsEvents {
    private static final float KNOCKED_DOWN_TIMER = 50.0f;

    public static void registerCallbacks() {
        registerOnLivingDeath();
        registerOnPlayerTick();
        registerOnPlayerInteractions();
        registerOnEntityUse();
    }

    private static void registerOnLivingDeath() {
        EntityEvent.LIVING_DEATH.register((entity, source) -> {
            if (entity.getWorld().isClient() || !(entity instanceof IKnockableDown knockable)) {
                return EventResult.pass();
            }

            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) entity;
            MinecraftServer server = serverPlayer.getServer();
            if (server == null || server.getCurrentPlayerCount() == 1) {
                return EventResult.pass();
            }

            if (knockable.is_KnockedDown()) {
                knockable.set_KnockedDown(false);
                knockable.set_ReviverCount(0);
                knockable.set_ReviveTimer(KnockdownsCommon.REVIVE_WAIT_TIME);

                return EventResult.pass();
            }

            entity.clearStatusEffects();
            entity.setInvulnerable(true);
            entity.setGlowing(true);
            entity.setHealth(entity.getMaxHealth());
            entity.extinguish();
            entity.setAir(entity.getMaxAir());
            entity.setFrozenTicks(0);
            serverPlayer.stopFallFlying();

            knockable.set_KnockedDown(true);
            knockable.set_ReviveTimer(KnockdownsCommon.REVIVE_WAIT_TIME);

            KnockdownsNetwork.sendToWorld(serverPlayer.getServerWorld(), new PlayKnockedDownSoundS2CPacket(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ()));

            TranslatableTextContent content = (TranslatableTextContent) entity.getDamageTracker().getDeathMessage().getContent();
            Text replaced = Text.translatableWithFallback(content.getKey().replace("death.", "knockdown."), content.getKey(), content.getArgs());

            server.getPlayerManager().broadcast(replaced, false);

            return EventResult.interruptFalse();
        });
    }

    private static void registerOnPlayerTick() {
        TickEvent.PLAYER_POST.register(player -> {
            if (player.getWorld().isClient()) {
                KnockdownsClient.onPlayerTick(player);
                return;
            }
            if (!(player instanceof IKnockableDown knockable) || !knockable.is_KnockedDown()) {
                return;
            }
            if (knockable.get_ReviverCount() > 0) {
                knockable.set_ReviveTimer(knockable.get_ReviveTimer() - knockable.get_ReviverCount());

                if (knockable.get_ReviveTimer() <= 0) {
                    knockable.set_KnockedDown(false);
                    knockable.set_ReviverCount(0);
                    knockable.set_ReviveTimer(KnockdownsCommon.REVIVE_WAIT_TIME);

                    player.setInvulnerable(false);
                    player.setGlowing(false);
                    player.setHealth(6.0f);
                }
                return;
            }
            knockable.set_ReviveTimer(Math.min(KnockdownsCommon.REVIVE_WAIT_TIME, knockable.get_ReviveTimer() + 2));

            if (player.age % 20 == 0) {
                player.setInvulnerable(false);
                DamageSource recent = player.getRecentDamageSource();
                player.damage(Objects.requireNonNullElse(recent, player.getDamageSources().generic()), player.getMaxHealth() / KNOCKED_DOWN_TIMER);
                player.velocityModified = false;
            }
        });
    }

    private static void registerOnPlayerInteractions() {
        InteractionEvent.LEFT_CLICK_BLOCK.register((player, hand, pos, direction) -> {
            if (KnockdownsCommon.isKnockedOrReviving(player)) {
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        PlayerEvent.ATTACK_ENTITY.register((player, world, hand, entity, hitResult) -> {
            if (KnockdownsCommon.isKnockedOrReviving(player)) {
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        InteractionEvent.RIGHT_CLICK_ITEM.register((player, hand) -> {
            if (KnockdownsCommon.isKnockedOrReviving(player)) {
                return CompoundEventResult.interruptFalse(hand == Hand.MAIN_HAND ? player.getMainHandStack() : player.getOffHandStack());
            }
            return CompoundEventResult.pass();
        });
        InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, direction) -> {
            if (KnockdownsCommon.isKnockedOrReviving(player)) {
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
    }

    private static void registerOnEntityUse() {
        InteractionEvent.INTERACT_ENTITY.register((player, entity, hand)
                -> player.getWorld().isClient() ? KnockdownsClient.onEntityUse(player, entity) : EventResult.pass());
    }
}
