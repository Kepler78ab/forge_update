package com.example.swordguard.combat;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Attach sword block-attacks to vanilla swords and known expansion sword ids (soft, no mod soft-dep).
 */
public final class SwordGuardPatches {
	private static final List<Item> VANILLA_SWORDS = List.of(
			Items.WOODEN_SWORD, Items.STONE_SWORD, Items.COPPER_SWORD, Items.IRON_SWORD,
			Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD
	);

	private static final List<Identifier> EXTRA_SWORDS = List.of(
			Identifier.of("weapon_expansion", "steel_sword"),
			Identifier.of("weapon_expansion", "obsidian_sword"),
			Identifier.of("weapon_expansion", "emerald_sword"),
			Identifier.of("weapon_expansion", "redstone_sword"),
			Identifier.of("weapon_expansion", "lapis_sword"),
			Identifier.of("forge_craft", "forged_sword")
	);

	private SwordGuardPatches() {
	}

	public static void register() {
		DefaultItemComponentEvents.MODIFY.register(context -> {
			for (Item item : VANILLA_SWORDS) {
				context.modify(item, builder ->
						builder.add(DataComponentTypes.BLOCKS_ATTACKS, GuardBlocksAttacks.sword())
				);
			}
			for (Identifier id : EXTRA_SWORDS) {
				Item item = Registries.ITEM.get(id);
				if (item != null && item != Items.AIR) {
					context.modify(item, builder ->
							builder.add(DataComponentTypes.BLOCKS_ATTACKS, GuardBlocksAttacks.sword())
					);
				}
			}
		});
	}
}
