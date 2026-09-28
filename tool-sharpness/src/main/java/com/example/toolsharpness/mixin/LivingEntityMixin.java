package com.example.toolsharpness.mixin;

import com.example.toolsharpness.SharpnessService;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@ModifyVariable(
			method = "damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z",
			at = @At("HEAD"),
			argsOnly = true,
			ordinal = 0
	)
	private float tool_sharpness$applySharpness(float amount, ServerWorld world, DamageSource source) {
		if (!(source.getAttacker() instanceof PlayerEntity player) || !source.isDirect()) {
			return amount;
		}
		ItemStack stack = player.getMainHandStack();
		if (!SharpnessService.hasSharpness(stack)) {
			return amount;
		}
		float factor = SharpnessService.getDamageFactor(stack, player);
		SharpnessService.decayOnHit(stack);
		return amount * factor;
	}
}
