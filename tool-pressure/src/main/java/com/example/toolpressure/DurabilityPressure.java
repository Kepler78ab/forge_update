package com.example.toolpressure;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.List;

/**
 * Durability: default ×1/2; diamond ×2/3; netherite ×3/4. Applies to tools + armor.
 */
public final class DurabilityPressure {
	private DurabilityPressure() {
	}

	public static void register() {
		DefaultItemComponentEvents.MODIFY.register(DurabilityPressure::apply);
	}

	private static void apply(DefaultItemComponentEvents.ModifyContext context) {
		scaleAll(context, halfItems(), 1, 2);
		scaleAll(context, diamondItems(), 2, 3);
		scaleAll(context, netheriteItems(), 3, 4);
	}

	private static void scaleAll(DefaultItemComponentEvents.ModifyContext context, List<Item> items, int num, int den) {
		for (Item item : items) {
			Integer max = item.getComponents().get(DataComponentTypes.MAX_DAMAGE);
			if (max == null || max <= 0) {
				continue;
			}
			int scaled = Math.max(1, max * num / den);
			context.modify(item, builder -> builder.add(DataComponentTypes.MAX_DAMAGE, scaled));
		}
	}

	private static List<Item> halfItems() {
		return List.of(
				Items.WOODEN_SWORD, Items.WOODEN_SHOVEL, Items.WOODEN_PICKAXE, Items.WOODEN_AXE, Items.WOODEN_HOE,
				Items.STONE_SWORD, Items.STONE_SHOVEL, Items.STONE_PICKAXE, Items.STONE_AXE, Items.STONE_HOE,
				Items.COPPER_SWORD, Items.COPPER_SHOVEL, Items.COPPER_PICKAXE, Items.COPPER_AXE, Items.COPPER_HOE,
				Items.IRON_SWORD, Items.IRON_SHOVEL, Items.IRON_PICKAXE, Items.IRON_AXE, Items.IRON_HOE,
				Items.GOLDEN_SWORD, Items.GOLDEN_SHOVEL, Items.GOLDEN_PICKAXE, Items.GOLDEN_AXE, Items.GOLDEN_HOE,
				Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS,
				Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS,
				Items.COPPER_HELMET, Items.COPPER_CHESTPLATE, Items.COPPER_LEGGINGS, Items.COPPER_BOOTS,
				Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS,
				Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS
		);
	}

	private static List<Item> diamondItems() {
		return List.of(
				Items.DIAMOND_SWORD, Items.DIAMOND_SHOVEL, Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE, Items.DIAMOND_HOE,
				Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS
		);
	}

	private static List<Item> netheriteItems() {
		return List.of(
				Items.NETHERITE_SWORD, Items.NETHERITE_SHOVEL, Items.NETHERITE_PICKAXE, Items.NETHERITE_AXE, Items.NETHERITE_HOE,
				Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS
		);
	}
}
