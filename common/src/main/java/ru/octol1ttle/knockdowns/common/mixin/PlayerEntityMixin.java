package ru.octol1ttle.knockdowns.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.octol1ttle.knockdowns.common.KnockdownsCommon;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;

@SuppressWarnings("WrongEntityDataParameterClass")
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity implements IKnockableDown {
    @Unique
    private static final TrackedData<Boolean> KNOCKED_DOWN = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private static final TrackedData<Boolean> IS_REVIVING = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private static final TrackedData<Integer> REVIVER_COUNT = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Unique
    private static final TrackedData<Integer> REVIVE_TIMER = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
        throw new AssertionError();
    }

    @ModifyExpressionValue(method = "updatePose", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSwimming()Z"))
    private boolean enterSwimmingIfKnockedDown(boolean original) {
        return original || this.is_KnockedDown();
    }

    @ModifyReturnValue(method = "canFoodHeal", at = @At("RETURN"))
    private boolean dontHealIfKnockedDown(boolean original) {
        return original && !this.is_KnockedDown();
    }

    @Inject(method = "checkFallFlying", at = @At("HEAD"), cancellable = true)
    private void dontOpenElytraIfKnockedDown(CallbackInfoReturnable<Boolean> cir) {
        if (this.is_KnockedDown()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initCustomDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(KNOCKED_DOWN, false);
        this.dataTracker.startTracking(IS_REVIVING, false);
        this.dataTracker.startTracking(REVIVER_COUNT, 0);
        this.dataTracker.startTracking(REVIVE_TIMER, KnockdownsCommon.REVIVE_WAIT_TIME);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readKnockedDownFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.set_KnockedDown(nbt.getBoolean("KnockedDown"));
        this.set_ReviveTimer(nbt.getInt("ReviveTimer"));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeKnockedDownToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("KnockedDown", this.is_KnockedDown());
        nbt.putInt("ReviveTimer", this.get_ReviveTimer());
    }

    @Override
    public boolean is_KnockedDown() {
        return this.dataTracker.get(KNOCKED_DOWN);
    }

    @Override
    public void set_KnockedDown(boolean knockedDown) {
        this.dataTracker.set(KNOCKED_DOWN, knockedDown);
    }

    @Override
    public boolean is_Reviving() {
        return this.dataTracker.get(IS_REVIVING);
    }

    @Override
    public void set_Reviving(boolean reviving) {
        this.dataTracker.set(IS_REVIVING, reviving);
    }

    @Override
    public int get_ReviverCount() {
        return this.dataTracker.get(REVIVER_COUNT);
    }

    @Override
    public void set_ReviverCount(int reviverCount) {
        this.dataTracker.set(REVIVER_COUNT, reviverCount);
    }

    @Override
    public int get_ReviveTimer() {
        return this.dataTracker.get(REVIVE_TIMER);
    }

    @Override
    public void set_ReviveTimer(int reviveTimer) {
        this.dataTracker.set(REVIVE_TIMER, reviveTimer);
    }
}
