package com.example.attack_anime_fix.survival;

import com.example.attack_anime_fix.Attack_anime_fix;
import com.example.attack_anime_fix.survival.heat.ModHeat;
import com.example.attack_anime_fix.survival.registry.ModBlockEntities;
import com.example.attack_anime_fix.survival.registry.ModBlocks;
import com.example.attack_anime_fix.survival.registry.ModComponents;
import com.example.attack_anime_fix.survival.registry.ModItems;
import com.example.attack_anime_fix.survival.registry.ModScreenHandlers;
import com.example.attack_anime_fix.survival.tools.ModTools;
import net.minecraft.util.Identifier;

/**
 * Survival Updated feature bootstrap (phases 1–2).
 */
public final class SurvivalMod {
	public static final String NAMESPACE = Attack_anime_fix.MOD_ID;

	private SurvivalMod() {
	}

	public static Identifier id(String path) {
		return Identifier.of(NAMESPACE, path);
	}

	public static void initialize() {
		ModComponents.register();
		ModBlocks.register();
		ModItems.register();
		ModBlockEntities.register();
		ModScreenHandlers.register();
		ModHeat.register();
		ModTools.register();
		Attack_anime_fix.LOGGER.info("Survival Updated phases 1–2 (Heat + Tools/Sharpness) registered");
	}
}
