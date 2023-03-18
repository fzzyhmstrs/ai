package me.fzzyhmstrs.amethyst_imbuement.item.promise

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterCriteria
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.item_util.interfaces.Flavorful
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.registry.Registry
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
