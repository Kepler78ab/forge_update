package com.example.attack_anime_fix.survival.tools;

import com.example.attack_anime_fix.survival.registry.ModComponents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Optional;

/**
 * Sharpness → damage / axe mining multipliers, whetstone restore, enchant fold-in.
 */
public final class SharpnessService {
	/** Copper+ factory sharpness is low; below this ratio applies a heavy penalty. */
	private static final float DULL_THRESHOLD = 0.35f;
	private static final float DULL_FACTOR = 0.55f;
	private static final float SHARP_BONUS = 0.25f;
	private static final float ENCHANT_PER_LEVEL = 0.08f;

	private SharpnessService() {
	}

	public static boolean hasSharpness(ItemStack stack) {
		return stack.contains(ModComponents.SHARPNESS);
	}

	public static boolean canSharpen(ItemStack stack) {
		return hasSharpness(stack) && !isFullySharp(stack);
	}

	public static boolean isFullySharp(ItemStack stack) {
		SharpnessData data = stack.get(ModComponents.SHARPNESS);
		return data != null && data.sharpness() >= data.maxSharpness() - 0.001f;
	}

	public static boolean sharpen(ItemStack stack) {
		SharpnessData data = stack.get(ModComponents.SHARPNESS);
		if (data == null) {
			return false;
		}
		stack.set(ModComponents.SHARPNESS, data.withSharpness(data.maxSharpness()));
		return true;
	}

	public static float getDamageFactor(ItemStack stack, PlayerEntity player) {
		float base = componentFactor(stack);
		return base + enchantBonus(stack, player);
	}

	public static float getMiningFactor(ItemStack stack, PlayerEntity player) {
		if (!stack.isIn(ItemTags.AXES)) {
			return 1.0f;
		}
		return getDamageFactor(stack, player);
	}

	public static void decayOnHit(ItemStack stack) {
		SharpnessData data = stack.get(ModComponents.SHARPNESS);
		if (data == null || data.decayPerDamage() <= 0.0f) {
			return;
		}
		stack.set(ModComponents.SHARPNESS, data.withSharpness(data.sharpness() - data.decayPerDamage()));
	}

	public static void appendTooltip(ItemStack stack, List<Text> lines) {
		SharpnessData data = stack.get(ModComponents.SHARPNESS);
		if (data == null) {
			return;
		}
		Formatting color = data.ratio() < DULL_THRESHOLD ? Formatting.RED
				: data.ratio() < 0.7f ? Formatting.YELLOW : Formatting.GREEN;
		lines.add(Text.translatable(
				"item.attack_anime_fix.sharpness",
				String.format("%.0f", data.sharpness()),
				String.format("%.0f", data.maxSharpness())
		).formatted(color));
	}

	private static float componentFactor(ItemStack stack) {
		SharpnessData data = stack.get(ModComponents.SHARPNESS);
		if (data == null) {
			return 1.0f;
		}
		float ratio = data.ratio();
		if (ratio < DULL_THRESHOLD) {
			return DULL_FACTOR + (ratio / DULL_THRESHOLD) * (1.0f - DULL_FACTOR) * 0.5f;
		}
		return 1.0f + (ratio - DULL_THRESHOLD) / (1.0f - DULL_THRESHOLD) * SHARP_BONUS;
	}

	private static float enchantBonus(ItemStack stack, PlayerEntity player) {
		if (player == null || player.getEntityWorld().isClient()) {
			return 0.0f;
		}
		Optional<RegistryEntry.Reference<Enchantment>> entry = player.getEntityWorld()
				.getRegistryManager()
				.getOrThrow(RegistryKeys.ENCHANTMENT)
				.getEntry(Enchantments.SHARPNESS.getValue());
		if (entry.isEmpty()) {
			return 0.0f;
		}
		int level = EnchantmentHelper.getLevel(entry.get(), stack);
		return level * ENCHANT_PER_LEVEL;
	}
}
