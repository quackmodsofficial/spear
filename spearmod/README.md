# SpearMod — Fabric 1.21.1
### Backport of the 1.21.11 Spear weapon — all mechanics, all 7 tiers

---

## What This Mod Does

Adds the full 1.21.11 Spear weapon to Fabric 1.21.1 with **exact mechanics** from the official release:

- 7 material tiers: Wood, Stone, Copper, Iron, Gold, Diamond, Netherite
- **Jab attack** (left-click): extended 4.5-block reach, minimum 2-block range, pierce through multiple enemies in a line
- **Charge attack** (right-click hold): velocity-scaled damage with 3 states — Engaged, Tired, Disengaged
- **Lunge enchantment** (I–III): propels you forward on jab, costs hunger
- Items are **registered as real Minecraft items** — visible on 1.21.11+ servers via ViaBackwards

---

## How to Install

1. Install [Fabric Loader 0.16.9+](https://fabricmc.net/use/) for Minecraft **1.21.1**
2. Install [Fabric API 0.102.0+1.21.1](https://modrinth.com/mod/fabric-api)
3. Drop `spearmod-1.0.0.jar` into your `.minecraft/mods/` folder
4. Launch 1.21.1 — done!

### Pojav Launcher
Same steps — just put the `.jar` in your mods folder for the 1.21.1 Fabric profile.

---

## Building From Source

Requirements: Java 21, internet connection for Gradle

```bash
cd spearmod/
./gradlew build
# Output: build/libs/spearmod-1.0.0.jar
```

---

## Spear Stats (exact 1.21.11 values)

| Tier      | Jab DMG | Charge Mult | Cooldown | Durability |
|-----------|---------|-------------|----------|------------|
| Wood      | 1       | ×0.70       | 0.75s    | 59         |
| Stone     | 2       | ×0.82       | 0.75s    | 131        |
| Copper    | 2       | ×0.82       | 0.65s    | 190        |
| Iron      | 3       | ×0.95       | 0.60s    | 250        |
| Gold      | 1       | ×0.70       | 0.70s    | 32         |
| Diamond   | 4       | ×1.075      | 0.50s    | 1561       |
| Netherite | 5       | ×1.20       | 0.40s    | 2031       |

Higher tier = more damage, slower jab cooldown, faster charge startup, shorter overall charge duration (requires more skill).

---

## Attack Mechanics

### Jab (Left-Click)
- Reach: **2 to 4.5 blocks** (can't hit targets within 2 blocks)
- Pierces all enemies in a straight line (half damage on secondary hits)
- No critical hits — but no forced knockback delay either
- Cooldown: must reach 100% before attacking again (displayed on hotbar)
- With **Lunge**: propels you horizontally in your look direction

### Charge (Right-Click Hold)
Hold right-click to enter the charge stance. Three stages:

| Stage       | Effect                              | Speed Threshold |
|-------------|-------------------------------------|-----------------|
| **Engaged** | Damage + Knockback + **Dismount**   | ≥5 blocks/sec   |
| **Tired**   | Damage + Knockback (no dismount)    | ≥3 blocks/sec   |
| **Disengaged** | Damage only                      | ≥1.5 blocks/sec |

Damage scales with your **relative speed toward the target**. Sprint, mount, or attack enemies running toward you for maximum damage.

---

## Lunge Enchantment

| Level | Speed         | Hunger Cost | Required Hunger |
|-------|---------------|-------------|-----------------|
| I     | 0.458 b/tick  | 1 point     | 6+ hearts       |
| II    | 0.916 b/tick  | 2 points    | 6+ hearts       |
| III   | 1.374 b/tick  | 3 points    | 6+ hearts       |

- Found as **treasure loot** (not from enchanting table)
- Does not work in water or while gliding with Elytra
- In Peaceful mode: hunger never depletes, so Lunge works freely

**Pro tip**: Jab with Lunge, then immediately hold right-click to start a charge attack using the gained velocity — this is the core Lunge combo!

---

## Crafting Recipes

All spears use this diagonal pattern:
```
_ M
_ S
S _
```
Where M = material, S = stick.

- **Netherite Spear**: Smithing Table — Netherite Upgrade Template + Diamond Spear + Netherite Ingot

---

## ViaBackwards / Server Compatibility

The spears are registered under `spearmod:iron_spear` etc. as real item IDs.

When connecting to a 1.21.11+ server via **ViaBackwards**:
- The server sends native `minecraft:iron_spear` etc. to clients
- ViaBackwards translates those IDs to what the 1.21.1 client can understand
- Since this mod registers matching items, the client will correctly display the spear
- **For this to work fully, the server should also have a spear plugin/mod** so item IDs match

If the server is vanilla 1.21.11, ViaBackwards may show the spear as an unknown item or fallback texture — this is a protocol limitation, not a mod bug.

---

## Textures Note

The included textures are functional placeholders. To use the **official Minecraft 1.21.11 textures**:

1. Extract textures from a 1.21.11 client jar: `assets/minecraft/textures/item/wooden_spear.png` etc.
2. Copy them to `assets/spearmod/textures/item/` in the mod jar (or a resource pack)

---

## License

MIT — free to use, modify, redistribute.
