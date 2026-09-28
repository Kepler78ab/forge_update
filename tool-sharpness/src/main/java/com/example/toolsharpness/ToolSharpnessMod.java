package com.example.toolsharpness;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ToolSharpnessMod implements ModInitializer {
	public static final String MOD_ID = "tool_sharpness";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		SharpnessComponents.register();
		ModBlocks.register();
		ModItems.register();
		ModItemGroups.register();
		ToolPatches.register();
		LOGGER.info("Tool Sharpness loaded (component + whetstone + factory dull)");
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
