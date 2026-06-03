package com.spearmod;

import com.spearmod.registry.ModItems;
import com.spearmod.registry.ModEnchantments;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpearMod implements ModInitializer {
    public static final String MOD_ID = "spearmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.register();
        ModEnchantments.register();
        LOGGER.info("SpearMod loaded! Spears are ready for combat.");
    }
}
