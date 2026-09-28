package com.example.forgecraft.survival.forge;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.StringIdentifiable;

public enum WeaponProfileId implements StringIdentifiable {
	SWORD_BASIC("sword_basic", Handedness.ONE_HAND, Kind.TOOL),
	SWORD_LONG("sword_long", Handedness.TWO_HAND, Kind.TOOL),
	AXE_BASIC("axe_basic", Handedness.ONE_HAND, Kind.TOOL),
	PICKAXE_BASIC("pickaxe_basic", Handedness.ONE_HAND, Kind.TOOL),
	SHOVEL_BASIC("shovel_basic", Handedness.ONE_HAND, Kind.TOOL),
	HOE_BASIC("hoe_basic", Handedness.ONE_HAND, Kind.TOOL),
	HELMET("helmet", Handedness.ONE_HAND, Kind.ARMOR),
	CHESTPLATE("chestplate", Handedness.ONE_HAND, Kind.ARMOR),
	LEGGINGS("leggings", Handedness.ONE_HAND, Kind.ARMOR),
	BOOTS("boots", Handedness.ONE_HAND, Kind.ARMOR);

	public static final Codec<WeaponProfileId> CODEC = StringIdentifiable.createCodec(WeaponProfileId::values);
	public static final PacketCodec<RegistryByteBuf, WeaponProfileId> PACKET_CODEC = PacketCodec.ofStatic(
			(buf, value) -> buf.writeVarInt(value.ordinal()),
			buf -> values()[buf.readVarInt()]
	);

	private final String id;
	private final Handedness handedness;
	private final Kind kind;

	WeaponProfileId(String id, Handedness handedness, Kind kind) {
		this.id = id;
		this.handedness = handedness;
		this.kind = kind;
	}

	public Handedness handedness() {
		return this.handedness;
	}

	public Kind kind() {
		return this.kind;
	}

	public boolean isArmor() {
		return this.kind == Kind.ARMOR;
	}

	@Override
	public String asString() {
		return this.id;
	}

	public enum Kind {
		TOOL,
		ARMOR
	}

	public enum Handedness implements StringIdentifiable {
		ONE_HAND("one_hand"),
		TWO_HAND("two_hand");

		public static final Codec<Handedness> CODEC = StringIdentifiable.createCodec(Handedness::values);
		public static final PacketCodec<RegistryByteBuf, Handedness> PACKET_CODEC = PacketCodec.ofStatic(
				(buf, value) -> buf.writeVarInt(value.ordinal()),
				buf -> values()[buf.readVarInt()]
		);

		private final String id;

		Handedness(String id) {
			this.id = id;
		}

		@Override
		public String asString() {
			return this.id;
		}
	}
}
