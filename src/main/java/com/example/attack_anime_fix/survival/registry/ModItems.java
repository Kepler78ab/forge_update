package com.example.attack_anime_fix.survival.registry;

import com.example.attack_anime_fix.survival.SurvivalMod;
import com.example.attack_anime_fix.survival.tools.ModToolMaterials;
import com.example.attack_anime_fix.survival.tools.SharpnessData;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public final class ModItems {
	public static Item FIRE_PIT;
	public static Item STEEL_INGOT;
	public static Item WHETSTONE;

	public static Item STEEL_SWORD;
	public static Item STEEL_SHOVEL;
	public static Item STEEL_PICKAXE;
	public static Item STEEL_AXE;
	public static Item STEEL_HOE;

	public static Item EMERALD_SWORD;
	public static Item EMERALD_PICKAXE;
	public static Item REDSTONE_SWORD;
	public static Item REDSTONE_PICKAXE;
	public static Item LAPIS_SWORD;
	public static Item LAPIS_PICKAXE;

	private ModItems() {
	}

	public static void register() {
		FIRE_PIT = registerBlockItem("fire_pit", ModBlocks.FIRE_PIT);
		WHETSTONE = registerBlockItem("whetstone", ModBlocks.WHETSTONE);
		STEEL_INGOT = registerSimple("steel_ingot");

		STEEL_SWORD = registerTool("steel_sword", ModToolMaterials.STEEL, ToolKind.SWORD, 3.0f, -2.4f, SharpnessData.dull(100.0f));
		STEEL_SHOVEL = registerTool("steel_shovel", ModToolMaterials.STEEL, ToolKind.SHOVEL, 1.5f, -3.0f, SharpnessData.dull(100.0f));
		STEEL_PICKAXE = registerTool("steel_pickaxe", ModToolMaterials.STEEL, ToolKind.PICKAXE, 1.0f, -2.8f, SharpnessData.dull(100.0f));
		STEEL_AXE = registerTool("steel_axe", ModToolMaterials.STEEL, ToolKind.AXE, 5.0f, -3.0f, SharpnessData.dull(100.0f));
		STEEL_HOE = registerTool("steel_hoe", ModToolMaterials.STEEL, ToolKind.HOE, -3.0f, 0.0f, SharpnessData.dull(100.0f));

		EMERALD_SWORD = registerTool("emerald_sword", ModToolMaterials.DECORATIVE, ToolKind.SWORD, 3.0f, -1.6f, SharpnessData.full(40.0f));
		EMERALD_PICKAXE = registerTool("emerald_pickaxe", ModToolMaterials.DECORATIVE, ToolKind.PICKAXE, 1.0f, -2.0f, SharpnessData.full(40.0f));
		REDSTONE_SWORD = registerTool("redstone_sword", ModToolMaterials.DECORATIVE, ToolKind.SWORD, 3.0f, -1.6f, SharpnessData.full(40.0f));
		REDSTONE_PICKAXE = registerTool("redstone_pickaxe", ModToolMaterials.DECORATIVE, ToolKind.PICKAXE, 1.0f, -2.0f, SharpnessData.full(40.0f));
		LAPIS_SWORD = registerTool("lapis_sword", ModToolMaterials.DECORATIVE, ToolKind.SWORD, 3.0f, -1.6f, SharpnessData.full(40.0f));
		LAPIS_PICKAXE = registerTool("lapis_pickaxe", ModToolMaterials.DECORATIVE, ToolKind.PICKAXE, 1.0f, -2.0f, SharpnessData.full(40.0f));
	}

	private static Item registerBlockItem(String path, net.minecraft.block.Block block) {
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, SurvivalMod.id(path));
		return Registry.register(
				Registries.ITEM,
				key,
				new BlockItem(block, new Item.Settings().registryKey(key).useBlockPrefixedTranslationKey())
		);
	}

	private static Item registerSimple(String path) {
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, SurvivalMod.id(path));
		return Registry.register(Registries.ITEM, key, new Item(new Item.Settings().registryKey(key)));
	}

	private static Item registerTool(
			String path,
			ToolMaterial material,
			ToolKind kind,
			float attackDamage,
			float attackSpeed,
			SharpnessData sharpness
	) {
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, SurvivalMod.id(path));
		Item.Settings settings = new Item.Settings().registryKey(key).component(ModComponents.SHARPNESS, sharpness);
		Item item = switch (kind) {
			case SWORD -> new Item(settings.sword(material, attackDamage, attackSpeed));
			case SHOVEL -> new Item(settings.shovel(material, attackDamage, attackSpeed));
			case PICKAXE -> new Item(settings.pickaxe(material, attackDamage, attackSpeed));
			case AXE -> new AxeItem(material, attackDamage, attackSpeed, settings);
			case HOE -> new Item(settings.hoe(material, attackDamage, attackSpeed));
		};
		return Registry.register(Registries.ITEM, key, item);
	}

	private enum ToolKind {
		SWORD, SHOVEL, PICKAXE, AXE, HOE
	}
}
