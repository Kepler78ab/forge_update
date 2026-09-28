package com.example.forgecraft.survival.smelt;

/**
 * Result of {@link SmeltEngine#smelt}.
 */
public record SmeltResult(int r, int g, int b, float hardness, String outputId) {
	public int rgb() {
		return (r << 16) | (g << 8) | b;
	}
}
