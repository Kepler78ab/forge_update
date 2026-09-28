package com.example.forgecraft.survival.combat;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.StringIdentifiable;

public enum ArmorClass implements StringIdentifiable {
	LIGHT("light", 0.0),
	MEDIUM("medium", -0.03),
	HEAVY("heavy", -0.055);

	public static final Codec<ArmorClass> CODEC = StringIdentifiable.createCodec(ArmorClass::values);
	public static final PacketCodec<RegistryByteBuf, ArmorClass> PACKET_CODEC = PacketCodec.ofStatic(
			(buf, value) -> buf.writeVarInt(value.ordinal()),
			buf -> values()[buf.readVarInt()]
	);

	private final String id;
	/** Per-piece movement-speed multiplier (ADD_MULTIPLIED_TOTAL). */
	private final double speedMultiplier;

	ArmorClass(String id, double speedMultiplier) {
		this.id = id;
		this.speedMultiplier = speedMultiplier;
	}

	public double speedMultiplier() {
		return this.speedMultiplier;
	}

	@Override
	public String asString() {
		return this.id;
	}
}
