package com.example.swordguard.mixin;

import com.example.swordguard.combat.HandednessService;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {
	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void sword_guard$emptyOffhand(
			World world,
			PlayerEntity user,
			Hand hand,
			CallbackInfoReturnable<ActionResult> cir
	) {
		ItemStack stack = user.getStackInHand(hand);
		if (!HandednessService.requiresEmptyOffhandToUse(stack)) {
			return;
		}
		Hand other = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
		if (!user.getStackInHand(other).isEmpty()) {
			cir.setReturnValue(ActionResult.FAIL);
		}
	}
}
