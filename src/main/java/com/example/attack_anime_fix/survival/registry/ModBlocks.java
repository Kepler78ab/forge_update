package com.example.attack_anime_fix.survival.registry;

import com.example.attack_anime_fix.survival.SurvivalMod;
import com.example.attack_anime_fix.survival.heat.FirePitBlock;
import com.example.attack_anime_fix.survival.tools.WhetstoneBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;

public final class ModBlocks {
	public static Block FIRE_PIT;
	public static Block WHETSTONE;

	private ModBlocks() {
	}

	public static void register() {
		RegistryKey<Block> firePitKey = RegistryKey.of(RegistryKeys.BLOCK, SurvivalMod.id("fire_pit"));
		FIRE_PIT = Registry.register(
				Registries.BLOCK,
				firePitKey,
				new FirePitBlock(
						AbstractBlock.Settings.copy(Blocks.CAMPFIRE)
								.mapColor(MapColor.SPRUCE_BROWN)
								.instrument(NoteBlockInstrument.BASS)
								.strength(2.0f)
								.sounds(BlockSoundGroup.WOOD)
								.luminance(state -> state.get(FirePitBlock.LIT) ? 12 : 0)
								.nonOpaque()
								.registryKey(firePitKey)
				)
		);

		RegistryKey<Block> whetstoneKey = RegistryKey.of(RegistryKeys.BLOCK, SurvivalMod.id("whetstone"));
		WHETSTONE = Registry.register(
				Registries.BLOCK,
				whetstoneKey,
				new WhetstoneBlock(
						AbstractBlock.Settings.copy(Blocks.GRINDSTONE)
								.mapColor(MapColor.STONE_GRAY)
								.strength(2.0f, 6.0f)
								.sounds(BlockSoundGroup.STONE)
								.registryKey(whetstoneKey)
				)
		);
	}
}
