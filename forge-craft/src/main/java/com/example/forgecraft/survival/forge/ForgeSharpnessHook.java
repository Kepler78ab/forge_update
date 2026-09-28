package com.example.forgecraft.survival.forge;

import net.minecraft.item.ItemStack;

/**
 * Soft bridge to {@code tool_sharpness} via reflection (no compile dependency).
 */
public final class ForgeSharpnessHook {
	private ForgeSharpnessHook() {
	}

	public static void applyDull(ItemStack stack, float maxSharpness) {
		try {
			Class<?> api = Class.forName("com.example.toolsharpness.SharpnessApi");
			api.getMethod("applyDull", ItemStack.class, float.class).invoke(null, stack, maxSharpness);
		} catch (ReflectiveOperationException ignored) {
			// Pack absent or API mismatch — skip.
		}
	}
}
