package com.example.forgecraft.survival.smelt;

/**
 * Hardness-band → logical output id ({@code display_color: auto}).
 */
public record SmeltOutputMapping(float hardnessMin, float hardnessMax, String resultId) {
	public boolean matches(float hardness) {
		return hardness >= hardnessMin && hardness < hardnessMax;
	}
}
