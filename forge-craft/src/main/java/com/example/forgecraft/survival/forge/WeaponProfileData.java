package com.example.forgecraft.survival.forge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record WeaponProfileData(WeaponProfileId profileId, WeaponProfileId.Handedness handedness) {
	public static final Codec<WeaponProfileData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			WeaponProfileId.CODEC.fieldOf("profile").forGetter(WeaponProfileData::profileId),
			WeaponProfileId.Handedness.CODEC.fieldOf("handedness").forGetter(WeaponProfileData::handedness)
	).apply(instance, WeaponProfileData::new));

	public static final PacketCodec<RegistryByteBuf, WeaponProfileData> PACKET_CODEC = PacketCodec.tuple(
			WeaponProfileId.PACKET_CODEC, WeaponProfileData::profileId,
			WeaponProfileId.Handedness.PACKET_CODEC, WeaponProfileData::handedness,
			WeaponProfileData::new
	);

	public static WeaponProfileData of(WeaponProfileId profileId) {
		return new WeaponProfileData(profileId, profileId.handedness());
	}
}
