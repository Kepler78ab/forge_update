package com.example.forgecraft.client;

import com.example.forgecraft.ForgeCraftMod;
import com.example.forgecraft.survival.registry.ModComponents;
import com.example.forgecraft.survival.registry.ModEntities;
import com.example.forgecraft.survival.registry.ModScreenHandlers;
import com.example.forgecraft.survival.smelt.MoltenMetalService;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.ItemFrameEntityRenderer;

public class ForgeCraftClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		HandledScreens.register(ModScreenHandlers.SMELTING_BLAST_FURNACE, SmeltingBlastFurnaceScreen::new);
		HandledScreens.register(ModScreenHandlers.STONE_HOPPER, StoneHopperScreen::new);
		HandledScreens.register(ModScreenHandlers.TEMPLATE_BENCH, TemplateBenchScreen::new);
		EntityRendererRegistry.register(ModEntities.TEMPLATE_FRAME, ItemFrameEntityRenderer::new);
		ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
			if (stack.contains(ModComponents.MOLTEN_METAL)) {
				MoltenMetalService.appendTooltip(stack, lines);
			}
		});
		ForgeCraftMod.LOGGER.info("Survival Updated client ready");
	}
}
