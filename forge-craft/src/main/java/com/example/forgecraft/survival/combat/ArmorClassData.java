package com.example.forgecraft.survival.combat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ArmorClassData(ArmorClass armorClass) {
	public static final Codec<ArmorClassData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ArmorClass.CODEC.fieldOf("class").forGetter(ArmorClassData::armorClass)
	).apply(instance, ArmorClassData::new));

	public static final PacketCodec<RegistryByteBuf, ArmorClassData> PACKET_CODEC = PacketCodec.tuple(
			ArmorClass.PACKET_CODEC, ArmorClassData::armorClass,
			ArmorClassData::new
	);

	public static ArmorClassData of(ArmorClass armorClass) {
		return new ArmorClassData(armorClass);
	}
}
