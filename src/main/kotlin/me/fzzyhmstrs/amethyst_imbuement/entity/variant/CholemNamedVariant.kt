package me.fzzyhmstrs.amethyst_imbuement.entity.variant

import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.registry.variant.Variant
import net.minecraft.entity.EntityType
import net.minecraft.text.Text
import net.minecraft.util.Identifier

open class CholemNamedVariant(texture: Identifier) : Variant(texture){
    private val translationAppend = texture.path.substringAfterLast('/',"generic.png").substringBefore('.',"generic")
    val enragedTexture = Identifier(texture.namespace,texture.path.substringBefore('.') + "_enraged.png")
    fun getName(type: EntityType<*>): Text {
        return AcText.translatable(type.translationKey + "." + translationAppend)
    }

}