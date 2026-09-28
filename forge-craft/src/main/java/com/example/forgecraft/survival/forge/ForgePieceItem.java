package com.example.forgecraft.survival.forge;

import com.example.forgecraft.survival.registry.ModComponents;
import com.example.forgecraft.survival.smelt.MoltenMetalData;
import com.example.forgecraft.survival.smelt.SmeltResult;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Burning / cooled / part stages live on {@link ForgePieceData}.
 * F6 cast burning tools: usable with Fire Aspect; water cool → finished forged tool.
 * Legacy: cool → cooled; crafting-table → part; haft recipe still available.
 */
public class ForgePieceItem extends Item {
	public ForgePieceItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		PlayerEntity player = context.getPlayer();
		ItemStack stack = context.getStack();
		ForgePieceData data = stack.get(ModComponents.FORGE_PIECE);
		if (data == null || player == null) {
			return ActionResult.PASS;
		}

		BlockState state = world.getBlockState(context.getBlockPos());

		if (data.stage() == ForgePieceStage.BURNING) {
			boolean water = state.isOf(Blocks.WATER)
					|| (state.isOf(Blocks.WATER_CAULDRON) && state.get(LeveledCauldronBlock.LEVEL) > 0);
			if (!water) {
				return ActionResult.PASS;
			}
			if (!world.isClient()) {
				ItemStack cooled = ForgeService.coolBurning(stack);
				if (cooled == null) {
					return ActionResult.PASS;
				}
				Hand hand = context.getHand();
				if (cooled != stack) {
					player.setStackInHand(hand, cooled);
				}
				world.playSound(null, context.getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.6f, 1.2f);
				if (state.isOf(Blocks.WATER_CAULDRON)) {
					LeveledCauldronBlock.decrementFluidLevel(state, world, context.getBlockPos());
				}
			}
			return ActionResult.SUCCESS;
		}

		if (data.stage() == ForgePieceStage.COOLED && state.isOf(Blocks.CRAFTING_TABLE)) {
			if (!world.isClient()) {
				ForgeService.disassembleToPart(stack);
				world.playSound(null, context.getBlockPos(), SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 0.5f, 1.4f);
				player.sendMessage(Text.translatable("message.forge_craft.forge_disassemble"), true);
			}
			return ActionResult.SUCCESS;
		}

		return ActionResult.PASS;
	}

	@Override
	public Text getName(ItemStack stack) {
		ForgePieceData data = stack.get(ModComponents.FORGE_PIECE);
		if (data == null) {
			return super.getName(stack);
		}
		MoltenMetalData alloy = stack.get(ModComponents.MOLTEN_METAL);
		if (alloy != null) {
			SmeltResult result = alloy.evaluate();
			return Text.translatable(
					"item.forge_craft.forge_piece." + data.stage().asString() + ".cast",
					Text.translatable("tooltip.forge_craft.molten_output." + result.outputId()),
					Text.translatable("profile.forge_craft." + data.profileId().asString())
			);
		}
		return Text.translatable(
				"item.forge_craft.forge_piece." + data.stage().asString(),
				Text.translatable("metal.forge_craft." + data.metal().asString()),
				Text.translatable("profile.forge_craft." + data.profileId().asString())
		);
	}
}
