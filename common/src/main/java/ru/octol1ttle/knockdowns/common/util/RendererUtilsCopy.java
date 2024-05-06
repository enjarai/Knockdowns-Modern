package ru.octol1ttle.knockdowns.common.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

/**
 * @author 0x3C50
 * <p>Utils for rendering in minecraft</p>
 */
public class RendererUtilsCopy {
    @ApiStatus.Internal
    public static final Matrix4f lastProjMat = new Matrix4f();
    @ApiStatus.Internal
    public static final Matrix4f lastModMat = new Matrix4f();
    @ApiStatus.Internal
    public static final Matrix4f lastWorldSpaceMatrix = new Matrix4f();
    private static final MinecraftClient client = MinecraftClient.getInstance();

    @Contract(value = "_ -> new", pure = true)
    public static ScreenSpaceTransformResult worldSpaceToScreenSpace(@NotNull Vec3d pos) {
        Camera camera = client.getEntityRenderDispatcher().camera;

        double deltaX = pos.x - camera.getPos().x;
        double deltaY = pos.y - camera.getPos().y;
        double deltaZ = pos.z - camera.getPos().z;

        Vector4f transformedCoordinates = new Vector4f((float) deltaX, (float) deltaY, (float) deltaZ, 1.f).mul(
                lastWorldSpaceMatrix);

        Matrix4f matrixProj = new Matrix4f(lastProjMat);

        Vector4f clip = matrixProj.transform(new Vector4f(transformedCoordinates));
        if (clip.z > 0 && Math.abs(clip.x) < Math.abs(clip.w) && Math.abs(clip.y) < Math.abs(clip.w)) {
            int[] viewport = new int[4];
            GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
            Matrix4f matrixModel = new Matrix4f(lastModMat);
            Vector3f target = new Vector3f();

            matrixProj
                    .mul(matrixModel)
                    .project(transformedCoordinates.x(), transformedCoordinates.y(), transformedCoordinates.z(), viewport, target);

            return new ScreenSpaceTransformResult(
                    new Vector2d(
                            target.x / client.getWindow().getScaleFactor(),
                            (client.getWindow().getHeight() - target.y) / client.getWindow().getScaleFactor()
                    ),
                    ScreenSpaceTransformResult.ScreenSpaceTransformType.ON_SCREEN
            );
        }

        clip.normalize();
        double angle = Math.atan2(clip.y, clip.x);
        double width = client.getWindow().getScaledWidth();
        double height = client.getWindow().getScaledHeight();

        double x = MathHelper.clamp(Math.cos(angle) * width + width / 2, 0.0f, width);
        double y = height - (MathHelper.clamp(Math.sin(angle) * height + height / 2, 0.0f, height));

        return new ScreenSpaceTransformResult(
                new Vector2d(x, y),
                ScreenSpaceTransformResult.ScreenSpaceTransformType.STUCK_TO_EDGES
        );
    }
}