package com.example.forgecraft.survival.heat;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;

public class FurnaceIgniterSlot extends Slot {
	public FurnaceIgniterSlot(Inventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return FuelTags.isIgniter(stack);
	}

	@Override
	public int getMaxItemCount(ItemStack stack) {
		if (stack.isOf(Items.LAVA_BUCKET)) {
			return 1;
		}
		return super.getMaxItemCount(stack);
	}
}
