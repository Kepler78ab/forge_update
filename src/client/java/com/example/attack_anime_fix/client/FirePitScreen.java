package com.example.attack_anime_fix.client;

import com.example.attack_anime_fix.survival.heat.FirePitScreenHandler;
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.recipebook.RecipeBookType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Reuses furnace GUI textures as a placeholder for the fire pit.
 */
public class FirePitScreen extends AbstractFurnaceScreen<FirePitScreenHandler> {
	private static final Identifier LIT_PROGRESS = Identifier.ofVanilla("container/furnace/lit_progress");
	private static final Identifier BURN_PROGRESS = Identifier.ofVanilla("container/furnace/burn_progress");
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/furnace.png");
	private static final Text TOGGLE = Text.translatable("gui.recipebook.toggleRecipes.smeltable");
	private static final List<RecipeBookWidget.Tab> TABS = List.of(
			new RecipeBookWidget.Tab(RecipeBookType.FURNACE),
			new RecipeBookWidget.Tab(Items.PORKCHOP, RecipeBookCategories.FURNACE_FOOD),
			new RecipeBookWidget.Tab(Items.OAK_LOG, RecipeBookCategories.FURNACE_BLOCKS),
			new RecipeBookWidget.Tab(Items.STICK, Items.CHARCOAL, RecipeBookCategories.FURNACE_MISC)
	);

	public FirePitScreen(FirePitScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title, TOGGLE, TEXTURE, LIT_PROGRESS, BURN_PROGRESS, TABS);
	}
}
