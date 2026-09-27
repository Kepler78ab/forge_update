package com.example.attack_anime_fix.survival.heat;

import com.example.attack_anime_fix.survival.registry.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

/**
 * Fire-pit / campfire lighting helpers. Furnaces use the igniter inventory slot instead.
 */
public final class IgnitionService {
	private IgnitionService() {
	}

	public static boolean tryIgniteWithTorch(World world, BlockPos pos, PlayerEntity player) {
		if (world.isClient()) {
			return false;
		}
		BlockState state = world.getBlockState(pos);
		if (state.isOf(ModBlocks.FIRE_PIT) && !state.get(FirePitBlock.LIT)) {
			world.setBlockState(pos, state.with(FirePitBlock.LIT, true), 3);
			playIgnite(world, pos);
			return true;
		}
		if ((state.isOf(Blocks.CAMPFIRE) || state.isOf(Blocks.SOUL_CAMPFIRE)) && !state.get(CampfireBlock.LIT)) {
			world.setBlockState(pos, state.with(CampfireBlock.LIT, true), 3);
			playIgnite(world, pos);
			return true;
		}
		return false;
	}

	private static void playIgnite(World world, BlockPos pos) {
		world.playSound(
				null,
				pos,
				SoundEvents.ITEM_FLINTANDSTEEL_USE,
				SoundCategory.BLOCKS,
				1.0f,
				world.getRandom().nextFloat() * 0.4f + 0.8f
		);
		world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, pos);
	}
}
