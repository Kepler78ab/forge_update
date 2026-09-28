package com.example.toolsharpness;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Right-click with a dull tool to restore sharpness. */
public class WhetstoneBlock extends Block {
	public static final MapCodec<WhetstoneBlock> CODEC = createCodec(WhetstoneBlock::new);

	public WhetstoneBlock(Settings settings) {
		super(settings);
	}

	@Override
	public MapCodec<? extends Block> getCodec() {
		return CODEC;
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
		if (!SharpnessService.canSharpen(stack)) {
			return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
		}
		if (!world.isClient()) {
			SharpnessService.sharpen(stack);
			world.playSound(null, pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
		}
		return ActionResult.SUCCESS;
	}
}
