package com.example.toolsharpness;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Vanilla tool balance + factory sharpness; soft-hooks expansion / forge tools by id.
 */
public final class ToolPatches {
	private static final double STONE_SWORD_ATTACK_SPEED = -3.0;

	private ToolPatches() {
	}

	public static void register() {
		DefaultItemComponentEvents.MODIFY.register(context -> {
			weakenWoodenTools(context);
			slowStoneSword(context);
			applyFactorySharpness(context);
			applyExternalTools(context);
		});
	}

	private static void weakenWoodenTools(DefaultItemComponentEvents.ModifyContext context) {
		ToolComponent swordTool = Items.WOODEN_SWORD.getComponents().get(DataComponentTypes.TOOL);
		if (swordTool == null) {
			return;
		}
		for (Item item : List.of(Items.WOODEN_PICKAXE, Items.WOODEN_AXE, Items.WOODEN_HOE)) {
			context.modify(item, builder -> builder.add(DataComponentTypes.TOOL, swordTool));
		}
	}

	private static void slowStoneSword(DefaultItemComponentEvents.ModifyContext context) {
		context.modify(Items.STONE_SWORD, builder -> {
			AttributeModifiersComponent current = Items.STONE_SWORD.getComponents()
					.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
			if (current == null) {
				return;
			}
			AttributeModifiersComponent.Builder rebuilt = AttributeModifiersComponent.builder();
			for (AttributeModifiersComponent.Entry entry : current.modifiers()) {
				if (entry.attribute().matches(EntityAttributes.ATTACK_SPEED)) {
					rebuilt.add(
							entry.attribute(),
							new EntityAttributeModifier(
									entry.modifier().id(),
									STONE_SWORD_ATTACK_SPEED,
									entry.modifier().operation()
							),
							entry.slot(),
							entry.display()
					);
				} else {
					rebuilt.add(entry.attribute(), entry.modifier(), entry.slot(), entry.display());
				}
			}
			builder.add(DataComponentTypes.ATTRIBUTE_MODIFIERS, rebuilt.build());
		});
	}

	private static void applyFactorySharpness(DefaultItemComponentEvents.ModifyContext context) {
		List<Item> full = List.of(
				Items.WOODEN_SWORD, Items.WOODEN_SHOVEL, Items.WOODEN_PICKAXE, Items.WOODEN_AXE, Items.WOODEN_HOE,
				Items.STONE_SWORD, Items.STONE_SHOVEL, Items.STONE_PICKAXE, Items.STONE_AXE, Items.STONE_HOE,
				Items.GOLDEN_SWORD, Items.GOLDEN_SHOVEL, Items.GOLDEN_PICKAXE, Items.GOLDEN_AXE, Items.GOLDEN_HOE
		);
		for (Item item : full) {
			float max = maxForVanilla(item);
			context.modify(item, builder -> builder.add(SharpnessComponents.SHARPNESS, SharpnessData.full(max)));
		}

		List<Item> dull = List.of(
				Items.COPPER_SWORD, Items.COPPER_SHOVEL, Items.COPPER_PICKAXE, Items.COPPER_AXE, Items.COPPER_HOE,
				Items.IRON_SWORD, Items.IRON_SHOVEL, Items.IRON_PICKAXE, Items.IRON_AXE, Items.IRON_HOE,
				Items.DIAMOND_SWORD, Items.DIAMOND_SHOVEL, Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE, Items.DIAMOND_HOE,
				Items.NETHERITE_SWORD, Items.NETHERITE_SHOVEL, Items.NETHERITE_PICKAXE, Items.NETHERITE_AXE, Items.NETHERITE_HOE
		);
		for (Item item : dull) {
			float max = maxForVanilla(item);
			context.modify(item, builder -> builder.add(SharpnessComponents.SHARPNESS, SharpnessData.dull(max)));
		}
	}

	/** Soft hooks 鈥?no compile dependency on other packs. */
	private static void applyExternalTools(DefaultItemComponentEvents.ModifyContext context) {
		dullById(context, "weapon_expansion", List.of(
				"steel_sword", "steel_shovel", "steel_pickaxe", "steel_axe", "steel_hoe",
				"obsidian_sword", "obsidian_shovel", "obsidian_pickaxe", "obsidian_axe", "obsidian_hoe"
		), 100.0f);
		dullById(context, "weapon_expansion", List.of(
				"emerald_sword", "emerald_shovel", "emerald_pickaxe", "emerald_axe", "emerald_hoe",
				"redstone_sword", "redstone_shovel", "redstone_pickaxe", "redstone_axe", "redstone_hoe",
				"lapis_sword", "lapis_shovel", "lapis_pickaxe", "lapis_axe", "lapis_hoe"
		), 40.0f);
		dullById(context, "forge_craft", List.of(
				"forged_sword", "forged_axe", "forged_pickaxe", "forged_shovel", "forged_hoe"
		), 100.0f);
	}

	private static void dullById(DefaultItemComponentEvents.ModifyContext context, String ns, List<String> paths, float max) {
		for (String path : paths) {
			Item item = Registries.ITEM.get(Identifier.of(ns, path));
			if (item == Items.AIR || item == null) {
				continue;
			}
			context.modify(item, builder -> builder.add(SharpnessComponents.SHARPNESS, SharpnessData.dull(max)));
		}
	}

	private static float maxForVanilla(Item item) {
		if (item == Items.NETHERITE_SWORD || item == Items.NETHERITE_AXE
				|| item == Items.NETHERITE_PICKAXE || item == Items.NETHERITE_SHOVEL || item == Items.NETHERITE_HOE) {
			return 120.0f;
		}
		if (item == Items.DIAMOND_SWORD || item == Items.DIAMOND_AXE
				|| item == Items.DIAMOND_PICKAXE || item == Items.DIAMOND_SHOVEL || item == Items.DIAMOND_HOE) {
			return 100.0f;
		}
		if (item == Items.IRON_SWORD || item == Items.IRON_AXE
				|| item == Items.IRON_PICKAXE || item == Items.IRON_SHOVEL || item == Items.IRON_HOE) {
			return 80.0f;
		}
		if (item == Items.COPPER_SWORD || item == Items.COPPER_AXE
				|| item == Items.COPPER_PICKAXE || item == Items.COPPER_SHOVEL || item == Items.COPPER_HOE) {
			return 70.0f;
		}
		if (item == Items.STONE_SWORD || item == Items.STONE_AXE
				|| item == Items.STONE_PICKAXE || item == Items.STONE_SHOVEL || item == Items.STONE_HOE) {
			return 50.0f;
		}
		return 40.0f;
	}
}
