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

public class TemplateBenchScreenHandler extends ScreenHandler {
	private final Inventory inventory;
	private final PropertyDelegate propertyDelegate;

	public TemplateBenchScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, new SimpleInventory(TemplateBenchBlockEntity.SLOT_COUNT),
				new ArrayPropertyDelegate(TemplateBenchBlockEntity.PROP_COUNT));
	}

	public TemplateBenchScreenHandler(
			int syncId,
			PlayerInventory playerInventory,
			Inventory inventory,
			PropertyDelegate propertyDelegate
	) {
		super(ModScreenHandlers.TEMPLATE_BENCH, syncId);
		checkSize(inventory, TemplateBenchBlockEntity.SLOT_COUNT);
		checkDataCount(propertyDelegate, TemplateBenchBlockEntity.PROP_COUNT);
		this.inventory = inventory;
		this.propertyDelegate = propertyDelegate;
		inventory.onOpen(playerInventory.player);

		this.addSlot(new Slot(inventory, TemplateBenchBlockEntity.BUFFER_SLOT, -1000, -1000) {
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
	public boolean onButtonClick(PlayerEntity player, int id) {
		if (id == 0 && this.inventory instanceof TemplateBenchBlockEntity bench) {
			return CastService.castAndGive(bench, player);
		}
		return false;
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
		return this.propertyDelegate.get(TemplateBenchBlockEntity.PROP_LITERS);
	}

	public int getMoltenColor() {
		return this.propertyDelegate.get(TemplateBenchBlockEntity.PROP_COLOR);
	}

	public boolean hasTemplate() {
		return this.propertyDelegate.get(TemplateBenchBlockEntity.PROP_HAS_TEMPLATE) != 0;
	}

	public int getMoltenBarHeight(int maxPixels) {
		return this.getLiters() * maxPixels / MoltenMetalData.MAX_LITERS;
	}
}
