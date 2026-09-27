package com.example.attack_anime_fix.survival.heat;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

/**
 * Single-slot inventory backed by {@link FurnaceIgnitionAccess} on a furnace BE.
 */
public final class IgniterInventory implements Inventory {
	private final AbstractFurnaceBlockEntity furnace;

	public IgniterInventory(AbstractFurnaceBlockEntity furnace) {
		this.furnace = furnace;
	}

	private FurnaceIgnitionAccess access() {
		return (FurnaceIgnitionAccess) this.furnace;
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return access().survival_updated$getIgniter().isEmpty();
	}

	@Override
	public ItemStack getStack(int slot) {
		return access().survival_updated$getIgniter();
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack current = access().survival_updated$getIgniter();
		if (current.isEmpty() || amount <= 0) {
			return ItemStack.EMPTY;
		}
		ItemStack split = current.split(amount);
		access().survival_updated$setIgniter(current);
		markDirty();
		return split;
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack current = access().survival_updated$getIgniter();
		access().survival_updated$setIgniter(ItemStack.EMPTY);
		markDirty();
		return current;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		access().survival_updated$setIgniter(stack);
		markDirty();
	}

	@Override
	public void markDirty() {
		this.furnace.markDirty();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return Inventory.canPlayerUse(this.furnace, player);
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return FuelTags.isIgniter(stack);
	}

	@Override
	public void clear() {
		access().survival_updated$setIgniter(ItemStack.EMPTY);
		markDirty();
	}
}
