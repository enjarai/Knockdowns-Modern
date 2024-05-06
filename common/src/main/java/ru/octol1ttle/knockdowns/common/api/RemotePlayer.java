package ru.octol1ttle.knockdowns.common.api;

import net.minecraft.util.math.Vec3d;

public record RemotePlayer(Vec3d eyePosition, boolean knockedDown) { }
