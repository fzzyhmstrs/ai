package me.fzzyhmstrs.amethyst_imbuement.armor

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ArmorMaterial
import net.minecraft.recipe.Ingredient
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents

@Suppress("PrivatePropertyName")
class AmetrineArmorMaterial : ArmorMaterial {
    private val BASE_DURABILITY = intArrayOf(13, 15, 16, 11)
    private val PROTECTION_VALUES = intArrayOf(4, 7, 9, 4)


    override fun getName(): String = "ametrine"
    override fun getEquipSound(): SoundEvent = SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND
    override fun getRepairIngredient(): Ingredient? = Ingredient.ofItems(RegisterItem.AMETRINE)
    override fun getEnchantability(): Int = 18
    override fun getProtectionAmount(slot: EquipmentSlot): Int = PROTECTION_VALUES[slot.entitySlotId]
    override fun getDurability(slot: EquipmentSlot): Int = BASE_DURABILITY[slot.entitySlotId] * 39
    override fun getKnockbackResistance(): Float = 0.1F
    override fun getToughness(): Float = 3.2F
}