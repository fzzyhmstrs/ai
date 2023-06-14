package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable
import me.fzzyhmstrs.fzzy_core.item_util.interfaces.Flavorful
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.item.ItemStack
import net.minecraft.item.ShieldItem
import net.minecraft.registry.Registries
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.World

class CopperWardItem(
    private val attribute: EntityAttribute,
    private val modifier: EntityAttributeModifier,
    settings: Settings)
    : 
    ShieldItem(settings), SpellcastersReagent, Modifiable, Flavorful<CopperWardItem>
{

    override var flavor: String = ""
    override var glint: Boolean = false
    override var flavorDesc: String = ""

    private val flavorText: MutableText by lazy{
        makeFlavorText()
    }

    private val flavorTextDesc: MutableText by lazy{
        makeFlavorTextDesc()
    }

    private fun makeFlavorText(): MutableText {
        val id = Registries.ITEM.getId(this)
        val key = "item.${id.namespace}.${id.path}.flavor"
        val text = AcText.translatable(key).formatted(Formatting.WHITE, Formatting.ITALIC)
        if (text.string == key) return AcText.empty()
        return text
    }

    private fun makeFlavorTextDesc(): MutableText {
        val id = Registries.ITEM.getId(this)
        val key = "item.${id.namespace}.${id.path}.flavor.desc"
        val text = AcText.translatable(key).formatted(Formatting.WHITE)
        if (text.string == key) return AcText.empty()
        return text
    }

    override fun flavorText(): MutableText{
        return flavorText
    }
    override fun flavorDescText(): MutableText{
        return flavorTextDesc
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, context)
        addFlavorText(tooltip, context)
    }

    override fun getAttributeModifier(): Pair<EntityAttribute,EntityAttributeModifier>{
        return Pair(attribute,modifier)
    }

    override fun getFlavorItem(): CopperWardItem {
        return this
    }

}
