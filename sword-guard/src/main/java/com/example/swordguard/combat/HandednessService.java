package com.example.swordguard.combat;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

/**
 * Bow/crossbow require empty opposite hand.
 * Two-handed melee: items in {@code #sword_guard:two_handed} (forge can add via datapack).
 */
public final class HandednessService {
	public static final TagKey<net.minecraft.item.Item> TWO_HANDED =
			TagKey.of(RegistryKeys.ITEM, Identifier.of("sword_guard", "two_handed"));

	private HandednessService() {
	}

	public static boolean isTwoHandMelee(ItemStack stack) {
		return !stack.isEmpty() && stack.isIn(TWO_HANDED);
	}

	public static boolean requiresEmptyOffhandToUse(ItemStack stack) {
		return stack.isOf(Items.BOW) || stack.isOf(Items.CROSSBOW) || isTwoHandMelee(stack);
	}

	public static void tick(PlayerEntity player) {
		ItemStack main = player.getMainHandStack();
		ItemStack off = player.getOffHandStack();
		if (isTwoHandMelee(main) && !off.isEmpty()) {
			player.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
			if (!player.giveItemStack(off)) {
				player.dropItem(off, false);
			}
			return;
		}
		if (isTwoHandMelee(off)) {
			ItemStack moved = off.copy();
			player.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
			if (!player.giveItemStack(moved)) {
				player.dropItem(moved, false);
			}
		}
	}
}
