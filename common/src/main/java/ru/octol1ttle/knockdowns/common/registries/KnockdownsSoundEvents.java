package ru.octol1ttle.knockdowns.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import ru.octol1ttle.knockdowns.common.KnockdownsCommon;

public class KnockdownsSoundEvents {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(KnockdownsCommon.MOD_ID, RegistryKeys.SOUND_EVENT);
    public static final RegistrySupplier<SoundEvent> KNOCKED_DOWN = SOUND_EVENTS.register(KnockdownsCommon.MOD_ID,
            () -> SoundEvent.of(new Identifier(KnockdownsCommon.MOD_ID, "knocked_down")));

    public static void register() {
        SOUND_EVENTS.register();
    }
}
