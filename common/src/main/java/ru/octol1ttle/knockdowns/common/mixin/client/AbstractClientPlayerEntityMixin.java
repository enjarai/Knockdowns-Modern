package ru.octol1ttle.knockdowns.common.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin {
    @Inject(
            method = "getFovMultiplier",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/player/PlayerAbilities;flying:Z"
            )
    )
    private void fixFovWhenFrozen(CallbackInfoReturnable<Float> cir, @Local(ordinal = 0) LocalFloatRef floatRef) {
        if (this instanceof IKnockableDown knockable && knockable.is_KnockedDown()) {
            floatRef.set(floatRef.get() * 0.6f);
        }
    }
}
