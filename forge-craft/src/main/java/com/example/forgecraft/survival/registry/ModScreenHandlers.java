package com.example.forgecraft.survival.registry;

import com.example.forgecraft.survival.SurvivalMod;
import com.example.forgecraft.survival.smelt.SmeltingBlastFurnaceScreenHandler;
import com.example.forgecraft.survival.smelt.StoneHopperScreenHandler;
import com.example.forgecraft.survival.smelt.TemplateBenchScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;

public final class ModScreenHandlers {
	public static ScreenHandlerType<SmeltingBlastFurnaceScreenHandler> SMELTING_BLAST_FURNACE;
	public static ScreenHandlerType<StoneHopperScreenHandler> STONE_HOPPER;
	public static ScreenHandlerType<TemplateBenchScreenHandler> TEMPLATE_BENCH;

	private ModScreenHandlers() {
	}

	public static void register() {
		SMELTING_BLAST_FURNACE = Registry.register(
				Registries.SCREEN_HANDLER,
				SurvivalMod.id("smelting_blast_furnace"),
				new ScreenHandlerType<>(SmeltingBlastFurnaceScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
		);
		STONE_HOPPER = Registry.register(
				Registries.SCREEN_HANDLER,
				SurvivalMod.id("stone_hopper"),
				new ScreenHandlerType<>(StoneHopperScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
		);
		TEMPLATE_BENCH = Registry.register(
				Registries.SCREEN_HANDLER,
				SurvivalMod.id("template_bench"),
				new ScreenHandlerType<>(TemplateBenchScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
		);
	}
}
