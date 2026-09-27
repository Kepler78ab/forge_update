package com.example.attack_anime_fix.mixin;

import com.example.attack_anime_fix.survival.heat.FirePitBlockEntity;
import com.example.attack_anime_fix.survival.heat.FuelTags;
import com.example.attack_anime_fix.survival.heat.FurnaceIgnitionAccess;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin implements FurnaceIgnitionAccess {
	@Unique
	private ItemStack survival_updated$igniter = ItemStack.EMPTY;

	@Override
	public ItemStack survival_updated$getIgniter() {
		return this.survival_updated$igniter;
	}

	@Override
	public void survival_updated$setIgniter(ItemStack stack) {
		this.survival_updated$igniter = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
	}

	@Override
	public boolean survival_updated$hasValidIgniter() {
		return FuelTags.isIgniter(this.survival_updated$igniter);
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private static void survival_updated$gateHeat(
			ServerWorld world,
			BlockPos pos,
			BlockState state,
			AbstractFurnaceBlockEntity blockEntity,
			CallbackInfo ci
	) {
		boolean lit = state.contains(AbstractFurnaceBlock.LIT) && state.get(AbstractFurnaceBlock.LIT);

		if (blockEntity instanceof FirePitBlockEntity) {
			if (!lit) {
				ci.cancel();
			}
			return;
		}

		if (blockEntity instanceof BlastFurnaceBlockEntity) {
			ItemStack fuel = blockEntity.getStack(1);
			// Blast furnace: only lava can start a burn cycle.
			if (!lit && !FuelTags.isLavaFuel(fuel)) {
				ci.cancel();
			}
			return;
		}

		// Furnace / smoker: need torch or lava bucket in igniter slot to start.
		FurnaceIgnitionAccess access = (FurnaceIgnitionAccess) blockEntity;
		if (!lit && !access.survival_updated$hasValidIgniter()) {
			ci.cancel();
		}
	}

	@Inject(method = "readData", at = @At("TAIL"))
	private void survival_updated$readIgniter(ReadView view, CallbackInfo ci) {
		this.survival_updated$igniter = view.read("SurvivalUpdatedIgniter", ItemStack.CODEC).orElse(ItemStack.EMPTY);
	}

	@Inject(method = "writeData", at = @At("TAIL"))
	private void survival_updated$writeIgniter(WriteView view, CallbackInfo ci) {
		if (!this.survival_updated$igniter.isEmpty()) {
			view.put("SurvivalUpdatedIgniter", ItemStack.CODEC, this.survival_updated$igniter);
		}
	}

	@Inject(method = "onBlockReplaced", at = @At("HEAD"))
	private void survival_updated$dropIgniter(BlockPos pos, BlockState oldState, CallbackInfo ci) {
		AbstractFurnaceBlockEntity self = (AbstractFurnaceBlockEntity) (Object) this;
		World world = self.getWorld();
		if (world == null || world.isClient() || this.survival_updated$igniter.isEmpty()) {
			return;
		}
		ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), this.survival_updated$igniter);
		this.survival_updated$igniter = ItemStack.EMPTY;
	}
}
