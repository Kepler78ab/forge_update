package com.example.swordguard.mixin;

import com.example.swordguard.combat.GuardService;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@ModifyVariable(
			method = "damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z",
			at = @At("HEAD"),
			argsOnly = true,
			ordinal = 0
	)
	private float sword_guard$riposte(float amount, ServerWorld world, DamageSource source) {
		if (!(source.getAttacker() instanceof PlayerEntity player) || !source.isDirect()) {
			return amount;
		}
		float riposte = GuardService.riposteDamageMultiplier(player, world);
		return riposte == 1.0f ? amount : amount * riposte;
	}

	@Inject(method = "getDamageBlockedAmount", at = @At("RETURN"), cancellable = true)
	private void sword_guard$scaleWindows(
			ServerWorld world,
			DamageSource source,
			float amount,
			CallbackInfoReturnable<Float> cir
	) {
		float blocked = cir.getReturnValueF();
		if (blocked <= 0.0f) {
			return;
		}
		LivingEntity self = (LivingEntity) (Object) this;
		float factor = GuardService.windowFactor(self);
		float scaled = blocked * factor;
		cir.setReturnValue(scaled);
		if (scaled > 0.0f) {
			GuardService.onBlocked(self, world, source, scaled);
		}
	}
}
