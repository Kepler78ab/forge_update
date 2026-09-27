package com.example.attack_anime_fix;

import com.example.attack_anime_fix.survival.SurvivalMod;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Attack_anime_fix implements ModInitializer {

	public static final String MOD_ID = "attack_anime_fix";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		SurvivalMod.initialize();
		LOGGER.info("Survival Updated initialized (MC 1.21.11 baseline)");
	}
}
