package com.example.forgecraft;

import com.example.forgecraft.survival.SurvivalMod;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForgeCraftMod implements ModInitializer {
	public static final String MOD_ID = "forge_craft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		SurvivalMod.initialize();
		LOGGER.info("Forge Craft initialized");
	}
}
