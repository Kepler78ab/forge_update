package com.example.forgecraft.survival.registry;

import com.example.forgecraft.survival.SurvivalMod;
import com.example.forgecraft.survival.smelt.TemplateFrameEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public final class ModEntities {
	public static EntityType<TemplateFrameEntity> TEMPLATE_FRAME;

	private ModEntities() {
	}

	public static void register() {
		RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, SurvivalMod.id("template_frame"));
		TEMPLATE_FRAME = Registry.register(
				Registries.ENTITY_TYPE,
				key,
				EntityType.Builder.<TemplateFrameEntity>create(TemplateFrameEntity::new, SpawnGroup.MISC)
						.dimensions(0.5f, 0.5f)
						.maxTrackingRange(10)
						.trackingTickInterval(Integer.MAX_VALUE)
						.build(key)
		);
	}
}
