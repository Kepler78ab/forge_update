package com.example.forgecraft.survival.combat;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Forge-side combat: armor weight + two-hand profile clear.
 * Sword guard / dual-wield / bow rules live in the sword_guard mod (no dependency).
 */
public final class ModCombat {
	private ModCombat() {
	}

	public static void register() {
		CombatPatches.register();
		ServerTickEvents.END_WORLD_TICK.register(world -> {
			if (world.isClient()) {
				return;
			}
			for (ServerPlayerEntity player : world.getPlayers()) {
				ArmorWeightService.tick(player);
				TwoHandProfileService.tick(player);
			}
		});
	}
}
