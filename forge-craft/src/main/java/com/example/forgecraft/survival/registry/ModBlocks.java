package com.example.forgecraft.survival.registry;

import com.example.forgecraft.survival.SurvivalMod;
import com.example.forgecraft.survival.smelt.SmeltingBlastFurnaceBlock;
import com.example.forgecraft.survival.smelt.StoneHopperBlock;
import com.example.forgecraft.survival.smelt.TemplateBenchBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public final class ModBlocks {
	public static Block SMELTING_BLAST_FURNACE;
	public static Block STONE_HOPPER;
	public static Block TEMPLATE_BENCH;

	private ModBlocks() {
	}

	public static void register() {
		RegistryKey<Block> furnaceKey = RegistryKey.of(RegistryKeys.BLOCK, SurvivalMod.id("smelting_blast_furnace"));
		SMELTING_BLAST_FURNACE = Registry.register(
				Registries.BLOCK,
				furnaceKey,
				new SmeltingBlastFurnaceBlock(SmeltingBlastFurnaceBlock.settings().registryKey(furnaceKey))
		);

		RegistryKey<Block> hopperKey = RegistryKey.of(RegistryKeys.BLOCK, SurvivalMod.id("stone_hopper"));
		STONE_HOPPER = Registry.register(
				Registries.BLOCK,
				hopperKey,
				new StoneHopperBlock(StoneHopperBlock.settings().registryKey(hopperKey))
		);

		RegistryKey<Block> benchKey = RegistryKey.of(RegistryKeys.BLOCK, SurvivalMod.id("template_bench"));
		TEMPLATE_BENCH = Registry.register(
				Registries.BLOCK,
				benchKey,
				new TemplateBenchBlock(TemplateBenchBlock.settings().registryKey(benchKey))
		);
	}
}
