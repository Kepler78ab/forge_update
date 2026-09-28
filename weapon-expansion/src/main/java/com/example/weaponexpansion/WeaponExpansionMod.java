package com.example.weaponexpansion;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WeaponExpansionMod implements ModInitializer {
	public static final String MOD_ID = "weapon_expansion";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ExpansionItems.register();
		ExpansionItemGroups.register();
		LOGGER.info("Weapon Expansion loaded (tools)");
	}
}
