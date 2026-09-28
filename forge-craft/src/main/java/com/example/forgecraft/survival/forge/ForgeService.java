package com.example.forgecraft.survival.forge;

import com.example.forgecraft.survival.SurvivalMod;
import com.example.forgecraft.survival.registry.ModComponents;
import com.example.forgecraft.survival.registry.ModItems;
import com.example.forgecraft.survival.smelt.MoltenMetalData;
import com.example.forgecraft.survival.smelt.SmeltResult;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public final class ForgeService {
	/** Burning cast tools: intentionally weak melee. */
	private static final float BURNING_DAMAGE_SWORD = 2.0f;
	private static final float BURNING_DAMAGE_LONG = 2.5f;
	private static final float BURNING_DAMAGE_AXE = 3.0f;
	private static final float BURNING_DAMAGE_PICK = 1.5f;
	private static final float BURNING_DAMAGE_SHOVEL = 1.5f;
	private static final float BURNING_DAMAGE_HOE = 1.0f;

	private ForgeService() {
	}

	public static ItemStack createTemplateStack(WeaponProfileId profile, TemplateFamily family) {
		ItemStack stack = new ItemStack(ModItems.FORGE_TEMPLATE);
		stack.set(ModComponents.FORGE_TEMPLATE, new ForgeTemplateData(profile, family));
		applyTemplateModel(stack, profile, family);
		return stack;
	}

	private static void applyTemplateModel(ItemStack stack, WeaponProfileId profile, TemplateFamily family) {
		String key = profile.asString() + "_" + family.asString();
		stack.set(
				DataComponentTypes.CUSTOM_MODEL_DATA,
				new CustomModelDataComponent(List.of(), List.of(), List.of(key), List.of())
		);
	}

	public static ItemStack createPieceStack(ForgePieceStage stage, WeaponProfileId profile, ForgeMetal metal) {
		ItemStack stack = new ItemStack(ModItems.FORGE_PIECE);
		stack.set(ModComponents.FORGE_PIECE, new ForgePieceData(stage, profile, metal, metal.units()));
		applyPieceModel(stack, stage, profile, metal);
		return stack;
	}

	public static ItemStack createBurningPiece(ForgeTemplateData template, ForgeMetal metal) {
		return createPieceStack(ForgePieceStage.BURNING, template.profileId(), metal);
	}

	/**
	 * F6 cast: burning tool/armor blank from molten slice. Tools get Fire Aspect I + low damage.
	 */
	public static ItemStack createBurningCast(WeaponProfileId profile, MoltenMetalData alloy, @Nullable World world) {
		ItemStack stack = new ItemStack(ModItems.FORGE_PIECE);
		ForgeMetal displayMetal = displayMetalFor(alloy.evaluate());
		stack.set(ModComponents.FORGE_PIECE, new ForgePieceData(ForgePieceStage.BURNING, profile, displayMetal, alloy.liters()));
		stack.set(ModComponents.MOLTEN_METAL, alloy);
		SmeltResult result = alloy.evaluate();
		stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(result.rgb()));
		applyPieceModel(stack, ForgePieceStage.BURNING, profile, displayMetal);
		if (!profile.isArmor()) {
			stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, burningAttributes(profile));
			applyFireAspect(stack, world);
		}
		return stack;
	}

	private static void applyFireAspect(ItemStack stack, @Nullable World world) {
		if (world == null || world.isClient()) {
			return;
		}
		Optional<RegistryEntry.Reference<Enchantment>> entry = world.getRegistryManager()
				.getOrThrow(RegistryKeys.ENCHANTMENT)
				.getEntry(Enchantments.FIRE_ASPECT.getValue());
		entry.ifPresent(enchantment -> stack.addEnchantment(enchantment, 1));
	}

	private static AttributeModifiersComponent burningAttributes(WeaponProfileId profile) {
		float damage = switch (profile) {
			case SWORD_BASIC -> BURNING_DAMAGE_SWORD;
			case SWORD_LONG -> BURNING_DAMAGE_LONG;
			case AXE_BASIC -> BURNING_DAMAGE_AXE;
			case PICKAXE_BASIC -> BURNING_DAMAGE_PICK;
			case SHOVEL_BASIC -> BURNING_DAMAGE_SHOVEL;
			case HOE_BASIC -> BURNING_DAMAGE_HOE;
			default -> BURNING_DAMAGE_SWORD;
		};
		float speed = switch (profile) {
			case AXE_BASIC -> -3.0f;
			case PICKAXE_BASIC -> -2.8f;
			case SHOVEL_BASIC -> -3.0f;
			case HOE_BASIC -> -1.0f;
			case SWORD_LONG -> -2.6f;
			default -> -2.4f;
		};
		return AttributeModifiersComponent.builder()
				.add(
						EntityAttributes.ATTACK_DAMAGE,
						new EntityAttributeModifier(SurvivalMod.id("burning_damage"), damage, EntityAttributeModifier.Operation.ADD_VALUE),
						AttributeModifierSlot.MAINHAND
				)
				.add(
						EntityAttributes.ATTACK_SPEED,
						new EntityAttributeModifier(SurvivalMod.id("burning_speed"), speed, EntityAttributeModifier.Operation.ADD_VALUE),
						AttributeModifierSlot.MAINHAND
				)
				.build();
	}

	private static ForgeMetal displayMetalFor(SmeltResult result) {
		if (result.hardness() >= 6.0f) {
			return ForgeMetal.STEEL;
		}
		if (result.hardness() >= 4.0f) {
			return ForgeMetal.IRON;
		}
		return ForgeMetal.COPPER;
	}

	/**
	 * Cool a burning piece. Cast alloys → finished forged tool (or cooled armor blank).
	 * Legacy pieces (no molten) → COOLED stage in place.
	 *
	 * @return replacement stack when item type changes; otherwise same stack mutated / null if failed
	 */
	@Nullable
	public static ItemStack coolBurning(ItemStack stack) {
		ForgePieceData data = stack.get(ModComponents.FORGE_PIECE);
		if (data == null || data.stage() != ForgePieceStage.BURNING) {
			return null;
		}
		MoltenMetalData alloy = stack.get(ModComponents.MOLTEN_METAL);
		if (alloy != null && !data.profileId().isArmor()) {
			return createFinishedWeapon(data.profileId(), alloy);
		}
		coolPiece(stack);
		return stack;
	}

	public static boolean coolPiece(ItemStack stack) {
		ForgePieceData data = stack.get(ModComponents.FORGE_PIECE);
		if (data == null || data.stage() != ForgePieceStage.BURNING) {
			return false;
		}
		stack.set(ModComponents.FORGE_PIECE, data.withStage(ForgePieceStage.COOLED));
		applyPieceModel(stack, ForgePieceStage.COOLED, data.profileId(), data.metal());
		stack.remove(DataComponentTypes.ATTRIBUTE_MODIFIERS);
		stack.remove(DataComponentTypes.ENCHANTMENTS);
		return true;
	}

	public static boolean disassembleToPart(ItemStack stack) {
		ForgePieceData data = stack.get(ModComponents.FORGE_PIECE);
		if (data == null || data.stage() != ForgePieceStage.COOLED) {
			return false;
		}
		stack.set(ModComponents.FORGE_PIECE, data.withStage(ForgePieceStage.PART));
		applyPieceModel(stack, ForgePieceStage.PART, data.profileId(), data.metal());
		return true;
	}

	/**
	 * Burning: {@code burning_<profile>}. Cooled/part: {@code <stage>_<profile>_<metal>}.
	 */
	private static void applyPieceModel(ItemStack stack, ForgePieceStage stage, WeaponProfileId profile, ForgeMetal metal) {
		String key = stage == ForgePieceStage.BURNING
				? stage.asString() + "_" + profile.asString()
				: stage.asString() + "_" + profile.asString() + "_" + metal.asString();
		stack.set(
				DataComponentTypes.CUSTOM_MODEL_DATA,
				new CustomModelDataComponent(List.of(), List.of(), List.of(key), List.of())
		);
	}

	public static Optional<ItemStack> haftWeapon(ItemStack partStack) {
		ForgePieceData data = partStack.get(ModComponents.FORGE_PIECE);
		if (data == null || data.stage() != ForgePieceStage.PART) {
			return Optional.empty();
		}
		Item weaponItem = toolItemFor(data.profileId());
		if (weaponItem == null) {
			return Optional.empty();
		}
		WeightData weight = new WeightData(data.units());
		ItemStack weapon = new ItemStack(weaponItem);
		weapon.set(ModComponents.FORGE_PIECE, data);
		weapon.set(ModComponents.WEAPON_PROFILE, WeaponProfileData.of(data.profileId()));
		weapon.set(ModComponents.WEIGHT, weight);
		weapon.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, attributesFor(data.profileId(), data.metal(), weight));
		applySharpnessIfPresent(weapon, maxSharpness(data.metal()));
		return Optional.of(weapon);
	}

	public static ItemStack createFinishedWeapon(WeaponProfileId profile, MoltenMetalData alloy) {
		Item weaponItem = toolItemFor(profile);
		if (weaponItem == null) {
			throw new IllegalArgumentException("No forged item for " + profile);
		}
		SmeltResult result = alloy.evaluate();
		WeightData weight = new WeightData(Math.max(1, alloy.liters()));
		ItemStack weapon = new ItemStack(weaponItem);
		weapon.set(ModComponents.WEAPON_PROFILE, WeaponProfileData.of(profile));
		weapon.set(ModComponents.WEIGHT, weight);
		weapon.set(ModComponents.MOLTEN_METAL, alloy);
		weapon.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(result.rgb()));
		weapon.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, attributesFromHardness(profile, result.hardness(), weight));
		int durability = Math.max(64, Math.round(150 + result.hardness() * 350));
		weapon.set(DataComponentTypes.MAX_DAMAGE, durability);
		weapon.set(DataComponentTypes.DAMAGE, 0);
		applySharpnessIfPresent(weapon, maxSharpnessFromHardness(result.hardness()));
		return weapon;
	}

	private static void applySharpnessIfPresent(ItemStack stack, float maxSharpness) {
		if (FabricLoader.getInstance().isModLoaded("tool_sharpness")) {
			ForgeSharpnessHook.applyDull(stack, maxSharpness);
		}
	}

	@Nullable
	private static Item toolItemFor(WeaponProfileId profile) {
		return switch (profile) {
			case SWORD_BASIC, SWORD_LONG -> ModItems.FORGED_SWORD;
			case AXE_BASIC -> ModItems.FORGED_AXE;
			case PICKAXE_BASIC -> ModItems.FORGED_PICKAXE;
			case SHOVEL_BASIC -> ModItems.FORGED_SHOVEL;
			case HOE_BASIC -> ModItems.FORGED_HOE;
			case HELMET, CHESTPLATE, LEGGINGS, BOOTS -> null;
		};
	}

	private static AttributeModifiersComponent attributesFromHardness(WeaponProfileId profile, float hardness, WeightData weight) {
		float damage = 2.0f + hardness * 0.7f;
		if (profile == WeaponProfileId.AXE_BASIC) {
			damage += 1.5f;
		}
		if (profile == WeaponProfileId.PICKAXE_BASIC) {
			damage -= 1.0f;
		}
		if (profile == WeaponProfileId.HOE_BASIC) {
			damage -= 2.0f;
		}
		if (profile == WeaponProfileId.SWORD_LONG) {
			damage += 0.5f;
		}
		return AttributeModifiersComponent.builder()
				.add(
						EntityAttributes.ATTACK_DAMAGE,
						new EntityAttributeModifier(SurvivalMod.id("forged_damage"), damage, EntityAttributeModifier.Operation.ADD_VALUE),
						AttributeModifierSlot.MAINHAND
				)
				.add(
						EntityAttributes.ATTACK_SPEED,
						new EntityAttributeModifier(SurvivalMod.id("forged_speed"), weight.attackSpeedModifier(), EntityAttributeModifier.Operation.ADD_VALUE),
						AttributeModifierSlot.MAINHAND
				)
				.build();
	}

	private static AttributeModifiersComponent attributesFor(WeaponProfileId profile, ForgeMetal metal, WeightData weight) {
		float damage = switch (metal) {
			case COPPER -> 4.0f;
			case IRON -> 5.0f;
			case STEEL -> 6.0f;
		};
		if (profile == WeaponProfileId.AXE_BASIC) {
			damage += 1.5f;
		}
		if (profile == WeaponProfileId.PICKAXE_BASIC) {
			damage -= 1.0f;
		}
		if (profile == WeaponProfileId.HOE_BASIC) {
			damage -= 2.0f;
		}
		return AttributeModifiersComponent.builder()
				.add(
						EntityAttributes.ATTACK_DAMAGE,
						new EntityAttributeModifier(SurvivalMod.id("forged_damage"), damage, EntityAttributeModifier.Operation.ADD_VALUE),
						AttributeModifierSlot.MAINHAND
				)
				.add(
						EntityAttributes.ATTACK_SPEED,
						new EntityAttributeModifier(SurvivalMod.id("forged_speed"), weight.attackSpeedModifier(), EntityAttributeModifier.Operation.ADD_VALUE),
						AttributeModifierSlot.MAINHAND
				)
				.build();
	}

	public static boolean canForgeWithTemplate(ForgeTemplateData template, ForgeMetal metal) {
		if (metal.requiresNetherrack()) {
			return template.family() == TemplateFamily.NETHERRACK;
		}
		return true;
	}

	public static float maxSharpness(ForgeMetal metal) {
		return switch (metal) {
			case COPPER -> 70.0f;
			case IRON -> 80.0f;
			case STEEL -> 100.0f;
		};
	}

	public static float maxSharpnessFromHardness(float hardness) {
		return Math.min(100.0f, 40.0f + hardness * 10.0f);
	}
}
