package com.spearmod.enchantment;

/**
 * LungeEnchantment — placeholder class kept for package completeness.
 *
 * In Minecraft 1.21.1 (data-driven enchantments), there is NO Java class
 * to extend for custom enchantments. The enchantment is defined entirely
 * in data/spearmod/enchantment/lunge.json.
 *
 * The actual Lunge propulsion logic lives in SpearItem#applyLunge(),
 * triggered on every jab hit when EnchantmentHelper detects the level.
 *
 * This class intentionally contains no code.
 */
public final class LungeEnchantment {
    private LungeEnchantment() {}
}
