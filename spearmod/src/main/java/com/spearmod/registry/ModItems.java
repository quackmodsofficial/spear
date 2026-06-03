package com.spearmod.registry;

import com.spearmod.SpearMod;
import com.spearmod.item.SpearItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * All 7 spear tiers registered with accurate 1.21.11 stats:
 *
 * Tier      | Jab DMG | Charge Mult | Cooldown | Durability | Engaged Start | Tired Start | Disengaged | Total
 * --------- | ------- | ----------- | -------- | ---------- | ------------- | ----------- | ---------- | -----
 * Wood      | 1       | x0.70       | 0.75s    | 59         | 4t            | 60t         | 180t       | 300t
 * Stone     | 2       | x0.82       | 0.70s    | 131        | 4t            | 55t         | 165t       | 275t
 * Copper    | 2       | x0.82       | 0.65s    | 190        | 3t            | 50t         | 150t       | 250t
 * Iron      | 3       | x0.95       | 0.60s    | 250        | 3t            | 45t         | 135t       | 225t
 * Gold      | 1       | x0.70       | 0.70s    | 32         | 4t            | 55t         | 165t       | 275t
 * Diamond   | 4       | x1.075      | 0.50s    | 1561       | 2t            | 40t         | 120t       | 200t
 * Netherite | 5       | x1.20       | 0.40s    | 2031       | 2t            | 35t         | 105t       | 175t
 */
public class ModItems {

    // ──────────────────────────────────────────────
    // ITEM REGISTRY KEYS
    // ──────────────────────────────────────────────
    public static final RegistryKey<Item> WOODEN_SPEAR_KEY = key("wooden_spear");
    public static final RegistryKey<Item> STONE_SPEAR_KEY = key("stone_spear");
    public static final RegistryKey<Item> COPPER_SPEAR_KEY = key("copper_spear");
    public static final RegistryKey<Item> IRON_SPEAR_KEY = key("iron_spear");
    public static final RegistryKey<Item> GOLDEN_SPEAR_KEY = key("golden_spear");
    public static final RegistryKey<Item> DIAMOND_SPEAR_KEY = key("diamond_spear");
    public static final RegistryKey<Item> NETHERITE_SPEAR_KEY = key("netherite_spear");

    // ──────────────────────────────────────────────
    // ITEM INSTANCES
    // ──────────────────────────────────────────────
    public static final SpearItem WOODEN_SPEAR = new SpearItem(
            ToolMaterials.WOOD,
            new Item.Settings().registryKey(WOODEN_SPEAR_KEY).maxDamage(59),
            1f, 0.70f,
            4, 60, 180, 300
    );

    public static final SpearItem STONE_SPEAR = new SpearItem(
            ToolMaterials.STONE,
            new Item.Settings().registryKey(STONE_SPEAR_KEY).maxDamage(131),
            2f, 0.82f,
            4, 55, 165, 275
    );

    public static final SpearItem COPPER_SPEAR = new SpearItem(
            ToolMaterials.IRON, // closest vanilla tier (copper not in 1.21.1 tools)
            new Item.Settings().registryKey(COPPER_SPEAR_KEY).maxDamage(190),
            2f, 0.82f,
            3, 50, 150, 250
    );

    public static final SpearItem IRON_SPEAR = new SpearItem(
            ToolMaterials.IRON,
            new Item.Settings().registryKey(IRON_SPEAR_KEY).maxDamage(250),
            3f, 0.95f,
            3, 45, 135, 225
    );

    public static final SpearItem GOLDEN_SPEAR = new SpearItem(
            ToolMaterials.GOLD,
            new Item.Settings().registryKey(GOLDEN_SPEAR_KEY).maxDamage(32),
            1f, 0.70f,
            4, 55, 165, 275
    );

    public static final SpearItem DIAMOND_SPEAR = new SpearItem(
            ToolMaterials.DIAMOND,
            new Item.Settings().registryKey(DIAMOND_SPEAR_KEY).maxDamage(1561),
            4f, 1.075f,
            2, 40, 120, 200
    );

    public static final SpearItem NETHERITE_SPEAR = new SpearItem(
            ToolMaterials.NETHERITE,
            new Item.Settings().registryKey(NETHERITE_SPEAR_KEY).maxDamage(2031).fireproof(),
            5f, 1.20f,
            2, 35, 105, 175
    );

    // ──────────────────────────────────────────────
    // ITEM GROUP
    // ──────────────────────────────────────────────
    public static final RegistryKey<ItemGroup> SPEAR_GROUP_KEY =
            RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(SpearMod.MOD_ID, "spears"));

    // ──────────────────────────────────────────────
    // REGISTRATION
    // ──────────────────────────────────────────────
    public static void register() {
        Registry.register(Registries.ITEM, WOODEN_SPEAR_KEY, WOODEN_SPEAR);
        Registry.register(Registries.ITEM, STONE_SPEAR_KEY, STONE_SPEAR);
        Registry.register(Registries.ITEM, COPPER_SPEAR_KEY, COPPER_SPEAR);
        Registry.register(Registries.ITEM, IRON_SPEAR_KEY, IRON_SPEAR);
        Registry.register(Registries.ITEM, GOLDEN_SPEAR_KEY, GOLDEN_SPEAR);
        Registry.register(Registries.ITEM, DIAMOND_SPEAR_KEY, DIAMOND_SPEAR);
        Registry.register(Registries.ITEM, NETHERITE_SPEAR_KEY, NETHERITE_SPEAR);

        // Create item group (shows up in creative inventory under "Combat")
        Registry.register(Registries.ITEM_GROUP, SPEAR_GROUP_KEY,
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemGroup.spearmod.spears"))
                        .icon(() -> new ItemStack(IRON_SPEAR))
                        .entries((ctx, e) -> {
                            e.add(WOODEN_SPEAR);
                            e.add(STONE_SPEAR);
                            e.add(COPPER_SPEAR);
                            e.add(IRON_SPEAR);
                            e.add(GOLDEN_SPEAR);
                            e.add(DIAMOND_SPEAR);
                            e.add(NETHERITE_SPEAR);
                        })
                        .build()
        );

        // Also inject into vanilla Combat group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.addAfter(Items.NETHERITE_SWORD,
                    WOODEN_SPEAR, STONE_SPEAR, COPPER_SPEAR, IRON_SPEAR,
                    GOLDEN_SPEAR, DIAMOND_SPEAR, NETHERITE_SPEAR);
        });
    }

    private static RegistryKey<Item> key(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SpearMod.MOD_ID, name));
    }
}
