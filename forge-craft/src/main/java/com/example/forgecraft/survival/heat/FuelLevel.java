package com.example.forgecraft.survival.heat;

/**
 * Fuel power tiers for furnaces / fire pit / blast furnace.
 */
public enum FuelLevel {
	NONE,
	LOW,
	MID,
	HIGH;

	public boolean atLeast(FuelLevel other) {
		return this.ordinal() >= other.ordinal();
	}
}
