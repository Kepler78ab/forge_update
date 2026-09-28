package com.example.forgecraft.client;

import com.example.forgecraft.survival.smelt.MoltenMetalData;
import com.example.forgecraft.survival.smelt.SmeltingBlastFurnaceScreenHandler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SmeltingBlastFurnaceScreen extends HandledScreen<SmeltingBlastFurnaceScreenHandler> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/blast_furnace.png");
	private static final Identifier LIT = Identifier.ofVanilla("container/blast_furnace/lit_progress");
	private static final Identifier BURN = Identifier.ofVanilla("container/blast_furnace/burn_progress");

	public SmeltingBlastFurnaceScreen(SmeltingBlastFurnaceScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void init() {
		super.init();
		this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
		this.addDrawableChild(ButtonWidget.builder(
						Text.translatable("gui.forge_craft.smelting_blast_furnace.toggle_leak"),
						button -> {
							if (this.client != null && this.client.interactionManager != null) {
								this.client.interactionManager.clickButton(this.handler.syncId, 0);
							}
						})
				.dimensions(this.x + 116, this.y + 60, 52, 16)
				.build());
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int i = this.x;
		int j = this.y;
		context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);

		if (this.handler.getBurnTime() > 0) {
			int lit = this.handler.getFuelProgress();
			context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, LIT, 14, 14, 0, 14 - lit, i + 56, j + 36 + 14 - lit, 14, lit);
		}
		int cook = this.handler.getCookProgress();
		context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BURN, 24, 16, 0, 0, i + 79, j + 34, cook, 16);

		drawMoltenBar(context, i + 134, j + 18, 16, 50);
	}

	private void drawMoltenBar(DrawContext context, int x, int y, int w, int h) {
		context.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFF2A2A2A);
		context.fill(x, y, x + w, y + h, 0xFF101010);
		int fill = this.handler.getMoltenBarHeight(h);
		if (fill > 0) {
			int rgb = this.handler.getMoltenColor() & 0xFFFFFF;
			int color = 0xFF000000 | rgb;
			context.fill(x, y + h - fill, x + w, y + h, color);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(context, mouseX, mouseY);
		int bx = this.x + 134;
		int by = this.y + 18;
		if (mouseX >= bx && mouseX < bx + 16 && mouseY >= by && mouseY < by + 50) {
			context.drawTooltip(
					this.textRenderer,
					Text.translatable(
							"tooltip.forge_craft.molten_liters",
							this.handler.getLiters(),
							MoltenMetalData.MAX_LITERS
					),
					mouseX,
					mouseY
			);
		}
	}

	@Override
	protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
		super.drawForeground(context, mouseX, mouseY);
		Text leak = Text.translatable(
				this.handler.isLeakEnabled()
						? "gui.forge_craft.smelting_blast_furnace.leak_on"
						: "gui.forge_craft.smelting_blast_furnace.leak_off"
		);
		context.drawText(this.textRenderer, leak, 116, 50, 0x404040, false);
	}
}
