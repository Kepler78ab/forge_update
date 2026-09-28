package com.example.forgecraft.survival.registry;

import com.example.forgecraft.survival.SurvivalMod;
import com.example.forgecraft.survival.forge.ForgePieceItem;
import com.example.forgecraft.survival.smelt.AlloyIngotItem;
import com.example.forgecraft.survival.smelt.MoltenMetalData;
import com.example.forgecraft.survival.smelt.MoltenMetalItem;
import com.example.forgecraft.survival.tools.ModToolMaterials;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public final class ModItems {
	public static Item STEEL_INGOT;
	public static Item ALLOY_INGOT;
	public static Item FORGE_TEMPLATE;
	public static Item FORGE_PIECE;
	public static Item MOLTEN_METAL;
	public static Item FORGED_SWORD;
	public static Item FORGED_AXE;
	public static Item FORGED_PICKAXE;
	public static Item FORGED_SHOVEL;
	public static Item FORGED_HOE;

	private ModItems() {
	}

	public static void register() {
		registerBlockItem("smelting_blast_furnace", ModBlocks.SMELTING_BLAST_FURNACE);
		registerBlockItem("stone_hopper", ModBlocks.STONE_HOPPER);
		registerBlockItem("template_bench", ModBlocks.TEMPLATE_BENCH);
		STEEL_INGOT = registerSimple("steel_ingot");

		RegistryKey<Item> alloyKey = RegistryKey.of(RegistryKeys.ITEM, SurvivalMod.id("alloy_ingot"));
		ALLOY_INGOT = Registry.register(
				Registries.ITEM,
				alloyKey,
				new AlloyIngotItem(new Item.Settings()
						.maxCount(64)
						.component(ModComponents.MOLTEN_METAL, MoltenMetalData.of("iron", 1))
						.registryKey(alloyKey))
		);

		RegistryKey<Item> templateKey = RegistryKey.of(RegistryKeys.ITEM, SurvivalMod.id("forge_template"));
		FORGE_TEMPLATE = Registry.register(
				Registries.ITEM,
				templateKey,
				new Item(new Item.Settings().maxCount(16).registryKey(templateKey))
		);

		RegistryKey<Item> pieceKey = RegistryKey.of(RegistryKeys.ITEM, SurvivalMod.id("forge_piece"));
		FORGE_PIECE = Registry.register(
				Registries.ITEM,
				pieceKey,
				new ForgePieceItem(new Item.Settings().maxCount(1).registryKey(pieceKey))
		);

		RegistryKey<Item> moltenKey = RegistryKey.of(RegistryKeys.ITEM, SurvivalMod.id("molten_metal"));
		MOLTEN_METAL = Registry.register(
				Registries.ITEM,
				moltenKey,
				new MoltenMetalItem(new Item.Settings()
						.maxCount(1)
						.component(ModComponents.MOLTEN_METAL, MoltenMetalData.of("iron", 8))
						.registryKey(moltenKey))
		);

		FORGED_SWORD = registerTool("forged_sword", ModToolMaterials.STEEL, ToolKind.SWORD, 3.0f, -2.4f);
		FORGED_AXE = registerTool("forged_axe", ModToolMaterials.STEEL, ToolKind.AXE, 5.0f, -3.0f);
		FORGED_PICKAXE = registerTool("forged_pickaxe", ModToolMaterials.STEEL, ToolKind.PICKAXE, 1.0f, -2.8f);
		FORGED_SHOVEL = registerTool("forged_shovel", ModToolMaterials.STEEL, ToolKind.SHOVEL, 1.5f, -3.0f);
		FORGED_HOE = registerTool("forged_hoe", ModToolMaterials.STEEL, ToolKind.HOE, -3.0f, 0.0f);
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
			float attackSpeed
	) {
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, SurvivalMod.id(path));
		Item.Settings settings = new Item.Settings().registryKey(key);
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
