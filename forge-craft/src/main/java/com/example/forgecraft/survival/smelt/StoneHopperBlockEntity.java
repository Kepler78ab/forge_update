package com.example.forgecraft.survival.smelt;

import com.example.forgecraft.survival.registry.ModBlockEntities;
import com.example.forgecraft.survival.registry.ModComponents;
import com.example.forgecraft.survival.registry.ModItems;
import com.example.forgecraft.survival.registry.ModScreenHandlers;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Tank-only hopper: merges incoming molten into content; UI shows bar only.
 */
public class StoneHopperBlockEntity extends LockableContainerBlockEntity implements SidedInventory {
	public static final int BUFFER_SLOT = 0;
	public static final int SLOT_COUNT = 1;
	public static final int PUSH_INTERVAL = 20;

	public static final int PROP_LITERS = 0;
	public static final int PROP_COLOR = 1;
	public static final int PROP_COUNT = 2;

	private static final int[] SLOTS = new int[]{BUFFER_SLOT};

	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(SLOT_COUNT, ItemStack.EMPTY);
	@Nullable
	private MoltenMetalData content;
	private int pushCooldown;

	private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		@Override
		public int get(int index) {
			return switch (index) {
				case PROP_LITERS -> content == null ? 0 : content.liters();
				case PROP_COLOR -> content == null ? 0x888888 : content.evaluate().rgb();
				default -> 0;
			};
		}

		@Override
		public void set(int index, int value) {
		}

		@Override
		public int size() {
			return PROP_COUNT;
		}
	};

	public StoneHopperBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.STONE_HOPPER, pos, state);
	}

	public PropertyDelegate getPropertyDelegate() {
		return this.propertyDelegate;
	}

	public int getLiters() {
		return this.content == null ? 0 : this.content.liters();
	}

	@Nullable
	public MoltenMetalData getContent() {
		return this.content;
	}

	public ItemStack createMoltenDrop() {
		if (this.content == null) {
			return ItemStack.EMPTY;
		}
		return MoltenMetalService.createStack(this.content);
	}

	public static void tick(World world, BlockPos pos, BlockState state, StoneHopperBlockEntity be) {
		if (world.isClient()) {
			return;
		}
		be.absorbBuffer();
		if (!state.get(StoneHopperBlock.ENABLED) || be.content == null) {
			return;
		}
		if (be.pushCooldown > 0) {
			be.pushCooldown--;
			return;
		}
		be.pushCooldown = PUSH_INTERVAL;
		Direction facing = state.get(StoneHopperBlock.FACING);
		Inventory target = HopperBlockEntity.getInventoryAt(world, pos.offset(facing));
		if (target == null) {
			return;
		}
		MoltenMetalData before = be.content;
		MoltenMetalData drip = new MoltenMetalData(1, SmeltEngine.scaleToLiters(before.composition(), 1));
		ItemStack stack = MoltenMetalService.createStack(drip);
		ItemStack leftover = HopperBlockEntity.transfer(null, target, stack, facing.getOpposite());
		if (leftover.isEmpty()) {
			be.content = MoltenLeakHelper.subtractVisible(before, drip);
			markDirty(world, pos, state);
		}
	}

	/** Merge any molten sitting in the buffer slot into the tank. */
	private void absorbBuffer() {
		ItemStack buf = this.inventory.get(BUFFER_SLOT);
		if (buf.isEmpty() || !buf.isOf(ModItems.MOLTEN_METAL)) {
			return;
		}
		MoltenMetalData add = buf.get(ModComponents.MOLTEN_METAL);
		if (add == null) {
			this.inventory.set(BUFFER_SLOT, ItemStack.EMPTY);
			return;
		}
		if (this.content == null) {
			if (add.liters() > MoltenMetalData.MAX_LITERS) {
				return;
			}
			this.content = add;
			this.inventory.set(BUFFER_SLOT, ItemStack.EMPTY);
			this.markDirty();
			return;
		}
		MoltenMetalData merged = MoltenMetalService.tryMerge(this.content, add);
		if (merged != null) {
			this.content = merged;
			this.inventory.set(BUFFER_SLOT, ItemStack.EMPTY);
			this.markDirty();
		}
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		super.setStack(slot, stack);
		this.absorbBuffer();
	}

	@Override
	protected Text getContainerName() {
		return Text.translatable("container.forge_craft.stone_hopper");
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new StoneHopperScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
	}

	@Override
	public int size() {
		return SLOT_COUNT;
	}

	@Override
	protected DefaultedList<ItemStack> getHeldStacks() {
		return this.inventory;
	}

	@Override
	protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
		this.inventory = inventory;
	}

	@Override
	protected void readData(ReadView view) {
		super.readData(view);
		this.inventory = DefaultedList.ofSize(SLOT_COUNT, ItemStack.EMPTY);
		this.content = view.read("Molten", MoltenMetalData.CODEC).orElse(null);
	}

	@Override
	protected void writeData(WriteView view) {
		super.writeData(view);
		if (this.content != null) {
			view.put("Molten", MoltenMetalData.CODEC, this.content);
		}
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return SLOTS;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		if (!stack.isOf(ModItems.MOLTEN_METAL)) {
			return false;
		}
		MoltenMetalData add = stack.get(ModComponents.MOLTEN_METAL);
		if (add == null) {
			return false;
		}
		if (this.content == null) {
			return add.liters() <= MoltenMetalData.MAX_LITERS;
		}
		return this.content.liters() + add.liters() <= MoltenMetalData.MAX_LITERS;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return false;
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return canInsert(slot, stack, null);
	}
}
