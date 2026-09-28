package com.example.toolsharpness.mixin;

import com.example.toolsharpness.SharpnessService;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
	@Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
	private void tool_sharpness$axeSharpnessMining(BlockState state, CallbackInfoReturnable<Float> cir) {
		PlayerEntity self = (PlayerEntity) (Object) this;
		ItemStack stack = self.getMainHandStack();
		float factor = SharpnessService.getMiningFactor(stack, self);
		if (factor != 1.0f) {
			cir.setReturnValue(cir.getReturnValueF() * factor);
		}
	}
}
