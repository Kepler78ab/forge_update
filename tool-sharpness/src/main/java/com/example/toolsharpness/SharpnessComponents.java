package com.example.toolsharpness;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class SharpnessComponents {
	public static ComponentType<SharpnessData> SHARPNESS;

	private SharpnessComponents() {
	}

	public static void register() {
		SHARPNESS = Registry.register(
				Registries.DATA_COMPONENT_TYPE,
				ToolSharpnessMod.id("sharpness"),
				ComponentType.<SharpnessData>builder().codec(SharpnessData.CODEC).packetCodec(SharpnessData.PACKET_CODEC).build()
		);
	}
}
