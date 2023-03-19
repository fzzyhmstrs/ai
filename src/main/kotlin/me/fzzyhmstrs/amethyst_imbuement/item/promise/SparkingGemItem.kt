package me.fzzyhmstrs.amethyst_imbuement.item.promise

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterCriteria
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class SparkingGemItem(settings: Settings): IgnitedGemItem(settings) {

    override fun giveTooltipHint(nbt: NbtCompound, stack: ItemStack, tooltip: MutableList<Text>){
    }
    
    fun sparkingGemCheck(stack: ItemStack, inventory: PlayerInventory, damageSource: DamageSource){
            if (damageSource == DamageSource.LIGHTNING_BOLT){
                stack.decrement(1)
                val newStack = ItemStack(RegisterItem.SPARKING_GEM)
                inventory.offerOrDrop(newStack)
                val player = inventory.player
                if (player is ServerPlayerEntity) {
                    RegisterCriteria.IGNITE.trigger(player)
                }
            }
        }

}
