package com.example.toolsharpness;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public final class ModItems {
	public static Item WHETSTONE;

	private ModItems() {
	}

	public static void register() {
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, ToolSharpnessMod.id("whetstone"));
		WHETSTONE = Registry.register(
				Registries.ITEM,
				key,
				new BlockItem(ModBlocks.WHETSTONE, new Item.Settings().registryKey(key).useBlockPrefixedTranslationKey())
		);
	}
}
