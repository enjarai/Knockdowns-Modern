package ru.octol1ttle.knockdowns.fabric;

import net.fabricmc.api.ClientModInitializer;
import ru.octol1ttle.knockdowns.common.KnockdownsClient;

public class KnockdownsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KnockdownsClient.init();
    }
}
