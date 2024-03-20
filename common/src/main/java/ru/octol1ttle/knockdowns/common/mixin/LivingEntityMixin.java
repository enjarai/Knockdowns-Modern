package ru.octol1ttle.knockdowns.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.octol1ttle.knockdowns.common.KnockdownsUtils;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @ModifyReturnValue(method = "canTarget(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("RETURN"))
    private boolean dontTargetKnockedPlayers(boolean original, LivingEntity target) {
        return original && !(target instanceof IKnockableDown knockable && knockable.is_KnockedDown());
    }

    @Inject(method = "tryUseTotem", at = @At("RETURN"))
    private void resetKnockedDownOnTotemUse(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity $this = (LivingEntity) (Object) this;
        if (cir.getReturnValue() && $this instanceof IKnockableDown knockable && knockable.is_KnockedDown()) {
            KnockdownsUtils.resetKnockedState(knockable);
        }
    }
}
