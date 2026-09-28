package com.example.forgecraft.survival.smelt;

import com.example.forgecraft.ForgeCraftMod;
import com.example.forgecraft.survival.SurvivalMod;
import com.example.forgecraft.survival.registry.ModComponents;
import com.example.forgecraft.survival.registry.ModItems;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Datapack-driven melt inputs under data/.../smelt_input/*.json.
 * Items not listed melt as {@code impurity}. {@code molten_metal} / {@code alloy_ingot} remelt as-is.
 */
public final class SmeltInputRegistry {
	public static final String FOLDER = "smelt_input";
	public static final String IMPURITY_ID = "impurity";

	public record SmeltInputEntry(Identifier itemId, String material, int liters) {
		public static final Codec<SmeltInputEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Identifier.CODEC.fieldOf("item").forGetter(SmeltInputEntry::itemId),
				Codec.STRING.fieldOf("material").forGetter(SmeltInputEntry::material),
				Codec.INT.optionalFieldOf("liters", 1).forGetter(SmeltInputEntry::liters)
		).apply(instance, SmeltInputEntry::new));
	}

	private static Map<Item, SmeltInputEntry> BY_ITEM = Map.of();

	private SmeltInputRegistry() {
	}

	public static void registerReloadListener() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return SurvivalMod.id("smelt_inputs");
			}

			@Override
			public void reload(ResourceManager manager) {
				SmeltInputRegistry.reload(manager);
			}
		});
	}

	public static void reload(ResourceManager manager) {
		Map<Item, SmeltInputEntry> next = new HashMap<>();
		Map<Identifier, Resource> found = manager.findResources(FOLDER, id -> id.getPath().endsWith(".json"));
		for (Map.Entry<Identifier, Resource> e : found.entrySet()) {
			try (var reader = new InputStreamReader(e.getValue().getInputStream(), StandardCharsets.UTF_8)) {
				JsonElement root = JsonParser.parseReader(reader);
				SmeltInputEntry entry = SmeltInputEntry.CODEC.parse(JsonOps.INSTANCE, root)
						.getOrThrow(msg -> new IllegalStateException(e.getKey() + ": " + msg));
				if (entry.liters() < 1) {
					ForgeCraftMod.LOGGER.warn("Skip smelt_input {}: liters < 1", e.getKey());
					continue;
				}
				if (!SmeltMaterials.contains(entry.material())) {
					ForgeCraftMod.LOGGER.warn("Skip smelt_input {}: unknown material {}", e.getKey(), entry.material());
					continue;
				}
				Item item = Registries.ITEM.getOptionalValue(entry.itemId()).orElse(Items.AIR);
				if (item == Items.AIR || item == null) {
					ForgeCraftMod.LOGGER.warn("Skip smelt_input {}: unknown item {}", e.getKey(), entry.itemId());
					continue;
				}
				next.put(item, entry);
			} catch (Exception ex) {
				ForgeCraftMod.LOGGER.error("Failed to load smelt_input {}", e.getKey(), ex);
			}
		}
		BY_ITEM = Map.copyOf(next);
		ForgeCraftMod.LOGGER.info("Loaded {} smelt_input entries", BY_ITEM.size());
	}

	/** Any non-empty stack except lava bucket (fuel) can enter the melt slot. */
	public static boolean isMeltable(ItemStack stack) {
		if (stack.isEmpty() || stack.isOf(Items.LAVA_BUCKET)) {
			return false;
		}
		return true;
	}

	public static Optional<MoltenMetalData> resolve(ItemStack stack) {
		if (!isMeltable(stack)) {
			return Optional.empty();
		}
		if (stack.isOf(ModItems.MOLTEN_METAL) || stack.isOf(ModItems.ALLOY_INGOT)) {
			MoltenMetalData data = stack.get(ModComponents.MOLTEN_METAL);
			return data == null ? Optional.empty() : Optional.of(data);
		}
		SmeltInputEntry entry = BY_ITEM.get(stack.getItem());
		if (entry != null) {
			return Optional.of(MoltenMetalData.of(entry.material(), entry.liters()));
		}
		return Optional.of(MoltenMetalData.of(IMPURITY_ID, 1));
	}

	public static Map<Item, SmeltInputEntry> entries() {
		return BY_ITEM;
	}
}
