package com.example.forgecraft.survival.smelt;

import com.example.forgecraft.survival.registry.ModBlockEntities;
import com.example.forgecraft.survival.registry.ModScreenHandlers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SmeltingBlastFurnaceBlockEntity extends LockableContainerBlockEntity implements SidedInventory {
	public static final int INPUT_SLOT = 0;
	public static final int FUEL_SLOT = 1;
	public static final int SLOT_COUNT = 2;

	public static final int LAVA_BURN_TIME = 20000;
	public static final int COOK_TIME_TOTAL = 200;
	public static final int LEAK_INTERVAL = 20;

	public static final int PROP_BURN_TIME = 0;
	public static final int PROP_BURN_TOTAL = 1;
	public static final int PROP_COOK_TIME = 2;
	public static final int PROP_COOK_TOTAL = 3;
	public static final int PROP_LITERS = 4;
	public static final int PROP_COLOR = 5;
	public static final int PROP_OPEN = 6;
	public static final int PROP_COUNT = 7;

	private static final int[] TOP_SLOTS = new int[]{INPUT_SLOT};
	private static final int[] SIDE_SLOTS = new int[]{FUEL_SLOT};
	private static final int[] BOTTOM_SLOTS = new int[]{};

	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(SLOT_COUNT, ItemStack.EMPTY);
	@Nullable
	private MoltenMetalData content;
	private int burnTime;
	private int burnTotal;
	private int cookTime;
	private int leakCooldown;

	private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		@Override
		public int get(int index) {
			return switch (index) {
				case PROP_BURN_TIME -> burnTime;
				case PROP_BURN_TOTAL -> burnTotal;
				case PROP_COOK_TIME -> cookTime;
				case PROP_COOK_TOTAL -> COOK_TIME_TOTAL;
				case PROP_LITERS -> content == null ? 0 : content.liters();
				case PROP_COLOR -> content == null ? 0x888888 : content.evaluate().rgb();
				case PROP_OPEN -> {
					World w = getWorld();
					if (w == null) {
						yield 0;
					}
					yield SmeltingBlastFurnaceBlock.shouldLeak(w, pos, getCachedState()) ? 1 : 0;
				}
				default -> 0;
			};
		}

		@Override
		public void set(int index, int value) {
			switch (index) {
				case PROP_BURN_TIME -> burnTime = value;
				case PROP_BURN_TOTAL -> burnTotal = value;
				case PROP_COOK_TIME -> cookTime = value;
				default -> {
				}
			}
		}

		@Override
		public int size() {
			return PROP_COUNT;
		}
	};

	public SmeltingBlastFurnaceBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.SMELTING_BLAST_FURNACE, pos, state);
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

	public void toggleOpen() {
		World world = this.getWorld();
		if (world == null || world.isClient()) {
			return;
		}
		BlockState state = this.getCachedState();
		world.setBlockState(this.pos, state.cycle(SmeltingBlastFurnaceBlock.OPEN), Block.NOTIFY_ALL);
	}

	public static void tick(World world, BlockPos pos, BlockState state, SmeltingBlastFurnaceBlockEntity be) {
		if (!(world instanceof ServerWorld)) {
			return;
		}
		boolean dirty = false;
		boolean wasLit = be.burnTime > 0;

		if (be.burnTime > 0) {
			be.burnTime--;
			dirty = true;
		}

		ItemStack fuel = be.inventory.get(FUEL_SLOT);
		ItemStack input = be.inventory.get(INPUT_SLOT);

		if (be.burnTime <= 0 && isLavaFuel(fuel) && canAcceptMelt(be, input)) {
			be.burnTime = LAVA_BURN_TIME;
			be.burnTotal = LAVA_BURN_TIME;
			fuel.decrement(1);
			if (fuel.isEmpty()) {
				be.inventory.set(FUEL_SLOT, new ItemStack(Items.BUCKET));
			}
			dirty = true;
		}

		boolean lit = be.burnTime > 0;
		if (lit && canAcceptMelt(be, input)) {
			be.cookTime++;
			if (be.cookTime >= COOK_TIME_TOTAL) {
				be.cookTime = 0;
				be.meltOne(input);
				dirty = true;
			} else {
				dirty = true;
			}
		} else if (be.cookTime > 0) {
			be.cookTime = Math.max(0, be.cookTime - 2);
			dirty = true;
		}

		if (SmeltingBlastFurnaceBlock.shouldLeak(world, pos, state) && be.content != null) {
			if (be.leakCooldown > 0) {
				be.leakCooldown--;
			} else {
				MoltenMetalData before = be.content;
				be.content = MoltenLeakHelper.tryLeakOneLiter(world, pos, be.content);
				be.leakCooldown = LEAK_INTERVAL;
				if (before != be.content) {
					dirty = true;
				}
			}
		}

		if (wasLit != lit) {
			world.setBlockState(pos, state.with(SmeltingBlastFurnaceBlock.LIT, lit), Block.NOTIFY_ALL);
			dirty = true;
		}

		if (dirty) {
			markDirty(world, pos, state);
		}
	}

	private static boolean isLavaFuel(ItemStack stack) {
		return stack.isOf(Items.LAVA_BUCKET);
	}

	private static boolean canAcceptMelt(SmeltingBlastFurnaceBlockEntity be, ItemStack input) {
		var melt = SmeltInputRegistry.resolve(input);
		if (melt.isEmpty()) {
			return false;
		}
		MoltenMetalData add = melt.get();
		if (be.content == null) {
			return add.liters() <= MoltenMetalData.MAX_LITERS;
		}
		return be.content.liters() + add.liters() <= MoltenMetalData.MAX_LITERS;
	}

	private void meltOne(ItemStack input) {
		var melt = SmeltInputRegistry.resolve(input);
		if (melt.isEmpty()) {
			return;
		}
		MoltenMetalData add = melt.get();
		if (this.content == null) {
			this.content = add;
		} else {
			MoltenMetalData merged = MoltenMetalService.tryMerge(this.content, add);
			if (merged == null) {
				return;
			}
			this.content = merged;
		}
		input.decrement(1);
	}

	@Override
	protected Text getContainerName() {
		return Text.translatable("container.forge_craft.smelting_blast_furnace");
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new SmeltingBlastFurnaceScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
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
		Inventories.readData(view, this.inventory);
		this.burnTime = view.getInt("BurnTime", 0);
		this.burnTotal = view.getInt("BurnTotal", 0);
		this.cookTime = view.getInt("CookTime", 0);
		this.content = view.read("Molten", MoltenMetalData.CODEC).orElse(null);
	}

	@Override
	protected void writeData(WriteView view) {
		super.writeData(view);
		Inventories.writeData(view, this.inventory);
		view.putInt("BurnTime", this.burnTime);
		view.putInt("BurnTotal", this.burnTotal);
		view.putInt("CookTime", this.cookTime);
		if (this.content != null) {
			view.put("Molten", MoltenMetalData.CODEC, this.content);
		}
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		if (side == Direction.DOWN) {
			return BOTTOM_SLOTS;
		}
		if (side == Direction.UP) {
			return TOP_SLOTS;
		}
		return SIDE_SLOTS;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		if (slot == FUEL_SLOT) {
			return isLavaFuel(stack);
		}
		if (slot == INPUT_SLOT) {
			return SmeltInputRegistry.isMeltable(stack);
		}
		return false;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return slot == FUEL_SLOT && stack.isOf(Items.BUCKET);
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return canInsert(slot, stack, null);
	}
}
