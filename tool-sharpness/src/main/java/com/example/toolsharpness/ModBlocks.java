package com.example.toolsharpness;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;

public final class ModBlocks {
	public static Block WHETSTONE;

	private ModBlocks() {
	}

	public static void register() {
		RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, ToolSharpnessMod.id("whetstone"));
		WHETSTONE = Registry.register(
				Registries.BLOCK,
				key,
				new WhetstoneBlock(
						AbstractBlock.Settings.copy(Blocks.GRINDSTONE)
								.mapColor(MapColor.STONE_GRAY)
								.strength(2.0f, 6.0f)
								.sounds(BlockSoundGroup.STONE)
								.registryKey(key)
				)
		);
	}
}
