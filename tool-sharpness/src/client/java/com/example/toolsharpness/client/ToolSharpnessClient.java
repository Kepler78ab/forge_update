package com.example.toolsharpness.client;

import com.example.toolsharpness.SharpnessComponents;
import com.example.toolsharpness.SharpnessService;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

public final class ToolSharpnessClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
			if (stack.contains(SharpnessComponents.SHARPNESS)) {
				SharpnessService.appendTooltip(stack, lines);
			}
		});
	}
}
