package com.example.swordguard;

import com.example.swordguard.combat.HandednessService;
import com.example.swordguard.combat.SwordGuardPatches;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SwordGuardMod implements ModInitializer {
	public static final String MOD_ID = "sword_guard";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		SwordGuardPatches.register();
		ServerTickEvents.END_WORLD_TICK.register(world -> {
			if (world.isClient()) {
				return;
			}
			for (ServerPlayerEntity player : world.getPlayers()) {
				HandednessService.tick(player);
			}
		});
		LOGGER.info("Sword Guard loaded");
	}
}
