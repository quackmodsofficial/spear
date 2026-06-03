package com.spearmod.registry;

import com.spearmod.SpearMod;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * Holds the RegistryKey for the Lunge enchantment so SpearItem can look it up.
 * The actual enchantment definition lives in data/spearmod/enchantment/lunge.json.
 * No manual Registry.register() needed — 1.21.1 loads enchantments from datapacks.
 */
public class ModEnchantments {

    public static final RegistryKey<Enchantment> LUNGE_KEY =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(SpearMod.MOD_ID, "lunge"));

    public static void register() {
        // No-op: enchantments are data-driven in 1.21.1.
        // The key above is enough for EnchantmentHelper lookups at runtime.
    }
}
