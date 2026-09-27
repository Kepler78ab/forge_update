package com.example.attack_anime_fix.survival.heat;

import com.example.attack_anime_fix.survival.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FirePitBlockEntity extends AbstractFurnaceBlockEntity {
	public FirePitBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.FIRE_PIT, pos, state, RecipeType.SMELTING);
	}

	@Override
	protected Text getContainerName() {
		return Text.translatable("container.attack_anime_fix.fire_pit");
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new FirePitScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		if (slot == FUEL_SLOT_INDEX) {
			return stack.isIn(FuelTags.FIRE_PIT_FUEL) || FuelTags.levelOf(stack) == FuelLevel.LOW;
		}
		if (slot == INPUT_SLOT_INDEX) {
			return stack.isIn(FuelTags.FIRE_PIT_INPUT) || stack.contains(DataComponentTypes.FOOD);
		}
		return super.isValid(slot, stack);
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		return isValid(slot, stack);
	}
}
