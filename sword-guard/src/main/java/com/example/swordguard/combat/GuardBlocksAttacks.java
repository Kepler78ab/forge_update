package com.example.swordguard.combat;

import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvents;

import java.util.List;
import java.util.Optional;

public final class GuardBlocksAttacks {
	private GuardBlocksAttacks() {
	}

	public static BlocksAttacksComponent sword() {
		return new BlocksAttacksComponent(
				0.0f,
				2.0f,
				List.of(new BlocksAttacksComponent.DamageReduction(90.0f, Optional.empty(), 0.0f, 1.0f)),
				new BlocksAttacksComponent.ItemDamage(1.0f, 0.0f, 1.0f),
				Optional.of(DamageTypeTags.BYPASSES_SHIELD),
				Optional.of(SoundEvents.ITEM_SHIELD_BLOCK),
				Optional.of(SoundEvents.ITEM_SHIELD_BREAK)
		);
	}
}
