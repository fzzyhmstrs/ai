package me.fzzyhmstrs.amethyst_imbuement.enchantment

import com.google.common.collect.Multimap
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.AttributeProviding
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import java.util.*

class SteadfastEnchantment(weight: Rarity, vararg slot: EquipmentSlot):
    ConfigDisableEnchantment(weight, EnchantmentTarget.ARMOR,*slot),
    AttributeProviding
{

    private val uuids: EnumMap<EquipmentSlot, UUID> = EnumMap(mapOf(
        EquipmentSlot.HEAD to UUID.fromString("797ca106-ba09-11ed-afa1-0242ac120002"),
        EquipmentSlot.CHEST to UUID.fromString("797ca52a-ba09-11ed-afa1-0242ac120002"),
        EquipmentSlot.LEGS to UUID.fromString("797ca67e-ba09-11ed-afa1-0242ac120002"),
        EquipmentSlot.FEET to UUID.fromString("797ca836-ba09-11ed-afa1-0242ac120002")
    ))

    override fun getMinPower(level: Int): Int {
        return 15 * level
    }

    override fun getMaxPower(level: Int): Int {
        return getMinPower(level) + 20
    }

    override fun getMaxLevel(): Int {
        return AiConfig.enchants.getAiMaxLevel(id.toString(),3)
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.item is ArmorItem) && checkEnabled()
    }

    override fun modifyAttributeMap(
        map: Multimap<EntityAttribute, EntityAttributeModifier>,
        slot: EquipmentSlot,
        level: Int
    ) {
        val uuid: UUID = uuids[slot] ?: return
        val name = "steadfast_modifier_" + slot.getName()
        map.put(
            EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
            EntityAttributeModifier(uuid, name, level * 0.05, EntityAttributeModifier.Operation.ADDITION)
        )
    }

}