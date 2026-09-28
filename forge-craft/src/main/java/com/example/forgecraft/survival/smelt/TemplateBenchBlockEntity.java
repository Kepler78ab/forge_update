package com.example.forgecraft.survival.smelt;

import com.example.forgecraft.survival.registry.ModBlockEntities;
import com.example.forgecraft.survival.registry.ModComponents;
import com.example.forgecraft.survival.registry.ModItems;
import com.example.forgecraft.survival.registry.ModScreenHandlers;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Receives molten from stone hopper; holds template (synced with {@link TemplateFrameEntity}).
 * Casting: no template → alloy ingot; with template → burning piece (stick already in template).
 */
public class TemplateBenchBlockEntity extends LockableContainerBlockEntity implements SidedInventory {
	public static final int BUFFER_SLOT = 0;
	public static final int SLOT_COUNT = 1;

	public static final int PROP_LITERS = 0;
	public static final int PROP_COLOR = 1;
	public static final int PROP_HAS_TEMPLATE = 2;
	public static final int PROP_COUNT = 3;

	private static final int[] SLOTS = new int[]{BUFFER_SLOT};

	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(SLOT_COUNT, ItemStack.EMPTY);
	@Nullable
	private MoltenMetalData content;
	private ItemStack templateStack = ItemStack.EMPTY;

	private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		@Override
		public int get(int index) {
			return switch (index) {
				case PROP_LITERS -> content == null ? 0 : content.liters();
				case PROP_COLOR -> content == null ? 0x888888 : content.evaluate().rgb();
				case PROP_HAS_TEMPLATE -> templateStack.isEmpty() ? 0 : 1;
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

	public TemplateBenchBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.TEMPLATE_BENCH, pos, state);
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

	public void setContent(@Nullable MoltenMetalData content) {
		this.content = content;
		this.markDirty();
	}

	public ItemStack getTemplateStack() {
		return this.templateStack;
	}

	public void setTemplateStack(ItemStack stack) {
		this.templateStack = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
		this.markDirty();
	}

	/** Clears template on BE and attached frame (consumed by cast). */
	public void consumeTemplate() {
		this.templateStack = ItemStack.EMPTY;
		World world = this.getWorld();
		if (world instanceof ServerWorld serverWorld) {
			List<TemplateFrameEntity> frames = serverWorld.getEntitiesByClass(
					TemplateFrameEntity.class,
					new Box(this.pos).expand(0.6),
					e -> this.pos.equals(e.getAttachedBlockPos())
			);
			for (TemplateFrameEntity frame : frames) {
				frame.setHeldItemStack(ItemStack.EMPTY);
			}
		}
		this.markDirty();
	}

	public ItemStack createMoltenDrop() {
		if (this.content == null) {
			return ItemStack.EMPTY;
		}
		return MoltenMetalService.createStack(this.content);
	}

	public static void tick(World world, BlockPos pos, BlockState state, TemplateBenchBlockEntity be) {
		if (world.isClient()) {
			return;
		}
		be.absorbBuffer();
	}

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
		return Text.translatable("container.forge_craft.template_bench");
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new TemplateBenchScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
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
		this.templateStack = view.read("Template", ItemStack.CODEC).orElse(ItemStack.EMPTY);
	}

	@Override
	protected void writeData(WriteView view) {
		super.writeData(view);
		if (this.content != null) {
			view.put("Molten", MoltenMetalData.CODEC, this.content);
		}
		if (!this.templateStack.isEmpty()) {
			view.put("Template", ItemStack.CODEC, this.templateStack);
		}
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return side == Direction.UP ? SLOTS : new int[0];
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
