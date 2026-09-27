package com.example.attack_anime_fix.survival.heat;

import com.example.attack_anime_fix.survival.registry.ModScreenHandlers;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;

public class FirePitScreenHandler extends AbstractFurnaceScreenHandler {
	public FirePitScreenHandler(int syncId, PlayerInventory playerInventory) {
		super(
				ModScreenHandlers.FIRE_PIT,
				RecipeType.SMELTING,
				RecipePropertySet.FURNACE_INPUT,
				RecipeBookType.FURNACE,
				syncId,
				playerInventory
		);
	}

	public FirePitScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(
				ModScreenHandlers.FIRE_PIT,
				RecipeType.SMELTING,
				RecipePropertySet.FURNACE_INPUT,
				RecipeBookType.FURNACE,
				syncId,
				playerInventory,
				inventory,
				propertyDelegate
		);
	}

	@Override
	protected boolean isFuel(ItemStack stack) {
		return stack.isIn(FuelTags.FIRE_PIT_FUEL) || FuelTags.levelOf(stack) == FuelLevel.LOW;
	}

	@Override
	protected boolean isSmeltable(ItemStack stack) {
		return stack.isIn(FuelTags.FIRE_PIT_INPUT) || stack.contains(DataComponentTypes.FOOD);
	}
}
