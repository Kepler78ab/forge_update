package com.example.forgecraft.survival.registry;

import com.example.forgecraft.survival.SurvivalMod;
import com.example.forgecraft.survival.forge.ForgeMetal;
import com.example.forgecraft.survival.forge.ForgePieceStage;
import com.example.forgecraft.survival.forge.ForgeService;
import com.example.forgecraft.survival.forge.TemplateFamily;
import com.example.forgecraft.survival.forge.WeaponProfileId;
import com.example.forgecraft.survival.smelt.AlloyIngotService;
import com.example.forgecraft.survival.smelt.MoltenMetalData;
import com.example.forgecraft.survival.smelt.MoltenMetalService;
import com.example.forgecraft.survival.smelt.SmeltMaterials;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

import java.util.Map;

public final class ModItemGroups {
	public static ItemGroup SURVIVAL;

	private ModItemGroups() {
	}

	public static void register() {
		SURVIVAL = Registry.register(
				Registries.ITEM_GROUP,
				SurvivalMod.id("survival"),
				FabricItemGroup.builder()
						.icon(() -> new ItemStack(ModItems.STEEL_INGOT))
						.displayName(Text.translatable("itemGroup.forge_craft.survival"))
						.entries((displayContext, entries) -> {
							entries.add(ModBlocks.SMELTING_BLAST_FURNACE);
							entries.add(ModBlocks.STONE_HOPPER);
							entries.add(ModBlocks.TEMPLATE_BENCH);
							entries.add(ModItems.STEEL_INGOT);
							entries.add(AlloyIngotService.create(MoltenMetalData.of("iron", 1)));
							entries.add(AlloyIngotService.create(MoltenMetalData.of(Map.of(
									"iron", 7,
									"carbon", 1
							))));
							for (String materialId : SmeltMaterials.all().keySet()) {
								entries.add(MoltenMetalService.createPure(materialId, 8));
							}
							entries.add(MoltenMetalService.createStack(MoltenMetalData.of(Map.of(
									"copper", 5,
									"iron", 3
							))));
							entries.add(MoltenMetalService.createStack(MoltenMetalData.of(Map.of(
									"iron", 7,
									"carbon", 1
							))));
							for (WeaponProfileId profile : WeaponProfileId.values()) {
								for (TemplateFamily family : TemplateFamily.values()) {
									entries.add(ForgeService.createTemplateStack(profile, family));
								}
							}
							for (WeaponProfileId profile : WeaponProfileId.values()) {
								entries.add(ForgeService.createPieceStack(ForgePieceStage.BURNING, profile, ForgeMetal.IRON));
							}
							for (ForgePieceStage stage : new ForgePieceStage[]{ForgePieceStage.COOLED, ForgePieceStage.PART}) {
								for (WeaponProfileId profile : WeaponProfileId.values()) {
									for (ForgeMetal metal : ForgeMetal.values()) {
										entries.add(ForgeService.createPieceStack(stage, profile, metal));
									}
								}
							}
							entries.add(ModItems.FORGED_SWORD);
							entries.add(ModItems.FORGED_AXE);
							entries.add(ModItems.FORGED_PICKAXE);
							entries.add(ModItems.FORGED_SHOVEL);
							entries.add(ModItems.FORGED_HOE);
							entries.add(Items.LEATHER_HELMET);
							entries.add(Items.LEATHER_CHESTPLATE);
							entries.add(Items.LEATHER_LEGGINGS);
							entries.add(Items.LEATHER_BOOTS);
							entries.add(Items.IRON_HELMET);
							entries.add(Items.IRON_CHESTPLATE);
							entries.add(Items.IRON_LEGGINGS);
							entries.add(Items.IRON_BOOTS);
							entries.add(Items.DIAMOND_HELMET);
							entries.add(Items.DIAMOND_CHESTPLATE);
							entries.add(Items.DIAMOND_LEGGINGS);
							entries.add(Items.DIAMOND_BOOTS);
						})
						.build()
		);
	}
}
