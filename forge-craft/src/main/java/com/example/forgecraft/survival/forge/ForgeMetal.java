package com.example.forgecraft.survival.forge;

import com.mojang.serialization.Codec;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

public enum ForgeMetal implements StringIdentifiable {
	COPPER("copper", 1, false),
	IRON("iron", 1, false),
	STEEL("steel", 1, true);

	public static final Codec<ForgeMetal> CODEC = StringIdentifiable.createCodec(ForgeMetal::values);
	public static final PacketCodec<RegistryByteBuf, ForgeMetal> PACKET_CODEC = PacketCodec.ofStatic(
			(buf, value) -> buf.writeVarInt(value.ordinal()),
			buf -> values()[buf.readVarInt()]
	);

	private final String id;
	private final int units;
	private final boolean requiresNetherrack;

	ForgeMetal(String id, int units, boolean requiresNetherrack) {
		this.id = id;
		this.units = units;
		this.requiresNetherrack = requiresNetherrack;
	}

	public int units() {
		return this.units;
	}

	public boolean requiresNetherrack() {
		return this.requiresNetherrack;
	}

	@Override
	public String asString() {
		return this.id;
	}

	@Nullable
	public static ForgeMetal fromInput(ItemStack stack) {
		Item item = stack.getItem();
		if (item == Items.COPPER_INGOT || item == Items.RAW_COPPER) {
			return COPPER;
		}
		if (item == Items.IRON_INGOT || item == Items.RAW_IRON || item == Items.IRON_ORE || item == Items.DEEPSLATE_IRON_ORE) {
			return IRON;
		}
		if (item == com.example.forgecraft.survival.registry.ModItems.STEEL_INGOT) {
			return STEEL;
		}
		return null;
	}
}
