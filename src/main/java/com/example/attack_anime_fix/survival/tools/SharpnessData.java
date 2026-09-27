package com.example.attack_anime_fix.survival.tools;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

/**
 * Item component: current sharpness, material cap, and per-hit decay.
 */
public record SharpnessData(float sharpness, float maxSharpness, float decayPerDamage) {
	public static final Codec<SharpnessData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("sharpness").forGetter(SharpnessData::sharpness),
			Codec.FLOAT.fieldOf("max_sharpness").forGetter(SharpnessData::maxSharpness),
			Codec.FLOAT.fieldOf("decay_per_damage").forGetter(SharpnessData::decayPerDamage)
	).apply(instance, SharpnessData::new));

	public static final PacketCodec<RegistryByteBuf, SharpnessData> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.FLOAT, SharpnessData::sharpness,
			PacketCodecs.FLOAT, SharpnessData::maxSharpness,
			PacketCodecs.FLOAT, SharpnessData::decayPerDamage,
			SharpnessData::new
	);

	public static SharpnessData dull(float maxSharpness) {
		return new SharpnessData(0.0f, maxSharpness, 0.025f);
	}

	public static SharpnessData full(float maxSharpness) {
		return new SharpnessData(maxSharpness, maxSharpness, 0.02f);
	}

	public SharpnessData withSharpness(float value) {
		return new SharpnessData(Math.clamp(value, 0.0f, maxSharpness), maxSharpness, decayPerDamage);
	}

	public float ratio() {
		return maxSharpness <= 0.0f ? 1.0f : sharpness / maxSharpness;
	}
}
