package com.example.forgecraft.survival.combat;

import com.example.forgecraft.survival.forge.WeaponProfileData;
import com.example.forgecraft.survival.forge.WeaponProfileId;
import com.example.forgecraft.survival.registry.ModComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

/**
 * Clears offhand when holding a forge two-hand profile weapon (e.g. sword_long).
 */
public final class TwoHandProfileService {
	private TwoHandProfileService() {
	}

	public static boolean isTwoHandProfile(ItemStack stack) {
		WeaponProfileData profile = stack.get(ModComponents.WEAPON_PROFILE);
		return profile != null && profile.handedness() == WeaponProfileId.Handedness.TWO_HAND;
	}

	public static void tick(PlayerEntity player) {
		ItemStack main = player.getMainHandStack();
		ItemStack off = player.getOffHandStack();
		if (isTwoHandProfile(main) && !off.isEmpty()) {
			player.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
			if (!player.giveItemStack(off)) {
				player.dropItem(off, false);
			}
			return;
		}
		if (isTwoHandProfile(off)) {
			ItemStack moved = off.copy();
			player.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
			if (!player.giveItemStack(moved)) {
				player.dropItem(moved, false);
			}
		}
	}
}
