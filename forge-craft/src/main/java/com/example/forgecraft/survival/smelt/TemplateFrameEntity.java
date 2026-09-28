package com.example.forgecraft.survival.smelt;

import com.example.forgecraft.survival.registry.ModComponents;
import com.example.forgecraft.survival.registry.ModEntities;
import com.example.forgecraft.survival.registry.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Item-frame style holder that only accepts forge templates; bound to a template bench.
 */
public class TemplateFrameEntity extends ItemFrameEntity {
	public TemplateFrameEntity(EntityType<? extends ItemFrameEntity> type, World world) {
		super(type, world);
	}

	public TemplateFrameEntity(World world, BlockPos pos, Direction facing) {
		super(ModEntities.TEMPLATE_FRAME, world, pos, facing);
	}

	@Override
	public boolean canStayAttached() {
		return this.getEntityWorld().getBlockState(this.getAttachedBlockPos()).getBlock() instanceof TemplateBenchBlock;
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		ItemStack held = player.getStackInHand(hand);
		boolean hasItem = !this.getHeldItemStack().isEmpty();
		boolean holding = !held.isEmpty();
		if (this.getEntityWorld().isClient()) {
			return (!hasItem && !holding) ? ActionResult.PASS : ActionResult.SUCCESS;
		}
		if (!hasItem) {
			if (holding && isTemplate(held)) {
				this.setHeldItemStack(held.copyWithCount(1));
				held.decrementUnlessCreative(1, player);
				syncToBench();
				return ActionResult.SUCCESS;
			}
			return ActionResult.PASS;
		}
		if (holding) {
			return ActionResult.FAIL;
		}
		ItemStack out = this.getHeldItemStack().copy();
		this.setHeldItemStack(ItemStack.EMPTY);
		if (!player.getInventory().insertStack(out)) {
			player.dropItem(out, false);
		}
		syncToBench();
		return ActionResult.SUCCESS;
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount) {
		if (this.isRemoved()) {
			return false;
		}
		if (!this.getHeldItemStack().isEmpty()) {
			this.dropStack(world, this.getHeldItemStack());
			this.setHeldItemStack(ItemStack.EMPTY);
			syncToBench();
			return true;
		}
		return false;
	}

	private void syncToBench() {
		World world = this.getEntityWorld();
		if (world.isClient()) {
			return;
		}
		BlockPos benchPos = this.getAttachedBlockPos();
		if (world.getBlockEntity(benchPos) instanceof TemplateBenchBlockEntity bench) {
			bench.setTemplateStack(this.getHeldItemStack().copy());
		}
	}

	public static boolean isTemplate(ItemStack stack) {
		return stack.isOf(ModItems.FORGE_TEMPLATE) && stack.contains(ModComponents.FORGE_TEMPLATE);
	}
}
