package com.example.forgecraft.survival.smelt;

import com.example.forgecraft.survival.registry.ModComponents;
import com.example.forgecraft.survival.registry.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;

/**
 * Alloy ingot from template-less casting; remeltable only.
 */
public final class AlloyIngotService {
	private AlloyIngotService() {
	}

	public static ItemStack create(MoltenMetalData data) {
		ItemStack stack = new ItemStack(ModItems.ALLOY_INGOT);
		apply(stack, data);
		return stack;
	}

	public static void apply(ItemStack stack, MoltenMetalData data) {
		stack.set(ModComponents.MOLTEN_METAL, data);
		SmeltResult result = data.evaluate();
		stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(result.rgb()));
	}
}
