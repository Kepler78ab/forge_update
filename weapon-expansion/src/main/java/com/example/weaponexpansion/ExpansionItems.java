package com.example.weaponexpansion;

import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ExpansionItems {
	public static Item STEEL_SWORD;
	public static Item STEEL_SHOVEL;
	public static Item STEEL_PICKAXE;
	public static Item STEEL_AXE;
	public static Item STEEL_HOE;

	public static Item OBSIDIAN_SWORD;
	public static Item OBSIDIAN_SHOVEL;
	public static Item OBSIDIAN_PICKAXE;
	public static Item OBSIDIAN_AXE;
	public static Item OBSIDIAN_HOE;

	public static Item EMERALD_SWORD;
	public static Item EMERALD_SHOVEL;
	public static Item EMERALD_PICKAXE;
	public static Item EMERALD_AXE;
	public static Item EMERALD_HOE;

	public static Item REDSTONE_SWORD;
	public static Item REDSTONE_SHOVEL;
	public static Item REDSTONE_PICKAXE;
	public static Item REDSTONE_AXE;
	public static Item REDSTONE_HOE;

	public static Item LAPIS_SWORD;
	public static Item LAPIS_SHOVEL;
	public static Item LAPIS_PICKAXE;
	public static Item LAPIS_AXE;
	public static Item LAPIS_HOE;

	private ExpansionItems() {
	}

	public static void register() {
		STEEL_SWORD = tool("steel_sword", ExpansionToolMaterials.STEEL, ToolKind.SWORD, 3.0f, -2.4f);
		STEEL_SHOVEL = tool("steel_shovel", ExpansionToolMaterials.STEEL, ToolKind.SHOVEL, 1.5f, -3.0f);
		STEEL_PICKAXE = tool("steel_pickaxe", ExpansionToolMaterials.STEEL, ToolKind.PICKAXE, 1.0f, -2.8f);
		STEEL_AXE = tool("steel_axe", ExpansionToolMaterials.STEEL, ToolKind.AXE, 5.0f, -3.0f);
		STEEL_HOE = tool("steel_hoe", ExpansionToolMaterials.STEEL, ToolKind.HOE, -3.0f, 0.0f);

		OBSIDIAN_SWORD = tool("obsidian_sword", ExpansionToolMaterials.OBSIDIAN, ToolKind.SWORD, 3.0f, -2.4f);
		OBSIDIAN_SHOVEL = tool("obsidian_shovel", ExpansionToolMaterials.OBSIDIAN, ToolKind.SHOVEL, 1.5f, -3.0f);
		OBSIDIAN_PICKAXE = tool("obsidian_pickaxe", ExpansionToolMaterials.OBSIDIAN, ToolKind.PICKAXE, 1.0f, -2.8f);
		OBSIDIAN_AXE = tool("obsidian_axe", ExpansionToolMaterials.OBSIDIAN, ToolKind.AXE, 5.0f, -3.0f);
		OBSIDIAN_HOE = tool("obsidian_hoe", ExpansionToolMaterials.OBSIDIAN, ToolKind.HOE, -3.0f, 0.0f);

		EMERALD_SWORD = tool("emerald_sword", ExpansionToolMaterials.DECORATIVE, ToolKind.SWORD, 3.0f, -1.6f);
		EMERALD_SHOVEL = tool("emerald_shovel", ExpansionToolMaterials.DECORATIVE, ToolKind.SHOVEL, 1.5f, -2.5f);
		EMERALD_PICKAXE = tool("emerald_pickaxe", ExpansionToolMaterials.DECORATIVE, ToolKind.PICKAXE, 1.0f, -2.0f);
		EMERALD_AXE = tool("emerald_axe", ExpansionToolMaterials.DECORATIVE, ToolKind.AXE, 5.0f, -2.5f);
		EMERALD_HOE = tool("emerald_hoe", ExpansionToolMaterials.DECORATIVE, ToolKind.HOE, -2.0f, 0.0f);

		REDSTONE_SWORD = tool("redstone_sword", ExpansionToolMaterials.DECORATIVE, ToolKind.SWORD, 3.0f, -1.6f);
		REDSTONE_SHOVEL = tool("redstone_shovel", ExpansionToolMaterials.DECORATIVE, ToolKind.SHOVEL, 1.5f, -2.5f);
		REDSTONE_PICKAXE = tool("redstone_pickaxe", ExpansionToolMaterials.DECORATIVE, ToolKind.PICKAXE, 1.0f, -2.0f);
		REDSTONE_AXE = tool("redstone_axe", ExpansionToolMaterials.DECORATIVE, ToolKind.AXE, 5.0f, -2.5f);
		REDSTONE_HOE = tool("redstone_hoe", ExpansionToolMaterials.DECORATIVE, ToolKind.HOE, -2.0f, 0.0f);

		LAPIS_SWORD = tool("lapis_sword", ExpansionToolMaterials.DECORATIVE, ToolKind.SWORD, 3.0f, -1.6f);
		LAPIS_SHOVEL = tool("lapis_shovel", ExpansionToolMaterials.DECORATIVE, ToolKind.SHOVEL, 1.5f, -2.5f);
		LAPIS_PICKAXE = tool("lapis_pickaxe", ExpansionToolMaterials.DECORATIVE, ToolKind.PICKAXE, 1.0f, -2.0f);
		LAPIS_AXE = tool("lapis_axe", ExpansionToolMaterials.DECORATIVE, ToolKind.AXE, 5.0f, -2.5f);
		LAPIS_HOE = tool("lapis_hoe", ExpansionToolMaterials.DECORATIVE, ToolKind.HOE, -2.0f, 0.0f);
	}

	private static Item tool(String path, ToolMaterial material, ToolKind kind, float damage, float speed) {
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(WeaponExpansionMod.MOD_ID, path));
		Item.Settings settings = new Item.Settings().registryKey(key);
		Item item = switch (kind) {
			case SWORD -> new Item(settings.sword(material, damage, speed));
			case SHOVEL -> new Item(settings.shovel(material, damage, speed));
			case PICKAXE -> new Item(settings.pickaxe(material, damage, speed));
			case AXE -> new AxeItem(material, damage, speed, settings);
			case HOE -> new Item(settings.hoe(material, damage, speed));
		};
		return Registry.register(Registries.ITEM, key, item);
	}

	private enum ToolKind {
		SWORD, SHOVEL, PICKAXE, AXE, HOE
	}
}
