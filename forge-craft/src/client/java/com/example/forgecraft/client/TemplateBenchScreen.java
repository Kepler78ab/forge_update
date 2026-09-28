package com.example.forgecraft.client;

import com.example.forgecraft.survival.smelt.MoltenMetalData;
import com.example.forgecraft.survival.smelt.TemplateBenchScreenHandler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TemplateBenchScreen extends HandledScreen<TemplateBenchScreenHandler> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/hopper.png");

	public TemplateBenchScreen(TemplateBenchScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.backgroundHeight = 133;
		this.playerInventoryTitleY = this.backgroundHeight - 94;
	}

	@Override
	protected void init() {
		super.init();
		this.addDrawableChild(ButtonWidget.builder(
						Text.translatable("gui.forge_craft.template_bench.cast"),
						button -> {
							if (this.client != null && this.client.interactionManager != null) {
								this.client.interactionManager.clickButton(this.handler.syncId, 0);
							}
						})
				.dimensions(this.x + 116, this.y + 18, 48, 18)
				.build());
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int i = this.x;
		int j = this.y;
		context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
		context.fill(i + 43, j + 19, i + 133, j + 38, 0xFFC6C6C6);
		drawMoltenBar(context, i + 70, j + 20, 16, 16);
		Text frame = Text.translatable(
				this.handler.hasTemplate()
						? "gui.forge_craft.template_bench.has_template"
						: "gui.forge_craft.template_bench.no_template"
		);
		context.drawText(this.textRenderer, frame, i + 43, j + 40, 0x404040, false);
	}

	private void drawMoltenBar(DrawContext context, int x, int y, int w, int h) {
		context.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFF2A2A2A);
		context.fill(x, y, x + w, y + h, 0xFF101010);
		int fill = this.handler.getMoltenBarHeight(h);
		if (fill > 0) {
			int color = 0xFF000000 | (this.handler.getMoltenColor() & 0xFFFFFF);
			context.fill(x, y + h - fill, x + w, y + h, color);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(context, mouseX, mouseY);
		int bx = this.x + 70;
		int by = this.y + 20;
		if (mouseX >= bx && mouseX < bx + 16 && mouseY >= by && mouseY < by + 16) {
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
}
