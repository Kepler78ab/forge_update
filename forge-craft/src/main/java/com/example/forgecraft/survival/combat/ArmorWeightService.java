package com.example.forgecraft.survival.combat;

import com.example.forgecraft.survival.SurvivalMod;
import com.example.forgecraft.survival.registry.ModComponents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/**
 * Applies movement-speed modifiers from worn {@link ArmorClassData} pieces.
 */
public final class ArmorWeightService {
	private static final Identifier MODIFIER_ID = SurvivalMod.id("armor_weight");
	private static final double MIN_MULTIPLIER = -0.30;

	private ArmorWeightService() {
	}

	public static void tick(PlayerEntity player) {
		EntityAttributeInstance speed = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
		if (speed == null) {
			return;
		}
		speed.removeModifier(MODIFIER_ID);
		double total = 0.0;
		for (EquipmentSlot slot : new EquipmentSlot[]{
				EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
		}) {
			ItemStack stack = player.getEquippedStack(slot);
			ArmorClassData data = stack.get(ModComponents.ARMOR_CLASS);
			if (data != null) {
				total += data.armorClass().speedMultiplier();
			}
		}
		if (total > -1.0e-6) {
			return;
		}
		total = Math.max(total, MIN_MULTIPLIER);
		speed.addTemporaryModifier(new EntityAttributeModifier(
				MODIFIER_ID,
				total,
				EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
		));
	}
}
