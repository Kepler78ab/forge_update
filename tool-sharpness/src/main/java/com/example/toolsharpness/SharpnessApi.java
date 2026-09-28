package com.example.toolsharpness;

import net.minecraft.item.ItemStack;

/**
 * Public hook for other mods (e.g. forge cast tools) when this pack is installed.
 */
public final class SharpnessApi {
	private SharpnessApi() {
	}

	public static void applyDull(ItemStack stack, float maxSharpness) {
		stack.set(SharpnessComponents.SHARPNESS, SharpnessData.dull(maxSharpness));
	}

	public static void applyFull(ItemStack stack, float maxSharpness) {
		stack.set(SharpnessComponents.SHARPNESS, SharpnessData.full(maxSharpness));
	}

	public static boolean hasSharpness(ItemStack stack) {
		return stack.contains(SharpnessComponents.SHARPNESS);
	}
}
