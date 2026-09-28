package com.example.forgecraft.survival.forge;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.StringIdentifiable;

public enum ForgePieceStage implements StringIdentifiable {
	BURNING("burning"),
	COOLED("cooled"),
	PART("part");

	public static final Codec<ForgePieceStage> CODEC = StringIdentifiable.createCodec(ForgePieceStage::values);
	public static final PacketCodec<RegistryByteBuf, ForgePieceStage> PACKET_CODEC = PacketCodec.ofStatic(
			(buf, value) -> buf.writeVarInt(value.ordinal()),
			buf -> values()[buf.readVarInt()]
	);

	private final String id;

	ForgePieceStage(String id) {
		this.id = id;
	}

	@Override
	public String asString() {
		return this.id;
	}
}
