package com.example.forgecraft.survival.registry;

import com.example.forgecraft.survival.SurvivalMod;
import com.example.forgecraft.survival.combat.ArmorClassData;
import com.example.forgecraft.survival.forge.ForgePieceData;
import com.example.forgecraft.survival.forge.ForgeTemplateData;
import com.example.forgecraft.survival.forge.WeaponProfileData;
import com.example.forgecraft.survival.forge.WeightData;
import com.example.forgecraft.survival.smelt.MoltenMetalData;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModComponents {
	public static ComponentType<ForgeTemplateData> FORGE_TEMPLATE;
	public static ComponentType<ForgePieceData> FORGE_PIECE;
	public static ComponentType<WeaponProfileData> WEAPON_PROFILE;
	public static ComponentType<WeightData> WEIGHT;
	public static ComponentType<ArmorClassData> ARMOR_CLASS;
	public static ComponentType<MoltenMetalData> MOLTEN_METAL;

	private ModComponents() {
	}

	public static void register() {
		FORGE_TEMPLATE = register("forge_template", ForgeTemplateData.CODEC, ForgeTemplateData.PACKET_CODEC);
		FORGE_PIECE = register("forge_piece", ForgePieceData.CODEC, ForgePieceData.PACKET_CODEC);
		WEAPON_PROFILE = register("weapon_profile", WeaponProfileData.CODEC, WeaponProfileData.PACKET_CODEC);
		WEIGHT = register("weight", WeightData.CODEC, WeightData.PACKET_CODEC);
		ARMOR_CLASS = register("armor_class", ArmorClassData.CODEC, ArmorClassData.PACKET_CODEC);
		MOLTEN_METAL = register("molten_metal", MoltenMetalData.CODEC, MoltenMetalData.PACKET_CODEC);
	}

	private static <T> ComponentType<T> register(
			String path,
			com.mojang.serialization.Codec<T> codec,
			net.minecraft.network.codec.PacketCodec<? super net.minecraft.network.RegistryByteBuf, T> packetCodec
	) {
		return Registry.register(
				Registries.DATA_COMPONENT_TYPE,
				SurvivalMod.id(path),
				ComponentType.<T>builder().codec(codec).packetCodec(packetCodec).build()
		);
	}
}
