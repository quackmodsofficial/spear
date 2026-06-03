package com.spearmod.item;

import com.spearmod.registry.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

/**
 * SpearItem — backport of the Minecraft 1.21.11 Spear weapon.
 * Extends Item directly (not SwordItem) to avoid double-attribute issues.
 */
public class SpearItem extends Item {

    public static final float MIN_REACH = 2.0f;
    public static final float MAX_REACH = 4.5f;
    public static final float HITBOX_INFLATION = 0.125f;

    private static final double ENGAGED_DISMOUNT_SPEED = 5.0;
    private static final double ENGAGED_KNOCKBACK_SPEED = 3.0;
    private static final double TIRED_KNOCKBACK_SPEED = 3.0;
    private static final double DISENGAGED_DAMAGE_SPEED = 1.5;

    // Per-entity hit cooldown: tracks last tick a target was hit by charge
    // Using a simple approach: store last-hit-tick on the attacker's NBT would
    // require mixins; instead we use a small in-memory set cleared each state change.
    // Simplest safe approach: only hit once per usageTick call (break after first hit already does this)

    private final int engagedStartTick;
    private final int tiredStartTick;
    private final int disengagedStartTick;
    private final int totalChargeTicks;
    private final float chargeDamageMultiplier;
    private final float jabDamage;
    private final ToolMaterial toolMaterial;

    public SpearItem(ToolMaterial material, Settings settings,
                     float jabDamage, float chargeDamageMultiplier,
                     int engagedStartTick, int tiredStartTick,
                     int disengagedStartTick, int totalChargeTicks) {
        super(settings);
        this.toolMaterial = material;
        this.jabDamage = jabDamage;
        this.chargeDamageMultiplier = chargeDamageMultiplier;
        this.engagedStartTick = engagedStartTick;
        this.tiredStartTick = tiredStartTick;
        this.disengagedStartTick = disengagedStartTick;
        this.totalChargeTicks = totalChargeTicks;
    }

