package com.example.forgecraft.survival.forge;

import com.example.forgecraft.survival.SurvivalMod;
import com.mojang.serialization.Codec;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.StringIdentifiable;

/**
 * Fill material for template crafting (stone / netherrack / red brick / clay).
 */
public enum TemplateFamily implements StringIdentifiable {
	STONE("stone"),
	NETHERRACK("netherrack"),
	BRICK("brick"),
	CLAY("clay");

	public static final Codec<TemplateFamily> CODEC = StringIdentifiable.createCodec(TemplateFamily::values);
	public static final PacketCodec<RegistryByteBuf, TemplateFamily> PACKET_CODEC = PacketCodec.ofStatic(
			(buf, value) -> buf.writeVarInt(value.ordinal()),
			buf -> values()[buf.readVarInt()]
	);

	private final String id;
	private final TagKey<Item> tag;

	TemplateFamily(String id) {
		this.id = id;
		this.tag = TagKey.of(RegistryKeys.ITEM, SurvivalMod.id("template_family_" + id));
	}

	public TagKey<Item> tag() {
		return this.tag;
	}

	@Override
	public String asString() {
		return this.id;
	}
}
