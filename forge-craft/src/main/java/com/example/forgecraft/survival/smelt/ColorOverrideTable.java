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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Color overrides: builtins plus datapack {@code smelt_color_override} JSON.
 */
public final class ColorOverrideTable {
	public static final String FOLDER = "smelt_color_override";

	private static Map<String, int[]> TABLE = builtins();

	private ColorOverrideTable() {
	}

	public static void registerReloadListener() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return SurvivalMod.id("smelt_color_overrides");
			}

			@Override
			public void reload(ResourceManager manager) {
				ColorOverrideTable.reload(manager);
			}
		});
	}

	public static void reload(ResourceManager manager) {
		Map<String, int[]> next = new HashMap<>(builtins());
		Map<Identifier, Resource> found = manager.findResources(FOLDER, id -> id.getPath().endsWith(".json"));
		for (Map.Entry<Identifier, Resource> e : found.entrySet()) {
			try (var reader = new InputStreamReader(e.getValue().getInputStream(), StandardCharsets.UTF_8)) {
				JsonElement root = JsonParser.parseReader(reader);
				if (!root.isJsonObject()) {
					continue;
				}
				JsonObject json = root.getAsJsonObject();
				if (!json.has("materials") || !json.has("color")) {
					ForgeCraftMod.LOGGER.warn("Skip smelt_color_override {}: need materials + color", e.getKey());
					continue;
				}
				JsonArray mats = json.getAsJsonArray("materials");
				JsonArray color = json.getAsJsonArray("color");
				if (mats.isEmpty() || color.size() < 3) {
					continue;
				}
				String[] ids = new String[mats.size()];
				for (int i = 0; i < mats.size(); i++) {
					ids[i] = mats.get(i).getAsString();
				}
				next.put(key(ids), new int[]{color.get(0).getAsInt(), color.get(1).getAsInt(), color.get(2).getAsInt()});
			} catch (Exception ex) {
				ForgeCraftMod.LOGGER.error("Failed to load smelt_color_override {}", e.getKey(), ex);
			}
		}
		TABLE = Map.copyOf(next);
		ForgeCraftMod.LOGGER.info("Loaded {} smelt color overrides", TABLE.size());
	}

	public static int[] lookup(Map<String, Integer> composition) {
		if (composition == null || composition.isEmpty()) {
			return null;
		}
		String key = composition.keySet().stream().sorted().collect(Collectors.joining("+"));
		return TABLE.get(key);
	}

	private static Map<String, int[]> builtins() {
		Map<String, int[]> map = new HashMap<>();
		map.put(key("copper", "gold"), new int[]{220, 160, 80});
		map.put(key("carbon", "iron"), new int[]{60, 60, 65});
		map.put(key("lapis", "obsidian"), new int[]{40, 30, 120});
		return map;
	}

	private static String key(String... ids) {
		return Arrays.stream(ids).sorted().collect(Collectors.joining("+"));
	}
}
