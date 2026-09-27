package com.example.attack_anime_fix.survival.heat;

import net.minecraft.item.ItemStack;

/**
 * Extra igniter stack on furnace / smoker block entities (mixin).
 */
public interface FurnaceIgnitionAccess {
	ItemStack survival_updated$getIgniter();

	void survival_updated$setIgniter(ItemStack stack);

	boolean survival_updated$hasValidIgniter();
}
