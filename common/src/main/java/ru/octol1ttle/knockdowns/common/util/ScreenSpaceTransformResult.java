package ru.octol1ttle.knockdowns.common.util;

import org.joml.Vector2d;

public record ScreenSpaceTransformResult(Vector2d vec, TransformType type) {
    public enum TransformType {
        ON_SCREEN,
        STUCK_TO_EDGES
    }
}
