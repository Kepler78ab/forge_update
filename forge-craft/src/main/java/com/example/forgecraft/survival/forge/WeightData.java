package com.example.forgecraft.survival.forge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record WeightData(int units) {
	public static final Codec<WeightData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("units").forGetter(WeightData::units)
	).apply(instance, WeightData::new));

	public static final PacketCodec<RegistryByteBuf, WeightData> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.VAR_INT, WeightData::units,
			WeightData::new
	);

	/** Base attack-speed additive (vanilla swords ~-2.4). Heavier → slower. */
	public float attackSpeedModifier() {
		return -2.0f - Math.max(0, units - 1) * 0.35f;
	}
}
