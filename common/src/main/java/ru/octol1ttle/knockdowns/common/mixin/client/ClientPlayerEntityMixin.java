package ru.octol1ttle.knockdowns.common.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin implements IKnockableDown {
//    @ModifyReturnValue(method = "shouldSlowDown", at = @At("RETURN"))
//    private boolean shouldSlowDown(boolean original) {
//        return original || this.is_KnockedDown();
//    }

    @ModifyExpressionValue(
            method = "tickMovement",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/input/Input;jumping:Z",
                    ordinal = 0
            )
    )
    private boolean cancelJump(boolean original) {
        return original && !this.is_KnockedDown();
    }

    @WrapOperation(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/input/Input;tick(ZF)V"
            )
    )
    private void shouldTickMovement(Input instance, boolean slowDown, float slowDownFactor, Operation<Void> original) {
        if (this.is_KnockedDown()) {
//            instance.pressingForward = false;
//            instance.pressingBack = false;
//            instance.pressingLeft = false;
//            instance.pressingRight = false;
//            instance.movementForward = 0;
//            instance.movementSideways = 0;
//            instance.sneaking = false;
            original.call(instance, true, 0.2f);
            instance.jumping = false;
        } else {
            original.call(instance, slowDown, slowDownFactor);
        }
    }
}