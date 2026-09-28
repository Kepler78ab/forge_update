package com.example.forgecraft.survival.registry;

import com.example.forgecraft.survival.SurvivalMod;
import com.example.forgecraft.survival.smelt.SmeltingBlastFurnaceBlockEntity;
import com.example.forgecraft.survival.smelt.StoneHopperBlockEntity;
import com.example.forgecraft.survival.smelt.TemplateBenchBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModBlockEntities {
	public static BlockEntityType<SmeltingBlastFurnaceBlockEntity> SMELTING_BLAST_FURNACE;
	public static BlockEntityType<StoneHopperBlockEntity> STONE_HOPPER;
	public static BlockEntityType<TemplateBenchBlockEntity> TEMPLATE_BENCH;

	private ModBlockEntities() {
	}

	public static void register() {
		SMELTING_BLAST_FURNACE = Registry.register(
				Registries.BLOCK_ENTITY_TYPE,
				SurvivalMod.id("smelting_blast_furnace"),
				FabricBlockEntityTypeBuilder.create(SmeltingBlastFurnaceBlockEntity::new, ModBlocks.SMELTING_BLAST_FURNACE).build()
		);
		STONE_HOPPER = Registry.register(
				Registries.BLOCK_ENTITY_TYPE,
				SurvivalMod.id("stone_hopper"),
				FabricBlockEntityTypeBuilder.create(StoneHopperBlockEntity::new, ModBlocks.STONE_HOPPER).build()
		);
		TEMPLATE_BENCH = Registry.register(
				Registries.BLOCK_ENTITY_TYPE,
				SurvivalMod.id("template_bench"),
				FabricBlockEntityTypeBuilder.create(TemplateBenchBlockEntity::new, ModBlocks.TEMPLATE_BENCH).build()
		);
	}
}
