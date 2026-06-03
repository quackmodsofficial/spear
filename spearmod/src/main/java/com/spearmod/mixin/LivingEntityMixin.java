package com.spearmod.mixin;

import com.spearmod.item.SpearItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to extend the player's attack reach when holding a SpearItem.
 * Vanilla reach is 3 blocks in survival; spears extend to 4.5 blocks.
 *
 * We inject into the reach-distance attribute resolution so it integrates
 * cleanly with ViaBackwards protocol translation.
 */
@Mixin(PlayerEntity.class)
public abstract class LivingEntityMixin {

    /**
     * When the game asks for the player's block interaction range and the player
     * is holding a spear, return the spear's extended max reach (4.5f).
     * This affects raycast length for left-click attacks.
     */
    @Inject(method = "getBlockInteractionRange", at = @At("RETURN"), cancellable = true)
    private void spearmod_extendAttackReach(CallbackInfoReturnable<Double> cir) {
        PlayerEntity self = (PlayerEntity)(Object)this;
        ItemStack mainHand = self.getMainHandStack();
        if (mainHand.getItem() instanceof SpearItem) {
            // Override reach to spear max range
            cir.setReturnValue((double) SpearItem.MAX_REACH);
        }
    }

    @Inject(method = "getEntityInteractionRange", at = @At("RETURN"), cancellable = true)
    private void spearmod_extendEntityReach(CallbackInfoReturnable<Double> cir) {
        PlayerEntity self = (PlayerEntity)(Object)this;
        ItemStack mainHand = self.getMainHandStack();
        if (mainHand.getItem() instanceof SpearItem) {
            cir.setReturnValue((double) SpearItem.MAX_REACH);
        }
    }
}
