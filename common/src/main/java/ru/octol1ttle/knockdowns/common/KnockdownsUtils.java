package ru.octol1ttle.knockdowns.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;

public class KnockdownsUtils {
    public static boolean isKnockedOrReviving(PlayerEntity player) {
        return player instanceof IKnockableDown knockable && (knockable.is_KnockedDown() || knockable.is_Reviving());
    }

    public static boolean allTeammatesKnocked(MinecraftServer server, PlayerEntity player) {
//        for (PlayerEntity teammate : server.getPlayerManager().getPlayerList()) {
//            if (teammate.equals(player)) {
//                continue;
//            }
//            IKnockableDown knockable = (IKnockableDown) teammate;
//            if (!knockable.is_KnockedDown() && !teammate.isDead()) {
//                return false;
//            }
//        }
//        return true;
        return false;
    }

    public static void hurtTenacity(PlayerEntity player, float damage) {
        player.setInvulnerable(false);
        //DamageSource recent = player.getRecentDamageSource();
//        player.damage(/*Objects.requireNonNullElse(recent, */player.getDamageSources().generic()/*)*/, damage);
        player.velocityModified = false;
    }

    public static void resetKnockedState(IKnockableDown knockable) {
        knockable.set_KnockedDown(false);
        knockable.set_ReviverCount(0);
        knockable.set_ReviveTimer(KnockdownsCommon.REVIVE_WAIT_TIME);
        knockable.set_KnockedAge(0);
    }
}
