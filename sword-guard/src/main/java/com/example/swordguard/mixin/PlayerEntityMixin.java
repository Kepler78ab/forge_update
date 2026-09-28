package com.example.swordguard.mixin;

import com.example.swordguard.combat.DualWieldService;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
	@Inject(method = "attack", at = @At("HEAD"))
	private void sword_guard$dualBefore(Entity target, CallbackInfo ci) {
		DualWieldService.beforeAttack((PlayerEntity) (Object) this);
	}

	@Inject(method = "attack", at = @At("RETURN"))
	private void sword_guard$dualAfter(Entity target, CallbackInfo ci) {
		DualWieldService.afterAttack((PlayerEntity) (Object) this);
	}
}
