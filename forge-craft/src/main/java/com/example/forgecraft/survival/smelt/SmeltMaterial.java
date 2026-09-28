package com.example.forgecraft.survival.smelt;

/**
 * Pure data material definition. Builtin defaults + datapack {@code smelt_material/*.json}.
 */
public record SmeltMaterial(
		String id,
		int r,
		int g,
		int b,
		float hardness,
		float weight,
		MaterialCategory category,
		String special
) {
	public SmeltMaterial {
		if (id == null || id.isBlank()) {
			throw new IllegalArgumentException("material id required");
		}
		r = clampByte(r);
		g = clampByte(g);
		b = clampByte(b);
		if (hardness < 0.0f) {
			throw new IllegalArgumentException("hardness >= 0");
		}
		if (weight <= 0.0f) {
			throw new IllegalArgumentException("weight > 0");
		}
		if (category == null) {
			category = MaterialCategory.METAL;
		}
	}

	public int rgb() {
		return (r << 16) | (g << 8) | b;
	}

	private static int clampByte(int v) {
		return Math.max(0, Math.min(255, v));
	}
}
