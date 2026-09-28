package com.example.forgecraft.survival;

import com.example.forgecraft.ForgeCraftMod;
import com.example.forgecraft.survival.combat.ModCombat;
import com.example.forgecraft.survival.forge.ModForge;
import com.example.forgecraft.survival.heat.ModHeat;
import com.example.forgecraft.survival.registry.ModBlockEntities;
import com.example.forgecraft.survival.registry.ModBlocks;
import com.example.forgecraft.survival.registry.ModComponents;
import com.example.forgecraft.survival.registry.ModEntities;
import com.example.forgecraft.survival.registry.ModItemGroups;
import com.example.forgecraft.survival.registry.ModItems;
import com.example.forgecraft.survival.registry.ModRecipes;
import com.example.forgecraft.survival.registry.ModScreenHandlers;
import com.example.forgecraft.survival.smelt.ModSmelt;
import net.minecraft.util.Identifier;

/**
 * Survival Updated feature bootstrap (forge + smelt chain).
 */
public final class SurvivalMod {
	public static final String NAMESPACE = ForgeCraftMod.MOD_ID;

	private SurvivalMod() {
	}

	public static Identifier id(String path) {
		return Identifier.of(NAMESPACE, path);
	}

	public static void initialize() {
		ModComponents.register();
		ModEntities.register();
		ModBlocks.register();
		ModItems.register();
		ModRecipes.register();
		ModItemGroups.register();
		ModBlockEntities.register();
		ModScreenHandlers.register();
		ModHeat.register();
		ModForge.register();
		ModSmelt.register();
		ModCombat.register();
		ForgeCraftMod.LOGGER.info("Forge Craft (smelt + cast chain) registered");
	}
}
