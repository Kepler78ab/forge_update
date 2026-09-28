package com.example.swordguard.combat;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sword guard windows + riposte. Applies to {@link ItemTags#SWORDS}.
 * If offhand holds a shield while main-hand sword blocks would apply, prefer vanilla shield behavior
 * (blocking item is the shield — this service no-ops timed windows).
 */
public final class GuardService {
	private static final Map<UUID, Long> RIPOSTE_UNTIL = new ConcurrentHashMap<>();

	private GuardService() {
	}

	public static boolean isSword(ItemStack stack) {
		return !stack.isEmpty() && stack.isIn(ItemTags.SWORDS);
	}

	public static boolean isShield(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof ShieldItem;
	}

	/** Active sword-guard stance: blocking with a sword, and offhand is not a shield. */
	public static boolean isSwordGuarding(LivingEntity entity) {
		ItemStack blocking = entity.getBlockingItem();
		if (!isSword(blocking)) {
			return false;
		}
		if (entity instanceof PlayerEntity player && isShield(player.getOffHandStack())) {
			return false;
		}
		return true;
	}

	public static boolean isDualSwordGuarding(LivingEntity entity) {
		if (!(entity instanceof PlayerEntity player)) {
			return false;
		}
		return isSword(player.getMainHandStack())
				&& isSword(player.getOffHandStack())
				&& !isShield(player.getOffHandStack());
	}

	public static float windowFactor(LivingEntity entity) {
		if (!isSwordGuarding(entity)) {
			return 1.0f;
		}
		int held = entity.getItemUseTime();
		return GuardWindows.mitigation(held, isDualSwordGuarding(entity));
	}

	public static void onBlocked(LivingEntity defender, ServerWorld world, DamageSource source, float blockedAmount) {
		if (blockedAmount <= 0.0f || !(defender instanceof PlayerEntity player)) {
			return;
		}
		if (!isSwordGuarding(defender)) {
			return;
		}
		float factor = windowFactor(defender);
		if (factor < 0.999f) {
			return;
		}
		player.timeUntilRegen = 10;
		RIPOSTE_UNTIL.put(player.getUuid(), world.getTime() + GuardWindows.RIPOSTE_TICKS);
	}

	public static float riposteDamageMultiplier(PlayerEntity player, ServerWorld world) {
		Long until = RIPOSTE_UNTIL.get(player.getUuid());
		if (until == null || world.getTime() > until) {
			RIPOSTE_UNTIL.remove(player.getUuid());
			return 1.0f;
		}
		RIPOSTE_UNTIL.remove(player.getUuid());
		return 1.5f;
	}
}
