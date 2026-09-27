package com.example.attack_anime_fix.mixin;

import com.example.attack_anime_fix.survival.heat.FuelTags;
import net.minecraft.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlastFurnaceBlockEntity.class)
public abstract class BlastFurnaceBlockEntityMixin {
	@Inject(method = "getFuelTime", at = @At("HEAD"), cancellable = true)
	private void survival_updated$lavaOnly(FuelRegistry fuelRegistry, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
		if (!FuelTags.isLavaFuel(stack)) {
			cir.setReturnValue(0);
		}
	}
}
