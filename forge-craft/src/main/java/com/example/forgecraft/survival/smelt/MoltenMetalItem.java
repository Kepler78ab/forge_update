package com.example.forgecraft.survival.smelt;

import com.example.forgecraft.survival.registry.ModComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class MoltenMetalItem extends Item {
	public MoltenMetalItem(Settings settings) {
		super(settings);
	}

	@Override
	public Text getName(ItemStack stack) {
		MoltenMetalData data = stack.get(ModComponents.MOLTEN_METAL);
		if (data == null) {
			return super.getName(stack);
		}
		SmeltResult result = data.evaluate();
		return Text.translatable(
				"item.forge_craft.molten_metal.named",
				Text.translatable("tooltip.forge_craft.molten_output." + result.outputId()),
				data.liters()
		);
	}
}
