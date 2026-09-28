package com.example.forgecraft.survival.heat;

import com.example.forgecraft.survival.SurvivalMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public final class FuelTags {
	public static final TagKey<Item> FUEL_LOW = TagKey.of(RegistryKeys.ITEM, SurvivalMod.id("fuel_low"));
	public static final TagKey<Item> FUEL_MID = TagKey.of(RegistryKeys.ITEM, SurvivalMod.id("fuel_mid"));
	public static final TagKey<Item> FUEL_HIGH = TagKey.of(RegistryKeys.ITEM, SurvivalMod.id("fuel_high"));

	private FuelTags() {
	}

	public static FuelLevel levelOf(ItemStack stack) {
		if (stack.isEmpty()) {
			return FuelLevel.NONE;
		}
		if (isLavaFuel(stack) || stack.isIn(FUEL_HIGH)) {
			return FuelLevel.HIGH;
		}
		if (stack.isIn(FUEL_MID)) {
			return FuelLevel.MID;
		}
		if (stack.isIn(FUEL_LOW)) {
			return FuelLevel.LOW;
		}
		if (stack.isOf(Items.COAL_BLOCK)) {
			return FuelLevel.HIGH;
		}
		if (stack.isOf(Items.COAL) || stack.isOf(Items.CHARCOAL)) {
			return FuelLevel.MID;
		}
		return FuelLevel.NONE;
	}

	public static boolean isLavaFuel(ItemStack stack) {
		return stack.isOf(Items.LAVA_BUCKET);
	}

	/** Igniter slot: torch or lava bucket. */
	public static boolean isIgniter(ItemStack stack) {
		return stack.isOf(Items.TORCH)
				|| stack.isOf(Items.SOUL_TORCH)
				|| stack.isOf(Items.LAVA_BUCKET);
	}
}
