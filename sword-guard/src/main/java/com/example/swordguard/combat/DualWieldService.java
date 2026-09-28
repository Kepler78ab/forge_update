package com.example.swordguard.combat;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dual-wield alternation when both hands hold swords ({@link ItemTags#SWORDS}).
 */
public final class DualWieldService {
	private static final Map<UUID, Boolean> NEXT_OFFHAND = new ConcurrentHashMap<>();
	private static final Map<UUID, Boolean> SWAPPED = new ConcurrentHashMap<>();

	private DualWieldService() {
	}

	public static boolean isOneHandSword(ItemStack stack) {
		return GuardService.isSword(stack);
	}

	public static boolean isDualWielding(PlayerEntity player) {
		return isOneHandSword(player.getMainHandStack()) && isOneHandSword(player.getOffHandStack());
	}

	public static void beforeAttack(PlayerEntity player) {
		if (!isDualWielding(player)) {
			SWAPPED.remove(player.getUuid());
			return;
		}
		boolean useOff = NEXT_OFFHAND.getOrDefault(player.getUuid(), false);
		if (useOff) {
			swapHands(player);
			SWAPPED.put(player.getUuid(), true);
		} else {
			SWAPPED.put(player.getUuid(), false);
		}
	}

	public static void afterAttack(PlayerEntity player) {
		Boolean swapped = SWAPPED.remove(player.getUuid());
		if (swapped != null && swapped) {
			swapHands(player);
		}
		if (isDualWielding(player)) {
			boolean prev = NEXT_OFFHAND.getOrDefault(player.getUuid(), false);
			NEXT_OFFHAND.put(player.getUuid(), !prev);
		} else {
			NEXT_OFFHAND.remove(player.getUuid());
		}
	}

	private static void swapHands(PlayerEntity player) {
		ItemStack main = player.getMainHandStack();
		ItemStack off = player.getOffHandStack();
		player.setStackInHand(Hand.MAIN_HAND, off);
		player.setStackInHand(Hand.OFF_HAND, main);
	}
}
