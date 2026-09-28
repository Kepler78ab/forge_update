package com.example.forgecraft.survival.smelt;

import com.example.forgecraft.survival.registry.ModScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

/**
 * Stone hopper UI: player inventory + molten bar (no visible machine slots).
 */
public class StoneHopperScreenHandler extends ScreenHandler {
	private final Inventory inventory;
	private final PropertyDelegate propertyDelegate;

	public StoneHopperScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, new SimpleInventory(StoneHopperBlockEntity.SLOT_COUNT),
				new ArrayPropertyDelegate(StoneHopperBlockEntity.PROP_COUNT));
	}

	public StoneHopperScreenHandler(
			int syncId,
			PlayerInventory playerInventory,
			Inventory inventory,
			PropertyDelegate propertyDelegate
	) {
		super(ModScreenHandlers.STONE_HOPPER, syncId);
		checkSize(inventory, StoneHopperBlockEntity.SLOT_COUNT);
		checkDataCount(propertyDelegate, StoneHopperBlockEntity.PROP_COUNT);
		this.inventory = inventory;
		this.propertyDelegate = propertyDelegate;
		inventory.onOpen(playerInventory.player);

		// Hidden buffer slot (off-screen); hoppers insert here then BE absorbs into tank.
		this.addSlot(new Slot(inventory, StoneHopperBlockEntity.BUFFER_SLOT, -1000, -1000) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return false;
			}

			@Override
			public boolean isEnabled() {
				return false;
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
	public ItemStack quickMove(PlayerEntity player, int slotIndex) {
		return ItemStack.EMPTY;
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

	public int getLiters() {
		return this.propertyDelegate.get(StoneHopperBlockEntity.PROP_LITERS);
	}

	public int getMoltenColor() {
		return this.propertyDelegate.get(StoneHopperBlockEntity.PROP_COLOR);
	}

	public int getMoltenBarHeight(int maxPixels) {
		return this.getLiters() * maxPixels / MoltenMetalData.MAX_LITERS;
	}
}
