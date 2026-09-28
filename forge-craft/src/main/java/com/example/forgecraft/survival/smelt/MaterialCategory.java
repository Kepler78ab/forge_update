package com.example.forgecraft.survival.smelt;

/**
 * Material category for mix rules (impurity hardness penalty, etc.).
 */
public enum MaterialCategory {
	METAL,
	GEM,
	IMPURITY;

	public static MaterialCategory fromString(String raw) {
		return switch (raw.toLowerCase()) {
			case "gem", "宝石" -> GEM;
			case "impurity", "杂质" -> IMPURITY;
			default -> METAL;
		};
	}
}
