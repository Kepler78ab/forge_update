package com.example.forgecraft.survival.smelt;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Push 1 升 molten into the inventory below (hoppers / F4 stone hopper).
 */
public final class MoltenLeakHelper {
	private MoltenLeakHelper() {
	}

	public static MoltenMetalData tryLeakOneLiter(World world, BlockPos furnacePos, MoltenMetalData content) {
		if (content == null || content.liters() < 1) {
			return content;
		}
		Inventory below = HopperBlockEntity.getInventoryAt(world, furnacePos.down());
		if (below == null) {
			return content;
		}

		MoltenMetalData drip = new MoltenMetalData(1, SmeltEngine.scaleToLiters(content.composition(), 1));
		ItemStack stack = MoltenMetalService.createStack(drip);
		ItemStack leftover = HopperBlockEntity.transfer(null, below, stack, Direction.UP);
		if (!leftover.isEmpty()) {
			return content;
		}
		return subtractOneLiter(content, drip);
	}

	static MoltenMetalData subtractVisible(MoltenMetalData content, MoltenMetalData drip) {
		return subtractOneLiter(content, drip);
	}

	private static MoltenMetalData subtractOneLiter(MoltenMetalData content, MoltenMetalData drip) {
		int remainLiters = content.liters() - 1;
		if (remainLiters <= 0) {
			return null;
		}
		Map<String, Integer> remain = new LinkedHashMap<>();
		for (Map.Entry<String, Integer> e : content.composition().entrySet()) {
			int left = e.getValue() - drip.composition().getOrDefault(e.getKey(), 0);
			if (left > 0) {
				remain.put(e.getKey(), left);
			}
		}
		if (remain.isEmpty()) {
			remain = SmeltEngine.scaleToLiters(content.composition(), remainLiters);
		} else {
			int sum = remain.values().stream().mapToInt(Integer::intValue).sum();
			if (sum != remainLiters) {
				remain = SmeltEngine.scaleToLiters(remain, remainLiters);
			}
		}
		return new MoltenMetalData(remainLiters, remain);
	}
}
