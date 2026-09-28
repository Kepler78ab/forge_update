package com.example.forgecraft.survival.registry;

import com.example.forgecraft.survival.SurvivalMod;
import com.example.forgecraft.survival.forge.HaftWeaponRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModRecipes {
	public static RecipeSerializer<HaftWeaponRecipe> HAFT_WEAPON;

	private ModRecipes() {
	}

	public static void register() {
		HAFT_WEAPON = Registry.register(
				Registries.RECIPE_SERIALIZER,
				SurvivalMod.id("haft_weapon"),
				new SpecialCraftingRecipe.SpecialRecipeSerializer<>(HaftWeaponRecipe::new)
		);
	}
}
