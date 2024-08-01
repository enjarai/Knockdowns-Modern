package ru.octol1ttle.knockdowns.common.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void setTarget(LivingEntity target, CallbackInfo ci) {
        if (target instanceof IKnockableDown knockable && knockable.is_KnockedDown()) {
            ci.cancel();
        }
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        return super.canTarget(target) && !(target instanceof IKnockableDown knockable && knockable.is_KnockedDown());
    }
}
