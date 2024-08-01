package ru.octol1ttle.knockdowns.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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

    @Unique
    private static final StatusEffectInstance PERM_BLINDNESS = new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 2);

    @ModifyReturnValue(
            method = "hasStatusEffect",
            at = @At("RETURN")
    )
    public boolean hasStatusEffect(boolean original, RegistryEntry<StatusEffect> effect) {
        if (!(((LivingEntity)(Object)this) instanceof PlayerEntity))
            return original;

        if (effect == StatusEffects.BLINDNESS) {
            return original || (this instanceof IKnockableDown knockable && knockable.is_KnockedDown());
        }

        return original;
    }

    @Nullable
    @ModifyReturnValue(
            method = "getStatusEffect",
            at = @At("RETURN")
    )
    public StatusEffectInstance getStatusEffect(StatusEffectInstance original, RegistryEntry<StatusEffect> effect) {
        if (!(((LivingEntity)(Object)this) instanceof PlayerEntity))
            return original;

        if (effect == StatusEffects.BLINDNESS && original == null && (this instanceof IKnockableDown knockable && knockable.is_KnockedDown())) {
            return PERM_BLINDNESS;
        }

        return original;
    }
}
