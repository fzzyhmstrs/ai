package me.fzzyhmstrs.amethyst_imbuement.item.promise

import me.fzzyhmstrs.fzzy_core.item_util.interfaces.Flavorful
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World

class GemOfPromiseItem(settings: Settings): Item(settings), Flavorful<GemOfPromiseItem> {

    override var flavor: String = ""
    override var glint: Boolean = false
    override var flavorDesc: String = ""
    
    override fun getFlavorItem(): GemOfPromiseItem {
        return this
    }
    
    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, context)
        addFlavorText(tooltip, context)
        val nbt = stack.orCreateNbt
        for (gem in REGISTRY){
            gem.giveTooltipHint(nbt,stack,tooltip)
        }
    }

    companion object{
        
        private val REGISTRY: MutableList<IgnitedGemItem> = mutableListOf()
        
        fun register(gem: IgnitedGemItem){
            REGISTRY.add(gem)
        }
    }

}
