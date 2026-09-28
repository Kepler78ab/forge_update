package com.example.forgecraft.survival.smelt;

import com.example.forgecraft.survival.forge.ForgeService;
import com.example.forgecraft.survival.forge.ForgeTemplateData;
import com.example.forgecraft.survival.forge.WeaponProfileId;
import com.example.forgecraft.survival.registry.ModComponents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Template bench casting: no template → alloy ingot; template present → burning piece
 * (stick already in the template recipe — no extra stick).
 */
public final class CastService {
	public static final int ALLOY_INGOT_LITERS = 1;

	private CastService() {
	}

	public static int litersFor(WeaponProfileId profile) {
		return switch (profile) {
			case SWORD_BASIC -> 2;
			case SWORD_LONG -> 3;
			case AXE_BASIC, PICKAXE_BASIC -> 3;
			case SHOVEL_BASIC -> 1;
			case HOE_BASIC -> 2;
			case HELMET -> 5;
			case CHESTPLATE -> 8;
			case LEGGINGS -> 7;
			case BOOTS -> 4;
		};
	}

	/**
	 * @return cast product, or empty if not enough molten / invalid state
	 */
	public static ItemStack tryCast(TemplateBenchBlockEntity bench) {
		MoltenMetalData content = bench.getContent();
		if (content == null) {
			return ItemStack.EMPTY;
		}

		ItemStack template = bench.getTemplateStack();
		if (template.isEmpty()) {
			return castAlloy(bench, content);
		}

		ForgeTemplateData data = template.get(ModComponents.FORGE_TEMPLATE);
		if (data == null) {
			return ItemStack.EMPTY;
		}
		int cost = litersFor(data.profileId());
		MoltenMetalService.Split split = MoltenMetalService.take(content, cost);
		if (split == null) {
			return ItemStack.EMPTY;
		}
		bench.setContent(split.remaining());
		bench.consumeTemplate();
		World world = bench.getWorld();
		return ForgeService.createBurningCast(data.profileId(), split.taken(), world);
	}

	private static ItemStack castAlloy(TemplateBenchBlockEntity bench, MoltenMetalData content) {
		MoltenMetalService.Split split = MoltenMetalService.take(content, ALLOY_INGOT_LITERS);
		if (split == null) {
			return ItemStack.EMPTY;
		}
		bench.setContent(split.remaining());
		return AlloyIngotService.create(split.taken());
	}

	public static boolean castAndGive(TemplateBenchBlockEntity bench, net.minecraft.entity.player.PlayerEntity player) {
		ItemStack result = tryCast(bench);
		if (result.isEmpty()) {
			return false;
		}
		World world = bench.getWorld();
		BlockPos pos = bench.getPos();
		if (world != null && !world.isClient()) {
			world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.45f, 1.4f);
			if (world instanceof ServerWorld) {
				bench.markDirty();
				world.updateListeners(pos, bench.getCachedState(), bench.getCachedState(), 3);
			}
		}
		if (!player.getInventory().insertStack(result)) {
			player.dropItem(result, false);
		}
		return true;
	}

	@Nullable
	public static Integer requiredLiters(TemplateBenchBlockEntity bench) {
		ItemStack template = bench.getTemplateStack();
		if (template.isEmpty()) {
			return ALLOY_INGOT_LITERS;
		}
		ForgeTemplateData data = template.get(ModComponents.FORGE_TEMPLATE);
		if (data == null) {
			return null;
		}
		return litersFor(data.profileId());
	}
}
