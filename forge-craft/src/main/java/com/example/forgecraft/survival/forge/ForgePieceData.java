package com.example.forgecraft.survival.forge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record ForgePieceData(ForgePieceStage stage, WeaponProfileId profileId, ForgeMetal metal, int units) {
	public static final Codec<ForgePieceData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ForgePieceStage.CODEC.fieldOf("stage").forGetter(ForgePieceData::stage),
			WeaponProfileId.CODEC.fieldOf("profile").forGetter(ForgePieceData::profileId),
			ForgeMetal.CODEC.fieldOf("metal").forGetter(ForgePieceData::metal),
			Codec.INT.fieldOf("units").forGetter(ForgePieceData::units)
	).apply(instance, ForgePieceData::new));

	public static final PacketCodec<RegistryByteBuf, ForgePieceData> PACKET_CODEC = PacketCodec.tuple(
			ForgePieceStage.PACKET_CODEC, ForgePieceData::stage,
			WeaponProfileId.PACKET_CODEC, ForgePieceData::profileId,
			ForgeMetal.PACKET_CODEC, ForgePieceData::metal,
			PacketCodecs.VAR_INT, ForgePieceData::units,
			ForgePieceData::new
	);

	public ForgePieceData withStage(ForgePieceStage next) {
		return new ForgePieceData(next, profileId, metal, units);
	}
}
