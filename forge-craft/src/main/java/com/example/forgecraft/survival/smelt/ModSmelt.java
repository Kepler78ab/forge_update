package com.example.forgecraft.survival.smelt;

/**
 * F2+ smelt bootstrap.
 */
public final class ModSmelt {
	private ModSmelt() {
	}

	public static void register() {
		SmeltMaterials.registerReloadListener();
		ColorOverrideTable.registerReloadListener();
		SmeltInputRegistry.registerReloadListener();
	}
}
