package me.fzzyhmstrs.amethyst_imbuement.armor

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.item.ArmorItem.Type
import net.minecraft.item.ArmorMaterial
import net.minecraft.recipe.Ingredient
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents

@Suppress("PrivatePropertyName")
class AmetrineArmorMaterial : ArmorMaterial {
    private val BASE_DURABILITY = intArrayOf(13, 15, 16, 11)
    private val PROTECTION_VALUES = intArrayOf(4, 7, 9, 4)


    override fun getName(): String = "ai_ametrine"
    override fun getEquipSound(): SoundEvent = SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND
    override fun getRepairIngredient(): Ingredient? = Ingredient.ofItems(RegisterItem.AMETRINE)
    override fun getEnchantability(): Int = 18
    override fun getProtection(type: Type): Int = PROTECTION_VALUES[type.equipmentSlot.entitySlotId]
    override fun getDurability(type: Type): Int = BASE_DURABILITY[type.equipmentSlot.entitySlotId] * 39
    override fun getKnockbackResistance(): Float = 0.1F
    override fun getToughness(): Float = 3.2F
}