package me.fzzyhmstrs.amethyst_imbuement.item

import net.minecraft.block.Block
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.item.BlockItem

class SpellcastersReagentBlockItem(
    private val attribute: EntityAttribute,
    private val modifier: EntityAttributeModifier,
    block: Block,
    settings: Settings)
    : 
    BlockItem(block,settings), SpellcastersReagent, Flavorful<SpellcastersReagentBlockItem>
{

    override fun getAttributeModifier(): Pair<EntityAttribute,EntityAttributeModifier>{
        return Pair(attribute,modifier)
    }

    override var glint = false
    override var flavor: String = ""
    override var flavorDesc: String = ""
    
    private val flavorText: MutableText by lazy{
        makeFlavorText()
    }
    
    private val flavorTextDesc: MutableText by lazy{
        makeFlavorTextDesc()
    }
    
    private fun makeFlavorText(): MutableText{
        val id = Registries.ITEM.getId(this)
        val key = "item.${id.namespace}.${id.path}.flavor"
        val text = AcText.translatable(key).formatted(Formatting.WHITE, Formatting.ITALIC)
        if (text.string == key) return AcText.empty()
        return text
    }
    
    private fun makeFlavorTextDesc(): MutableText{
        val id = Registries.ITEM.getId(this)
        val key = "item.${id.namespace}.${id.path}.flavor.desc"
        val text = AcText.translatable(key).formatted(Formatting.WHITE)
        if (text.string == key) return AcText.empty()
        return text
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)
        addFlavorText(tooltip, context)
    }

    override fun hasGlint(stack: ItemStack): Boolean {
        return if (glint) {
            true
        } else {
            super.hasGlint(stack)
        }
    }
    
    override fun flavorText(): MutableText{
        return flavorText
    }
    override fun flavorDescText(): MutableText{
        return flavorTextDesc
    }

    override fun getFlavorItem(): CustomFlavorItem {
        return this
    }

}
