package ru.octol1ttle.knockdowns.common.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.octol1ttle.knockdowns.common.api.IKnockableDown;
import ru.octol1ttle.knockdowns.common.events.KnockdownsClientEvents;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableDepthTest()V", ordinal = 0))
    public void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        KnockdownsClientEvents.onHudRender(context);
    }

    @Inject(
            method = "renderVignetteOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIFFIIII)V"
            )
    )
    private void changeColorWhenFrozen(DrawContext context, Entity entity, CallbackInfo ci) {
        if (entity instanceof IKnockableDown knockable && knockable.is_KnockedDown()) {
            context.setShaderColor(0.0f, 0.8f, 0.8f, 1f);
        }
    }
}
