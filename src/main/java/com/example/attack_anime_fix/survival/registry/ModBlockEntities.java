package com.example.attack_anime_fix.survival.registry;

import com.example.attack_anime_fix.survival.SurvivalMod;
import com.example.attack_anime_fix.survival.heat.FirePitBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModBlockEntities {
	public static BlockEntityType<FirePitBlockEntity> FIRE_PIT;

	private ModBlockEntities() {
	}

	public static void register() {
		FIRE_PIT = Registry.register(
				Registries.BLOCK_ENTITY_TYPE,
				SurvivalMod.id("fire_pit"),
				FabricBlockEntityTypeBuilder.create(FirePitBlockEntity::new, ModBlocks.FIRE_PIT).build()
		);
	}
}
