package com.example.weaponexpansion;

import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

/**
 * Expansion tool tiers. F7 forge compat: steel ×3/4, obsidian ×4/5 vs diamond baseline (1561).
 */
public final class ExpansionToolMaterials {
	/** Diamond baseline before forge-compat nerf. */
	public static final int DIAMOND_BASE_DURABILITY = 1561;

	public static final TagKey<Item> REPAIRS_STEEL_TOOLS =
			TagKey.of(RegistryKeys.ITEM, Identifier.of(WeaponExpansionMod.MOD_ID, "repairs_steel_tools"));
	public static final TagKey<Item> REPAIRS_OBSIDIAN_TOOLS =
			TagKey.of(RegistryKeys.ITEM, Identifier.of(WeaponExpansionMod.MOD_ID, "repairs_obsidian_tools"));

	/** Diamond-tier mining; durability ×3/4 (forge compat). Repaired by iron block tag. */
	public static final ToolMaterial STEEL = new ToolMaterial(
			BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
			DIAMOND_BASE_DURABILITY * 3 / 4,
			8.0f,
			3.0f,
			10,
			REPAIRS_STEEL_TOOLS
	);

	/** Diamond-tier mining; durability ×4/5 (forge compat). Repaired by obsidian. */
	public static final ToolMaterial OBSIDIAN = new ToolMaterial(
			BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
			DIAMOND_BASE_DURABILITY * 4 / 5,
			8.0f,
			3.5f,
			12,
			REPAIRS_OBSIDIAN_TOOLS
	);

	/** Decorative light tools. */
	public static final ToolMaterial DECORATIVE = new ToolMaterial(
			BlockTags.INCORRECT_FOR_WOODEN_TOOL,
			59,
			2.0f,
			0.0f,
			15,
			ItemTags.PLANKS
	);

	private ExpansionToolMaterials() {
	}
}
