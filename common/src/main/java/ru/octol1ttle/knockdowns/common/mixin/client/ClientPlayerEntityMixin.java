package ru.octol1ttle.knockdowns.common.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin implements IKnockableDown {
    @ModifyReturnValue(method = "shouldSlowDown", at = @At("RETURN"))
    private boolean shouldSlowDown(boolean original) {
        return original || this.is_KnockedDown();
    }
}