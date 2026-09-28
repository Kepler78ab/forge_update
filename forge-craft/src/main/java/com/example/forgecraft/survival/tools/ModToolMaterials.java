package com.example.forgecraft.survival.tools;

import com.example.forgecraft.survival.SurvivalMod;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

public final class ModToolMaterials {
	public static final TagKey<net.minecraft.item.Item> REPAIRS_STEEL_TOOLS =
			TagKey.of(RegistryKeys.ITEM, SurvivalMod.id("repairs_steel_tools"));

	/** Diamond niche: same mining tier / durability band as diamond. Used by forged_* tools. */
	public static final ToolMaterial STEEL = new ToolMaterial(
			BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
			1561,
			8.0f,
			3.0f,
			10,
			REPAIRS_STEEL_TOOLS
	);

	private ModToolMaterials() {
	}
}
