package com.example.swordguard.combat;

/**
 * Timed guard windows. Dual-sword doubles the perfect window length.
 */
public final class GuardWindows {
	public static final int PERFECT_TICKS = 5;
	public static final int DECAY_TICKS = 10;
	public static final int RIPOSTE_TICKS = 40;

	private GuardWindows() {
	}

	public static float mitigation(int heldTicks, boolean dualSword) {
		int perfect = dualSword ? PERFECT_TICKS * 2 : PERFECT_TICKS;
		if (heldTicks <= perfect) {
			return 1.0f;
		}
		int decayEnd = perfect + DECAY_TICKS;
		if (heldTicks <= decayEnd) {
			float t = (heldTicks - perfect) / (float) DECAY_TICKS;
			return 1.0f - t;
		}
		return 0.0f;
	}
}
