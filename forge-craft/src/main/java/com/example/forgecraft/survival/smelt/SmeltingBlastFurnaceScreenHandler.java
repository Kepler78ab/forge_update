package com.example.forgecraft.survival.smelt;

import com.example.forgecraft.survival.registry.ModScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class SmeltingBlastFurnaceScreenHandler extends ScreenHandler {
	private final Inventory inventory;
	private final PropertyDelegate propertyDelegate;

	public SmeltingBlastFurnaceScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, new SimpleInventory(SmeltingBlastFurnaceBlockEntity.SLOT_COUNT),
				new ArrayPropertyDelegate(SmeltingBlastFurnaceBlockEntity.PROP_COUNT));
	}

	public SmeltingBlastFurnaceScreenHandler(
			int syncId,
			PlayerInventory playerInventory,
			Inventory inventory,
			PropertyDelegate propertyDelegate
	) {
		super(ModScreenHandlers.SMELTING_BLAST_FURNACE, syncId);
		checkSize(inventory, SmeltingBlastFurnaceBlockEntity.SLOT_COUNT);
		checkDataCount(propertyDelegate, SmeltingBlastFurnaceBlockEntity.PROP_COUNT);
		this.inventory = inventory;
		this.propertyDelegate = propertyDelegate;
		inventory.onOpen(playerInventory.player);

		this.addSlot(new Slot(inventory, SmeltingBlastFurnaceBlockEntity.INPUT_SLOT, 56, 17) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return SmeltInputRegistry.isMeltable(stack);
			}
		});
		this.addSlot(new Slot(inventory, SmeltingBlastFurnaceBlockEntity.FUEL_SLOT, 56, 53) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.isOf(Items.LAVA_BUCKET);
			}
		});

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
			}
		}
		for (int col = 0; col < 9; col++) {
			this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
		}

		this.addProperties(propertyDelegate);
	}

	@Override
	public boolean onButtonClick(PlayerEntity player, int id) {
		if (id == 0 && this.inventory instanceof SmeltingBlastFurnaceBlockEntity furnace) {
			furnace.toggleOpen();
			return true;
		}
		return false;
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slotIndex) {
		ItemStack result = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotIndex);
		if (slot != null && slot.hasStack()) {
			ItemStack stack = slot.getStack();
			result = stack.copy();
			if (slotIndex < SmeltingBlastFurnaceBlockEntity.SLOT_COUNT) {
				if (!this.insertItem(stack, SmeltingBlastFurnaceBlockEntity.SLOT_COUNT, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (stack.isOf(Items.LAVA_BUCKET)) {
				if (!this.insertItem(stack, SmeltingBlastFurnaceBlockEntity.FUEL_SLOT, SmeltingBlastFurnaceBlockEntity.FUEL_SLOT + 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (SmeltInputRegistry.isMeltable(stack)) {
				if (!this.insertItem(stack, SmeltingBlastFurnaceBlockEntity.INPUT_SLOT, SmeltingBlastFurnaceBlockEntity.INPUT_SLOT + 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (slotIndex < SmeltingBlastFurnaceBlockEntity.SLOT_COUNT + 27) {
				if (!this.insertItem(stack, SmeltingBlastFurnaceBlockEntity.SLOT_COUNT + 27, this.slots.size(), false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(stack, SmeltingBlastFurnaceBlockEntity.SLOT_COUNT, SmeltingBlastFurnaceBlockEntity.SLOT_COUNT + 27, false)) {
				return ItemStack.EMPTY;
			}
			if (stack.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}
		}
		return result;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player);
	}

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		this.inventory.onClose(player);
	}

	public int getBurnTime() {
		return this.propertyDelegate.get(SmeltingBlastFurnaceBlockEntity.PROP_BURN_TIME);
	}

	public int getBurnTotal() {
		return Math.max(1, this.propertyDelegate.get(SmeltingBlastFurnaceBlockEntity.PROP_BURN_TOTAL));
	}

	public int getCookTime() {
		return this.propertyDelegate.get(SmeltingBlastFurnaceBlockEntity.PROP_COOK_TIME);
	}

	public int getCookTotal() {
		return Math.max(1, this.propertyDelegate.get(SmeltingBlastFurnaceBlockEntity.PROP_COOK_TOTAL));
	}

	public int getLiters() {
		return this.propertyDelegate.get(SmeltingBlastFurnaceBlockEntity.PROP_LITERS);
	}

	public int getMoltenColor() {
		return this.propertyDelegate.get(SmeltingBlastFurnaceBlockEntity.PROP_COLOR);
	}

	public boolean isLeakEnabled() {
		return this.propertyDelegate.get(SmeltingBlastFurnaceBlockEntity.PROP_OPEN) != 0;
	}

	public int getFuelProgress() {
		return this.getBurnTime() * 13 / this.getBurnTotal();
	}

	public int getCookProgress() {
		return this.getCookTime() * 24 / this.getCookTotal();
	}

	public int getMoltenBarHeight(int maxPixels) {
		return this.getLiters() * maxPixels / MoltenMetalData.MAX_LITERS;
	}
}
