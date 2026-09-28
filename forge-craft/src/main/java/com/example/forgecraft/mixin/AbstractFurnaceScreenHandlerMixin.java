package com.example.forgecraft.mixin;

import com.example.forgecraft.survival.heat.FurnaceIgniterSlot;
import com.example.forgecraft.survival.heat.IgniterInventory;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmokerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceScreenHandler.class)
public abstract class AbstractFurnaceScreenHandlerMixin extends AbstractRecipeScreenHandler {
	private static final int IGNITER_X = 26;
	private static final int IGNITER_Y = 53;

	protected AbstractFurnaceScreenHandlerMixin(ScreenHandlerType<?> type, int syncId) {
		super(type, syncId);
	}

	@Inject(
			method = "<init>(Lnet/minecraft/screen/ScreenHandlerType;Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/recipe/book/RecipeBookType;ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;)V",
			at = @At("RETURN")
	)
	private void survival_updated$addExtraSlots(
			ScreenHandlerType<?> type,
			RecipeType<?> recipeType,
			RegistryKey<RecipePropertySet> recipePropertySet,
			RecipeBookType category,
			int syncId,
			PlayerInventory playerInventory,
			Inventory inventory,
			PropertyDelegate propertyDelegate,
			CallbackInfo ci
	) {
		AbstractFurnaceScreenHandler self = (AbstractFurnaceScreenHandler) (Object) this;

		if (self instanceof FurnaceScreenHandler || self instanceof SmokerScreenHandler) {
			Inventory igniterInv = inventory instanceof AbstractFurnaceBlockEntity furnace
					? new IgniterInventory(furnace)
					: new SimpleInventory(1);
			this.addSlot(new FurnaceIgniterSlot(igniterInv, 0, IGNITER_X, IGNITER_Y));
		}
	}
}
