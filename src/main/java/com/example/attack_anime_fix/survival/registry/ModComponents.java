package com.example.attack_anime_fix.survival.registry;

import com.example.attack_anime_fix.survival.SurvivalMod;
import com.example.attack_anime_fix.survival.tools.SharpnessData;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModComponents {
	public static ComponentType<SharpnessData> SHARPNESS;

	private ModComponents() {
	}

	public static void register() {
		SHARPNESS = Registry.register(
				Registries.DATA_COMPONENT_TYPE,
				SurvivalMod.id("sharpness"),
				ComponentType.<SharpnessData>builder()
						.codec(SharpnessData.CODEC)
						.packetCodec(SharpnessData.PACKET_CODEC)
						.build()
		);
	}
}
