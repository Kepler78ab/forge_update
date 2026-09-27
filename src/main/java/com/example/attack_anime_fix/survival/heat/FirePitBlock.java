package com.example.attack_anime_fix.survival.heat;

import com.example.attack_anime_fix.survival.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FirePitBlock extends AbstractFurnaceBlock {
	public static final MapCodec<FirePitBlock> CODEC = createCodec(FirePitBlock::new);

	public FirePitBlock(Settings settings) {
		super(settings);
	}

	@Override
	public MapCodec<? extends AbstractFurnaceBlock> getCodec() {
		return CODEC;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new FirePitBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return validateTicker(world, type, ModBlockEntities.FIRE_PIT);
	}

	@Override
	protected void openScreen(World world, BlockPos pos, PlayerEntity player) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof NamedScreenHandlerFactory factory) {
			player.openHandledScreen(factory);
		}
	}

	@Override
	protected ActionResult onUseWithItem(
			ItemStack stack,
			BlockState state,
			World world,
			BlockPos pos,
			PlayerEntity player,
			Hand hand,
			BlockHitResult hit
	) {
		if (stack.isOf(Items.TORCH) || stack.isOf(Items.SOUL_TORCH)) {
			if (!world.isClient()) {
				IgnitionService.tryIgniteWithTorch(world, pos, player);
			}
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!state.get(LIT)) {
			return;
		}
		double x = pos.getX() + 0.5;
		double y = pos.getY() + 0.2;
		double z = pos.getZ() + 0.5;
		if (random.nextDouble() < 0.1) {
			world.playSound(null, x, y, z, SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.4f, 1.0f);
		}
		world.addParticleClient(ParticleTypes.SMOKE, x, y + 0.3, z, 0.0, 0.02, 0.0);
		world.addParticleClient(ParticleTypes.FLAME, x, y + 0.25, z, 0.0, 0.01, 0.0);
	}
}
