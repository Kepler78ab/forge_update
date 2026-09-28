package com.example.forgecraft.survival.smelt;

import com.example.forgecraft.ForgeCraftMod;
import com.example.forgecraft.survival.SurvivalMod;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Material table: builtins from melt design doc, overlaid by datapack
 * {@code smelt_material} JSON on reload.
 */
public final class SmeltMaterials {
	public static final String FOLDER = "smelt_material";

	private static Map<String, SmeltMaterial> BY_ID = builtins();

	private SmeltMaterials() {
	}

	public static void registerReloadListener() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return SurvivalMod.id("smelt_materials");
			}

			@Override
			public void reload(ResourceManager manager) {
				SmeltMaterials.reload(manager);
			}
		});
	}

	public static void reload(ResourceManager manager) {
		Map<String, SmeltMaterial> next = new LinkedHashMap<>(builtins());
		Map<Identifier, Resource> found = manager.findResources(FOLDER, id -> id.getPath().endsWith(".json"));
		for (Map.Entry<Identifier, Resource> e : found.entrySet()) {
			try (var reader = new InputStreamReader(e.getValue().getInputStream(), StandardCharsets.UTF_8)) {
				JsonElement root = JsonParser.parseReader(reader);
				if (!root.isJsonObject()) {
					ForgeCraftMod.LOGGER.warn("Skip smelt_material {}: not an object", e.getKey());
					continue;
				}
				SmeltMaterial material = parse(e.getKey(), root.getAsJsonObject());
				if (material == null) {
					continue;
				}
				next.put(material.id(), material);
			} catch (Exception ex) {
				ForgeCraftMod.LOGGER.error("Failed to load smelt_material {}", e.getKey(), ex);
			}
		}
		BY_ID = Collections.unmodifiableMap(next);
		ForgeCraftMod.LOGGER.info("Loaded {} smelt materials ({} from datapack overlays)", BY_ID.size(), found.size());
	}

	private static SmeltMaterial parse(Identifier fileId, JsonObject json) {
		String id = json.has("id") ? json.get("id").getAsString() : idFromPath(fileId);
		if (id == null || id.isBlank()) {
			ForgeCraftMod.LOGGER.warn("Skip smelt_material {}: missing id", fileId);
			return null;
		}
		if (!json.has("color") || !json.get("color").isJsonArray()) {
			ForgeCraftMod.LOGGER.warn("Skip smelt_material {}: color [r,g,b] required", fileId);
			return null;
		}
		JsonArray color = json.getAsJsonArray("color");
		if (color.size() < 3) {
			ForgeCraftMod.LOGGER.warn("Skip smelt_material {}: color needs 3 ints", fileId);
			return null;
		}
		int r = color.get(0).getAsInt();
		int g = color.get(1).getAsInt();
		int b = color.get(2).getAsInt();
		float hardness = json.has("hardness") ? json.get("hardness").getAsFloat() : 1.0f;
		float weight = json.has("weight") ? json.get("weight").getAsFloat() : 1.0f;
		MaterialCategory category = json.has("category")
				? MaterialCategory.fromString(json.get("category").getAsString())
				: MaterialCategory.METAL;
		String special = json.has("special") && !json.get("special").isJsonNull()
				? json.get("special").getAsString()
				: null;
		try {
			return new SmeltMaterial(id, r, g, b, hardness, weight, category, special);
		} catch (IllegalArgumentException ex) {
			ForgeCraftMod.LOGGER.warn("Skip smelt_material {}: {}", fileId, ex.getMessage());
			return null;
		}
	}

	private static String idFromPath(Identifier fileId) {
		String path = fileId.getPath();
		int slash = path.lastIndexOf('/');
		int dot = path.lastIndexOf('.');
		if (slash < 0 || dot <= slash) {
			return null;
		}
		return path.substring(slash + 1, dot);
	}

	private static Map<String, SmeltMaterial> builtins() {
		Map<String, SmeltMaterial> map = new LinkedHashMap<>();
		put(map, new SmeltMaterial("copper", 200, 120, 60, 3.0f, 1.0f, MaterialCategory.METAL, null));
		put(map, new SmeltMaterial("gold", 255, 215, 0, 2.5f, 1.0f, MaterialCategory.METAL, null));
		put(map, new SmeltMaterial("iron", 180, 180, 180, 5.0f, 1.0f, MaterialCategory.METAL, null));
		put(map, new SmeltMaterial("obsidian", 30, 20, 50, 6.0f, 1.5f, MaterialCategory.GEM, null));
		put(map, new SmeltMaterial("lapis", 30, 60, 180, 4.0f, 1.2f, MaterialCategory.GEM, null));
		put(map, new SmeltMaterial("emerald", 30, 200, 100, 5.5f, 1.2f, MaterialCategory.GEM, null));
		put(map, new SmeltMaterial("redstone", 200, 30, 30, 3.5f, 1.0f, MaterialCategory.GEM, null));
		put(map, new SmeltMaterial("carbon", 40, 40, 40, 2.0f, 0.8f, MaterialCategory.IMPURITY, null));
		put(map, new SmeltMaterial("impurity", 120, 110, 100, 1.0f, 0.5f, MaterialCategory.IMPURITY, null));
		return map;
	}

	private static void put(Map<String, SmeltMaterial> map, SmeltMaterial material) {
		map.put(material.id(), material);
	}

	public static SmeltMaterial get(String id) {
		return BY_ID.get(id);
	}

	public static boolean contains(String id) {
		return BY_ID.containsKey(id);
	}

	public static Map<String, SmeltMaterial> all() {
		return BY_ID;
	}
}
