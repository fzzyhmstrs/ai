package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.EquipmentAugment
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack

class SwiftnessAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): EquipmentAugment(weight, mxLvl,EnchantmentTarget.ARMOR_LEGS, *slot) {

    override fun attributeModifier(stack: ItemStack,level: Int, uuid: UUID): Pair<EntityAttribute, EntityAttributeModifier> {
        return Pair(EntityAttributes.GENERIC_MOVEMENT_SPEED,
            EntityAttributeModifier(uuid,"swiftness_augment_mod",0.1 * level,EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        )
    }

}
