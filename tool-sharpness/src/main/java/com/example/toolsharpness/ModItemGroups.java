package com.example.toolsharpness;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public final class ModItemGroups {
	public static ItemGroup MAIN;

	private ModItemGroups() {
	}

	public static void register() {
		MAIN = Registry.register(
				Registries.ITEM_GROUP,
				ToolSharpnessMod.id("main"),
				FabricItemGroup.builder()
						.icon(() -> new ItemStack(ModItems.WHETSTONE))
						.displayName(Text.translatable("itemGroup.tool_sharpness.main"))
						.entries((ctx, entries) -> entries.add(ModItems.WHETSTONE))
						.build()
		);
	}
}
