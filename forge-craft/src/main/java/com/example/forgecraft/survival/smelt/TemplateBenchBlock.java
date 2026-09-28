package com.example.forgecraft.survival.smelt;

import com.example.forgecraft.survival.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * End-portal-frame look template bench: receives molten from above; hosts a template frame entity.
 */
public class TemplateBenchBlock extends BlockWithEntity {
	public static final MapCodec<TemplateBenchBlock> CODEC = createCodec(TemplateBenchBlock::new);
	public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
	private static final VoxelShape SHAPE = VoxelShapes.union(
			Block.createCuboidShape(0, 0, 0, 16, 13, 16),
			Block.createCuboidShape(1, 13, 1, 15, 15, 15)
	);

	public TemplateBenchBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return CODEC;
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
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
	protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (!oldState.isOf(state.getBlock()) && !world.isClient()) {
			spawnFrame((ServerWorld) world, pos);
		}
	}

	@Override
	protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
		removeFrames(world, pos);
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof TemplateBenchBlockEntity bench) {
			ItemStack molten = bench.createMoltenDrop();
			if (!molten.isEmpty()) {
				ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), molten);
			}
			ItemStack template = bench.getTemplateStack();
			if (!template.isEmpty()) {
				ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), template);
			}
		}
		ItemScatterer.onStateReplaced(state, world, pos);
	}

	static void spawnFrame(ServerWorld world, BlockPos pos) {
		removeFrames(world, pos);
		TemplateFrameEntity frame = new TemplateFrameEntity(world, pos, Direction.UP);
		world.spawnEntity(frame);
	}

	static void removeFrames(ServerWorld world, BlockPos pos) {
		List<TemplateFrameEntity> frames = world.getEntitiesByClass(
				TemplateFrameEntity.class,
				new Box(pos).expand(0.6),
				e -> pos.equals(e.getAttachedBlockPos())
		);
		for (TemplateFrameEntity frame : frames) {
			frame.discard();
		}
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
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
		builder.add(FACING);
	}

	@Override
	protected boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof TemplateBenchBlockEntity bench) {
			int liters = bench.getLiters();
			if (liters <= 0) {
				return 0;
			}
			return 1 + (liters - 1) * 14 / MoltenMetalData.MAX_LITERS;
		}
		return 0;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new TemplateBenchBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient()
				? null
				: validateTicker(type, ModBlockEntities.TEMPLATE_BENCH, TemplateBenchBlockEntity::tick);
	}

	public static Settings settings() {
		return Settings.copy(Blocks.END_PORTAL_FRAME)
				.mapColor(MapColor.GREEN)
				.sounds(BlockSoundGroup.STONE)
				.strength(3.0f, 9.0f)
				.nonOpaque();
	}
}
