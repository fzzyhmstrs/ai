package me.fzzyhmstrs.amethyst_imbuement.armor

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.item.ArmorItem.Type
import net.minecraft.item.ArmorMaterial
import net.minecraft.recipe.Ingredient
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents

object AiArmorMaterials{
    val AMETRINE: ScepterToolMatierial
      get() = AiConfig.materials.armor.ametrine
    val STEEL: ScepterToolMaterial
      get() = AiConfig.materials.armor.steel
}
