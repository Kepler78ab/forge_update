package com.example.attack_anime_fix.survival.registry;

import com.example.attack_anime_fix.survival.SurvivalMod;
import com.example.attack_anime_fix.survival.heat.FirePitScreenHandler;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;

public final class ModScreenHandlers {
	public static ScreenHandlerType<FirePitScreenHandler> FIRE_PIT;

	private ModScreenHandlers() {
	}

	public static void register() {
		FIRE_PIT = Registry.register(
				Registries.SCREEN_HANDLER,
				SurvivalMod.id("fire_pit"),
				new ScreenHandlerType<>(FirePitScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
		);
	}
}
