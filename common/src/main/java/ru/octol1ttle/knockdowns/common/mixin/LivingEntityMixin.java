package ru.octol1ttle.knockdowns.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @ModifyReturnValue(method = "canTarget(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("RETURN"))
    private boolean dontTargetKnockedPlayers(boolean original, LivingEntity target) {
        return original && !(target instanceof IKnockableDown knockable && knockable.is_KnockedDown());
    }
}
