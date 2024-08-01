package ru.octol1ttle.knockdowns.common.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.octol1ttle.knockdowns.common.util.RendererUtilsCopy;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
//    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD, ordinal = 0), method = "renderWorld")
//    void renderer_postWorldRender(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
//        RendererUtilsCopy.lastProjMat.set(RenderSystem.getProjectionMatrix());
//        RendererUtilsCopy.lastModMat.set(RenderSystem.getModelViewMatrix());
//        RendererUtilsCopy.lastWorldSpaceMatrix.set(matrix.peek().getPositionMatrix());
//    }
}