    // ──────────────────────────────────────────────
    // JAB ATTACK
    // ──────────────────────────────────────────────

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Apply base jab damage (vanilla handles the primary hit via attack strength)
        // Pierce: hit additional entities in the line (half damage)
        if (attacker.getWorld() instanceof ServerWorld serverWorld) {
            Vec3d pos = attacker.getEyePos();
            Vec3d look = attacker.getRotationVec(1.0f);
            Box lineBox = new Box(pos, pos.add(look.multiply(MAX_REACH))).expand(HITBOX_INFLATION);

            serverWorld.getEntitiesByClass(LivingEntity.class, lineBox,
                    e -> e != attacker && e != target && e.isAlive()).forEach(pierce -> {
                double dist = attacker.squaredDistanceTo(pierce);
                if (dist >= MIN_REACH * MIN_REACH) {
                    pierce.damage(serverWorld.getDamageSources().mobAttack(attacker), jabDamage * 0.5f);
                }
            });

            if (attacker instanceof PlayerEntity player) {
                applyLunge(stack, player, serverWorld);
            }
        }
        // Only cost 1 durability — do NOT call super (SwordItem would add another)
        stack.damage(1, attacker, LivingEntity.getSlotForHand(attacker.getActiveHand()));
        return true;
    }

    // ──────────────────────────────────────────────
    // CHARGE ATTACK
    // ──────────────────────────────────────────────

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPYGLASS;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return totalChargeTicks;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ITEM_SPYGLASS_USE, SoundCategory.PLAYERS, 0.8f,
                1.0f + world.random.nextFloat() * 0.1f);
        return TypedActionResult.consume(stack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient) return;
        // Only players can deal charge damage (safe cast guard)
        if (!(user instanceof PlayerEntity player)) return;

        int usedTicks = totalChargeTicks - remainingUseTicks;
        ChargeState state = getChargeState(usedTicks);
        if (state == ChargeState.IDLE) return;

        // Throttle: only scan every 5 ticks to avoid rapid-fire hits
        if (usedTicks % 5 != 0) return;

        Vec3d pos = user.getEyePos();
        Vec3d look = user.getRotationVec(1.0f);
        Box searchBox = user.getBoundingBox().stretch(look.multiply(MAX_REACH)).expand(HITBOX_INFLATION);

        List<Entity> targets = world.getEntitiesByClass(Entity.class, searchBox,
                e -> e != user && e instanceof LivingEntity && e.isAlive());

        for (Entity target : targets) {
            double dist = user.squaredDistanceTo(target);
            if (dist < MIN_REACH * MIN_REACH) continue;

            Vec3d relVel = user.getVelocity().subtract(target.getVelocity());
            double approachSpeed = relVel.dotProduct(look); // blocks/tick

            float damage = computeChargeDamage(approachSpeed);
            if (damage <= 0) continue;

            boolean doKnockback = false;
            boolean doDismount = false;

            if (state == ChargeState.ENGAGED) {
                if (approachSpeed * 20 >= ENGAGED_DISMOUNT_SPEED) {
                    doKnockback = true;
                    doDismount = true;
                } else if (approachSpeed * 20 >= ENGAGED_KNOCKBACK_SPEED) {
                    doKnockback = true;
                }
            } else if (state == ChargeState.TIRED) {
                if (approachSpeed * 20 >= TIRED_KNOCKBACK_SPEED) {
                    doKnockback = true;
                }
            }

            target.damage(world.getDamageSources().playerAttack(player), damage);
            if (doKnockback && target instanceof LivingEntity living) {
                living.takeKnockback(0.5, -look.x, -look.z);
            }
            if (doDismount && target.hasPassengers()) {
                target.removeAllPassengers();
            }

            stack.damage(1, user, LivingEntity.getSlotForHand(user.getActiveHand()));
            break; // one target per scan
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ITEM_SPYGLASS_STOP_USING, SoundCategory.PLAYERS, 0.8f, 1.0f);
    }

    // ──────────────────────────────────────────────
    // LUNGE
    // ──────────────────────────────────────────────

    private void applyLunge(ItemStack stack, PlayerEntity player, ServerWorld world) {
        var registry = world.getRegistryManager().getOrThrow(net.minecraft.registry.RegistryKeys.ENCHANTMENT);
        var entryOpt = registry.getEntry(ModEnchantments.LUNGE_KEY);
        if (entryOpt.isEmpty()) return;

        int lungeLevel = EnchantmentHelper.getLevel(entryOpt.get(), stack);
        if (lungeLevel <= 0) return;
        if (player.getHungerManager().getFoodLevel() < 6) return;
        if (player.isTouchingWater() || player.isFallFlying()) return;

        double speed = lungeLevel * 0.458; // blocks/tick
        Vec3d look = player.getRotationVec(1.0f);
        Vec3d horizontal = new Vec3d(look.x, 0, look.z).normalize().multiply(speed);
        player.setVelocity(horizontal.x, player.getVelocity().y, horizontal.z);
        player.velocityModified = true;

        // Exhaust hunger: 4f exhaustion ≈ 1 hunger point per level
        player.getHungerManager().addExhaustion(lungeLevel * 4.0f);

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST, SoundCategory.PLAYERS, 0.6f, 1.2f);
    }

    // ──────────────────────────────────────────────
    // CHARGE STATE
    // ──────────────────────────────────────────────

    public enum ChargeState { IDLE, ENGAGED, TIRED, DISENGAGED }

    public ChargeState getChargeState(int usedTicks) {
        if (usedTicks < engagedStartTick)    return ChargeState.IDLE;
        if (usedTicks < tiredStartTick)      return ChargeState.ENGAGED;
        if (usedTicks < disengagedStartTick) return ChargeState.TIRED;
        if (usedTicks < totalChargeTicks)    return ChargeState.DISENGAGED;
        return ChargeState.IDLE;
    }

    private float computeChargeDamage(double approachSpeedPerTick) {
        if (approachSpeedPerTick * 20 < DISENGAGED_DAMAGE_SPEED) return 0;
        float velocityFactor = MathHelper.clamp((float)(approachSpeedPerTick * 20) / 10f, 0.5f, 2.0f);
        return jabDamage * chargeDamageMultiplier * velocityFactor;
    }

    // ──────────────────────────────────────────────
    // TOOLTIP
    // ──────────────────────────────────────────────

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.spearmod.spear.tooltip.jab"));
        tooltip.add(Text.translatable("item.spearmod.spear.tooltip.charge"));
        tooltip.add(Text.translatable("item.spearmod.spear.tooltip.reach"));
    }

    public float getJabDamage() { return jabDamage; }
    public float getChargeDamageMultiplier() { return chargeDamageMultiplier; }
}
