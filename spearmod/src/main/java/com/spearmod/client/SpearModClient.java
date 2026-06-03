package com.spearmod.client;

import net.fabricmc.api.ClientModInitializer;
import com.spearmod.SpearMod;

public class SpearModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SpearMod.LOGGER.info("SpearMod client initialized.");
        // Future: custom charge animations, HUD state indicator, etc.
    }
}
