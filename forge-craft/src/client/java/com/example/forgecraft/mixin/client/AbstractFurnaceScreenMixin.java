package com.example.forgecraft.mixin.client;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.ingame.SmokerScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceScreen.class)
public abstract class AbstractFurnaceScreenMixin extends RecipeBookScreen<AbstractFurnaceScreenHandler> {
	private static final Identifier SLOT = Identifier.ofVanilla("container/slot");
	private static final int IGNITER_X = 26;
	private static final int IGNITER_Y = 53;

	protected AbstractFurnaceScreenMixin(
			AbstractFurnaceScreenHandler handler,
			RecipeBookWidget<?> recipeBook,
			PlayerInventory inventory,
			Text title
	) {
		super(handler, recipeBook, inventory, title);
	}

	@Inject(method = "drawBackground", at = @At("TAIL"))
	private void survival_updated$drawExtraSlots(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
		AbstractFurnaceScreen<?> self = (AbstractFurnaceScreen<?>) (Object) this;
		if (self instanceof FurnaceScreen || self instanceof SmokerScreen) {
			context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT, this.x + IGNITER_X - 1, this.y + IGNITER_Y - 1, 18, 18);
		}
	}
}
