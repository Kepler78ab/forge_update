package com.example.weaponexpansion;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ExpansionItemGroups {
	public static ItemGroup WEAPON_EXPANSION;

	private ExpansionItemGroups() {
	}

	public static void register() {
		WEAPON_EXPANSION = Registry.register(
				Registries.ITEM_GROUP,
				Identifier.of(WeaponExpansionMod.MOD_ID, "main"),
				FabricItemGroup.builder()
						.icon(() -> new ItemStack(ExpansionItems.STEEL_SWORD))
						.displayName(Text.translatable("itemGroup.weapon_expansion.main"))
						.entries((ctx, entries) -> {
							entries.add(ExpansionItems.STEEL_SWORD);
							entries.add(ExpansionItems.STEEL_SHOVEL);
							entries.add(ExpansionItems.STEEL_PICKAXE);
							entries.add(ExpansionItems.STEEL_AXE);
							entries.add(ExpansionItems.STEEL_HOE);
							entries.add(ExpansionItems.OBSIDIAN_SWORD);
							entries.add(ExpansionItems.OBSIDIAN_SHOVEL);
							entries.add(ExpansionItems.OBSIDIAN_PICKAXE);
							entries.add(ExpansionItems.OBSIDIAN_AXE);
							entries.add(ExpansionItems.OBSIDIAN_HOE);
							entries.add(ExpansionItems.EMERALD_SWORD);
							entries.add(ExpansionItems.EMERALD_SHOVEL);
							entries.add(ExpansionItems.EMERALD_PICKAXE);
							entries.add(ExpansionItems.EMERALD_AXE);
							entries.add(ExpansionItems.EMERALD_HOE);
							entries.add(ExpansionItems.REDSTONE_SWORD);
							entries.add(ExpansionItems.REDSTONE_SHOVEL);
							entries.add(ExpansionItems.REDSTONE_PICKAXE);
							entries.add(ExpansionItems.REDSTONE_AXE);
							entries.add(ExpansionItems.REDSTONE_HOE);
							entries.add(ExpansionItems.LAPIS_SWORD);
							entries.add(ExpansionItems.LAPIS_SHOVEL);
							entries.add(ExpansionItems.LAPIS_PICKAXE);
							entries.add(ExpansionItems.LAPIS_AXE);
							entries.add(ExpansionItems.LAPIS_HOE);
						})
						.build()
		);
	}
}
