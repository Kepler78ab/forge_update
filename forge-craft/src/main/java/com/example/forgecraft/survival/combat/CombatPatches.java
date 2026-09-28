package com.example.forgecraft.survival.combat;

import com.example.forgecraft.survival.registry.ModComponents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.List;

/**
 * Armor-class defaults only (sword guard moved to sword_guard mod).
 */
public final class CombatPatches {
	private CombatPatches() {
	}

	public static void register() {
		DefaultItemComponentEvents.MODIFY.register(CombatPatches::patchArmor);
	}

	private static void patchArmor(DefaultItemComponentEvents.ModifyContext context) {
		tagArmor(context, ArmorClass.LIGHT, List.of(
				Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS,
				Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS
		));
		tagArmor(context, ArmorClass.MEDIUM, List.of(
				Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS,
				Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS,
				Items.COPPER_HELMET, Items.COPPER_CHESTPLATE, Items.COPPER_LEGGINGS, Items.COPPER_BOOTS
		));
		tagArmor(context, ArmorClass.HEAVY, List.of(
				Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS,
				Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS
		));
	}

	private static void tagArmor(
			DefaultItemComponentEvents.ModifyContext context,
			ArmorClass armorClass,
			List<Item> items
	) {
		ArmorClassData data = ArmorClassData.of(armorClass);
		for (Item item : items) {
			context.modify(item, builder -> builder.add(ModComponents.ARMOR_CLASS, data));
		}
	}
}
