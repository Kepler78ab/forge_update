package com.example.attack_anime_fix.client;

import com.example.attack_anime_fix.Attack_anime_fix;
import com.example.attack_anime_fix.survival.registry.ModComponents;
import com.example.attack_anime_fix.survival.registry.ModScreenHandlers;
import com.example.attack_anime_fix.survival.tools.SharpnessService;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class Attack_anime_fixClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		HandledScreens.register(ModScreenHandlers.FIRE_PIT, FirePitScreen::new);
		ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
			if (stack.contains(ModComponents.SHARPNESS)) {
				SharpnessService.appendTooltip(stack, lines);
			}
		});
		Attack_anime_fix.LOGGER.info("Survival Updated client ready");
	}
}
