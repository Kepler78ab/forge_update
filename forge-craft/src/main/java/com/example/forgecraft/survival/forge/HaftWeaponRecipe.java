package com.example.forgecraft.survival.forge;

import com.example.forgecraft.survival.registry.ModComponents;
import com.example.forgecraft.survival.registry.ModItems;
import com.example.forgecraft.survival.registry.ModRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

/**
 * Shapeless special recipe: forge PART + stick → hafted forged weapon.
 */
public class HaftWeaponRecipe extends SpecialCraftingRecipe {
	public HaftWeaponRecipe(CraftingRecipeCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingRecipeInput input, World world) {
		return findPart(input) != null && countSticks(input) == 1 && input.getStackCount() == 2;
	}

	@Override
	public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
		ItemStack part = findPart(input);
		if (part == null) {
			return ItemStack.EMPTY;
		}
		return ForgeService.haftWeapon(part).orElse(ItemStack.EMPTY);
	}

	@Override
	public RecipeSerializer<? extends SpecialCraftingRecipe> getSerializer() {
		return ModRecipes.HAFT_WEAPON;
	}

	private static ItemStack findPart(CraftingRecipeInput input) {
		ItemStack found = null;
		for (ItemStack stack : input.getStacks()) {
			if (stack.isEmpty()) {
				continue;
			}
			if (!stack.isOf(ModItems.FORGE_PIECE)) {
				continue;
			}
			ForgePieceData data = stack.get(ModComponents.FORGE_PIECE);
			if (data == null || data.stage() != ForgePieceStage.PART) {
				return null;
			}
			if (found != null) {
				return null;
			}
			found = stack;
		}
		return found;
	}

	private static int countSticks(CraftingRecipeInput input) {
		int sticks = 0;
		for (ItemStack stack : input.getStacks()) {
			if (stack.isOf(Items.STICK)) {
				sticks += stack.getCount() > 0 ? 1 : 0;
			}
		}
		return sticks;
	}
}
