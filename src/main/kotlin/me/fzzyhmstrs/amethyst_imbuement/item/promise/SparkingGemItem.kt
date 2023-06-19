package me.fzzyhmstrs.amethyst_imbuement.item.promise

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterCriteria
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterModifier
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.tag.DamageTypeTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class SparkingGemItem(settings: Settings): IgnitedGemItem(settings) {

    override fun giveTooltipHint(nbt: NbtCompound, stack: ItemStack, tooltip: MutableList<Text>){
    }

    override fun getModifier(): Identifier {
        return RegisterModifier.DYNAMO.modifierId
    }

    fun sparkingGemCheck(stack: ItemStack, inventory: PlayerInventory, damageSource: DamageSource){
            if (damageSource.isIn(DamageTypeTags.IS_LIGHTNING) ){
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
