package com.example.forgecraft.survival.forge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ForgeTemplateData(WeaponProfileId profileId, TemplateFamily family) {
	public static final Codec<ForgeTemplateData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			WeaponProfileId.CODEC.fieldOf("profile").forGetter(ForgeTemplateData::profileId),
			TemplateFamily.CODEC.fieldOf("family").forGetter(ForgeTemplateData::family)
	).apply(instance, ForgeTemplateData::new));

	public static final PacketCodec<RegistryByteBuf, ForgeTemplateData> PACKET_CODEC = PacketCodec.tuple(
			WeaponProfileId.PACKET_CODEC, ForgeTemplateData::profileId,
			TemplateFamily.PACKET_CODEC, ForgeTemplateData::family,
			ForgeTemplateData::new
	);
}
