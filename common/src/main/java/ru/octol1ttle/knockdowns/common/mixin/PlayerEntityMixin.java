package ru.octol1ttle.knockdowns.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements IKnockableDown {
    @Unique
    private boolean knockdowns$knockedDown;
    @Unique
    private boolean knockdowns$beingRevived;

    @ModifyExpressionValue(method = "updatePose", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSwimming()Z"))
    private boolean enterSwimmingIfKnockedDown(boolean original) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        if (!(player instanceof IKnockableDown knockableDown)) {
            throw new IllegalStateException();
        }

        return original || knockableDown.knockdowns$isKnockedDown();
    }

    @ModifyReturnValue(method = "canFoodHeal", at = @At("RETURN"))
    private boolean dontHealIfKnockedDown(boolean original) {
        return original && !this.knockdowns$isKnockedDown();
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readKnockedDownFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.knockdowns$knockedDown = nbt.getBoolean("KnockedDown");
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeKnockedDownToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("KnockedDown", this.knockdowns$knockedDown);
    }

    @Override
    public boolean knockdowns$isKnockedDown() {
        return knockdowns$knockedDown;
    }

    @Override
    public void knockdowns$setKnockedDown(boolean knockedDown) {
        this.knockdowns$knockedDown = knockedDown;
    }

    @Override
    public boolean knockdowns$isBeingRevived() {
        return knockdowns$beingRevived;
    }

    @Override
    public void knockdowns$setBeingRevived(boolean beingRevived) {
        this.knockdowns$beingRevived = beingRevived;
    }

    @Override
    public UUID knockdowns$getUuid() {
        return ((PlayerEntity)(Object)this).getUuid();
    }
}
