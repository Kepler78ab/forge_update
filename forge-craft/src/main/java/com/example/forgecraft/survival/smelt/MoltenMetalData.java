package com.example.forgecraft.survival.smelt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Molten metal item state: volume in 升 (≤{@link #MAX_LITERS}) + material composition.
 */
public record MoltenMetalData(int liters, Map<String, Integer> composition) {
	public static final int MAX_LITERS = 64;

	public static final Codec<MoltenMetalData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("liters").forGetter(MoltenMetalData::liters),
			Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("composition").forGetter(MoltenMetalData::composition)
	).apply(instance, MoltenMetalData::new));

	public static final PacketCodec<RegistryByteBuf, MoltenMetalData> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.VAR_INT, MoltenMetalData::liters,
			PacketCodecs.map(LinkedHashMap::new, PacketCodecs.STRING, PacketCodecs.VAR_INT), MoltenMetalData::composition,
			MoltenMetalData::new
	);

	public MoltenMetalData {
		if (liters < 1 || liters > MAX_LITERS) {
			throw new IllegalArgumentException("liters must be 1.." + MAX_LITERS);
		}
		Map<String, Integer> cleaned = new LinkedHashMap<>();
		int sum = 0;
		if (composition != null) {
			for (Map.Entry<String, Integer> e : composition.entrySet()) {
				if (e.getKey() == null || e.getValue() == null || e.getValue() <= 0) {
					continue;
				}
				if (!SmeltMaterials.contains(e.getKey())) {
					continue;
				}
				cleaned.put(e.getKey(), e.getValue());
				sum += e.getValue();
			}
		}
		if (cleaned.isEmpty()) {
			cleaned.put("iron", liters);
			sum = liters;
		}
		if (sum != liters) {
			cleaned = new LinkedHashMap<>(SmeltEngine.scaleToLiters(cleaned, liters));
		}
		composition = Collections.unmodifiableMap(cleaned);
	}

	public SmeltResult evaluate() {
		return SmeltEngine.smelt(composition);
	}

	public static MoltenMetalData of(String materialId, int liters) {
		return new MoltenMetalData(liters, Map.of(materialId, liters));
	}

	public static MoltenMetalData of(Map<String, Integer> composition) {
		int sum = composition.values().stream().mapToInt(Integer::intValue).sum();
		int liters = Math.max(1, Math.min(MAX_LITERS, sum));
		return new MoltenMetalData(liters, SmeltEngine.scaleToLiters(composition, liters));
	}
}
