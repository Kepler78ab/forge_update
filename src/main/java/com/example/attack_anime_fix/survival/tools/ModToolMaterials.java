package com.example.attack_anime_fix.survival.tools;

import com.example.attack_anime_fix.survival.SurvivalMod;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;

public final class ModToolMaterials {
	public static final TagKey<net.minecraft.item.Item> REPAIRS_STEEL_TOOLS =
			TagKey.of(RegistryKeys.ITEM, SurvivalMod.id("repairs_steel_tools"));

	/** Diamond niche: same mining tier / durability band as diamond. */
	public static final ToolMaterial STEEL = new ToolMaterial(
			BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
			1561,
			8.0f,
			3.0f,
			10,
			REPAIRS_STEEL_TOOLS
	);

	/** Decorative light tools: wood dig/damage, repaired by planks. */
	public static final ToolMaterial DECORATIVE = new ToolMaterial(
			BlockTags.INCORRECT_FOR_WOODEN_TOOL,
			59,
			2.0f,
			0.0f,
			15,
			ItemTags.PLANKS
	);

	private ModToolMaterials() {
	}
}
