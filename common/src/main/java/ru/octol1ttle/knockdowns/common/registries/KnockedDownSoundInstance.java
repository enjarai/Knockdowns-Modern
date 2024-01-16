package ru.octol1ttle.knockdowns.common.registries;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class KnockedDownSoundInstance extends MovingSoundInstance {
    private final Vec3d pos;

    public KnockedDownSoundInstance(SoundEvent sound, Vec3d pos) {
        super(sound, SoundCategory.MASTER, Random.create(0L));
        this.pos = pos;
        this.relative = true;
    }

    @Override
    public void tick() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            throw new IllegalStateException();
        }
        Vec3d vec = pos.subtract(player.getPos()).normalize();
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }
}
