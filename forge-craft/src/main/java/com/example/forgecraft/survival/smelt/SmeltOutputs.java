package com.example.forgecraft.survival.smelt;

import java.util.List;

/**
 * Minimal output map from {@code 熔炼系统拓展.md} §五.
 */
public final class SmeltOutputs {
	private static final List<SmeltOutputMapping> MAPPINGS = List.of(
			new SmeltOutputMapping(0.0f, 2.0f, "brittle_slag"),
			new SmeltOutputMapping(2.0f, 4.0f, "soft_alloy"),
			new SmeltOutputMapping(4.0f, 6.0f, "hard_alloy"),
			new SmeltOutputMapping(6.0f, 99.0f, "ultra_alloy")
	);

	private SmeltOutputs() {
	}

	public static String map(float hardness) {
		for (SmeltOutputMapping mapping : MAPPINGS) {
			if (mapping.matches(hardness)) {
				return mapping.resultId();
			}
		}
		return "soft_alloy";
	}
}
