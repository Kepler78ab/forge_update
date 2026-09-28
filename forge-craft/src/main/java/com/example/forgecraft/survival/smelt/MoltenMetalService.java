package com.example.forgecraft.survival.smelt;

import com.example.forgecraft.survival.registry.ModComponents;
import com.example.forgecraft.survival.registry.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MoltenMetalService {
	private MoltenMetalService() {
	}

	public static ItemStack createStack(MoltenMetalData data) {
		ItemStack stack = new ItemStack(ModItems.MOLTEN_METAL);
		apply(stack, data);
		return stack;
	}

	public static ItemStack createPure(String materialId, int liters) {
		return createStack(MoltenMetalData.of(materialId, liters));
	}

	public static void apply(ItemStack stack, MoltenMetalData data) {
		stack.set(ModComponents.MOLTEN_METAL, data);
		SmeltResult result = data.evaluate();
		stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(result.rgb()));
	}

	/** Merge {@code add} into {@code base} if total liters ≤ max. Returns null if overflow. */
	public static MoltenMetalData tryMerge(MoltenMetalData base, MoltenMetalData add) {
		int total = base.liters() + add.liters();
		if (total > MoltenMetalData.MAX_LITERS) {
			return null;
		}
		Map<String, Integer> merged = new LinkedHashMap<>(base.composition());
		for (Map.Entry<String, Integer> e : add.composition().entrySet()) {
			merged.merge(e.getKey(), e.getValue(), Integer::sum);
		}
		return new MoltenMetalData(total, merged);
	}

	/**
	 * Take {@code amount} liters from {@code base}, preserving composition ratios.
	 * @return null if amount invalid / insufficient
	 */
	@Nullable
	public static Split take(MoltenMetalData base, int amount) {
		if (amount < 1 || amount > base.liters()) {
			return null;
		}
		MoltenMetalData taken = new MoltenMetalData(amount, SmeltEngine.scaleToLiters(base.composition(), amount));
		if (amount == base.liters()) {
			return new Split(taken, null);
		}
		int remainLiters = base.liters() - amount;
		MoltenMetalData remaining = new MoltenMetalData(
				remainLiters,
				SmeltEngine.scaleToLiters(base.composition(), remainLiters)
		);
		return new Split(taken, remaining);
	}

	public record Split(MoltenMetalData taken, @Nullable MoltenMetalData remaining) {
	}

	public static void appendTooltip(ItemStack stack, List<Text> lines) {
		MoltenMetalData data = stack.get(ModComponents.MOLTEN_METAL);
		if (data == null) {
			return;
		}
		SmeltResult result = data.evaluate();
		lines.add(Text.translatable("tooltip.forge_craft.molten_liters", data.liters(), MoltenMetalData.MAX_LITERS));
		lines.add(Text.translatable(
				"tooltip.forge_craft.molten_hardness",
				String.format("%.2f", result.hardness())
		));
		lines.add(Text.translatable("tooltip.forge_craft.molten_output." + result.outputId()));
		for (Map.Entry<String, Integer> e : data.composition().entrySet()) {
			int pct = Math.round(100f * e.getValue() / data.liters());
			lines.add(Text.translatable(
					"tooltip.forge_craft.molten_part",
					Text.translatable("smelt.material.forge_craft." + e.getKey()),
					e.getValue(),
					pct
			));
		}
	}
}
