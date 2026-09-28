package com.example.forgecraft.survival.smelt;

import com.example.forgecraft.survival.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Melting blast furnace: lava fuel, molten tank ≤64L, OPEN∨redstone leaks downward.
 * Appearance reuses vanilla blast furnace models.
 */
public class SmeltingBlastFurnaceBlock extends BlockWithEntity {
	public static final MapCodec<SmeltingBlastFurnaceBlock> CODEC = createCodec(SmeltingBlastFurnaceBlock::new);
	public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty LIT = Properties.LIT;
	public static final BooleanProperty OPEN = Properties.OPEN;

	public SmeltingBlastFurnaceBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState()
				.with(FACING, Direction.NORTH)
				.with(LIT, false)
				.with(OPEN, false));
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return CODEC;
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (player.isSneaking()) {
			if (!world.isClient()) {
				world.setBlockState(pos, state.cycle(OPEN), Block.NOTIFY_ALL);
			}
			return ActionResult.SUCCESS;
		}
		if (!world.isClient()) {
			NamedScreenHandlerFactory factory = state.createScreenHandlerFactory(world, pos);
			if (factory != null) {
				player.openHandledScreen(factory);
			}
		}
		return ActionResult.SUCCESS;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}

	@Override
	protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof SmeltingBlastFurnaceBlockEntity furnace) {
			ItemStack molten = furnace.createMoltenDrop();
			if (!molten.isEmpty()) {
				ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), molten);
			}
		}
		ItemScatterer.onStateReplaced(state, world, pos);
	}

	@Override
	protected boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof SmeltingBlastFurnaceBlockEntity furnace) {
			int liters = furnace.getLiters();
			if (liters <= 0) {
				return 0;
			}
			return 1 + (liters - 1) * 14 / MoltenMetalData.MAX_LITERS;
		}
		return 0;
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, LIT, OPEN);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new SmeltingBlastFurnaceBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient()
				? null
				: validateTicker(type, ModBlockEntities.SMELTING_BLAST_FURNACE, SmeltingBlastFurnaceBlockEntity::tick);
	}

	/** OPEN property or redstone power enables downward leak. */
	public static boolean shouldLeak(World world, BlockPos pos, BlockState state) {
		return state.get(OPEN) || world.isReceivingRedstonePower(pos);
	}

	public static Settings settings() {
		return Settings.copy(Blocks.BLAST_FURNACE).luminance(state -> state.get(LIT) ? 13 : 0);
	}
}
