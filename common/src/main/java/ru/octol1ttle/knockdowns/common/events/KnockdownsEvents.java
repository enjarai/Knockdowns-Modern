package ru.octol1ttle.knockdowns.common.events;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.SharedConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.knockdowns.common.KnockdownsClient;
import ru.octol1ttle.knockdowns.common.KnockdownsCommon;
import ru.octol1ttle.knockdowns.common.KnockdownsUtils;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;
import ru.octol1ttle.knockdowns.common.network.KnockdownsNetwork;
import ru.octol1ttle.knockdowns.common.network.packets.PlayKnockedDownSoundS2CPacket;

public class KnockdownsEvents {
    private static final float KNOCKED_INVULNERABILITY_TICKS = 3.0f * SharedConstants.TICKS_PER_SECOND;
    private static final float KNOCKED_HURT_PERIOD = 1.2f;
    private static final float KNOCKED_TENACITY = 60.0f;

    public static void registerCallbacks() {
        registerOnLivingDeath();
        registerOnPlayerTick();
        registerOnPlayerInteractions();
        registerOnEntityUse();
    }

    private static void registerOnLivingDeath() {
        EntityEvent.LIVING_DEATH.register((entity, source) -> {
            MinecraftServer server = entity.getServer();
            if (server == null || !(entity instanceof IKnockableDown knockable)) {
                return EventResult.pass();
            }

            ServerPlayerEntity player = (ServerPlayerEntity) entity;

            if (knockable.is_KnockedDown() || KnockdownsUtils.allTeammatesKnocked(server, player)) {
                KnockdownsUtils.resetKnockedState(knockable);

                return EventResult.pass();
            }

            entity.clearStatusEffects();
            entity.setInvulnerable(true);
            entity.setGlowing(true);
            entity.setHealth(entity.getMaxHealth());
            entity.extinguish();
            entity.setAir(entity.getMaxAir());
            entity.setFrozenTicks(0);
            player.stopFallFlying();

            knockable.set_KnockedDown(true);
            knockable.set_ReviveTimer(KnockdownsCommon.REVIVE_WAIT_TIME);
            knockable.set_KnockedAge(0);

            KnockdownsNetwork.sendToWorld(player.getServerWorld(), new PlayKnockedDownSoundS2CPacket(player.getX(), player.getY(), player.getZ()));

            Text deathMessage = entity.getDamageTracker().getDeathMessage();
            TranslatableTextContent deathContent = (TranslatableTextContent) deathMessage.getContent();

            String knockdownKey = deathContent.getKey().replace("death.", "knockdown.");
            Text knockdownMessage = Text.translatable(knockdownKey, deathContent.getArgs());

            server.getPlayerManager().broadcast(!knockdownMessage.getString().equals(knockdownKey) ? knockdownMessage : deathMessage, false);

            return EventResult.interruptFalse();
        });
    }

    private static void registerOnPlayerTick() {
        TickEvent.PLAYER_POST.register(player -> {
            MinecraftServer server = player.getServer();
            if (server == null) {
                KnockdownsClient.onPlayerTick(player);
                return;
            }
            if (!(player instanceof IKnockableDown knockable) || !knockable.is_KnockedDown()) {
                return;
            }

            if (KnockdownsUtils.allTeammatesKnocked(server, player)) {
                KnockdownsUtils.hurtTenacity(player, player.getMaxHealth());
                return;
            }

            if (knockable.get_ReviverCount() > 0) {
                knockable.set_ReviveTimer(knockable.get_ReviveTimer() - knockable.get_ReviverCount());

                if (knockable.get_ReviveTimer() <= 0) {
                    KnockdownsUtils.resetKnockedState(knockable);

                    player.setInvulnerable(false);
                    player.setGlowing(false);
                    player.setHealth(player.getMaxHealth() * 0.3f);
                }
                return;
            }
            knockable.set_ReviveTimer(Math.min(KnockdownsCommon.REVIVE_WAIT_TIME, knockable.get_ReviveTimer() + 1));

            knockable.set_KnockedAge(knockable.get_KnockedAge() + 1);

            int period = MathHelper.floor(KNOCKED_HURT_PERIOD * SharedConstants.TICKS_PER_SECOND);
            if (knockable.get_KnockedAge() >= KNOCKED_INVULNERABILITY_TICKS && knockable.get_KnockedAge() % period == 0) {
                KnockdownsUtils.hurtTenacity(player, player.getMaxHealth() / (KNOCKED_TENACITY / KNOCKED_HURT_PERIOD));
            }
        });
    }

    private static void registerOnPlayerInteractions() {
        InteractionEvent.LEFT_CLICK_BLOCK.register((player, hand, pos, direction) -> {
            if (KnockdownsUtils.isKnockedOrReviving(player)) {
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        PlayerEvent.ATTACK_ENTITY.register((player, world, hand, entity, hitResult) -> {
            if (KnockdownsUtils.isKnockedOrReviving(player)) {
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        BlockEvent.PLACE.register((level, pos, state, placer) -> {
            if (placer instanceof PlayerEntity player && KnockdownsUtils.isKnockedOrReviving(player)) {
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        InteractionEvent.RIGHT_CLICK_ITEM.register((player, hand) -> {
            if (KnockdownsUtils.isKnockedOrReviving(player)) {
                return CompoundEventResult.interruptFalse(hand == Hand.MAIN_HAND ? player.getMainHandStack() : player.getOffHandStack());
            }
            return CompoundEventResult.pass();
        });
    }

    private static void registerOnEntityUse() {
        InteractionEvent.INTERACT_ENTITY.register((player, entity, hand)
                -> player.getWorld().isClient() ? KnockdownsClient.onEntityUse(player, entity) : EventResult.pass());
    }
}
