package me.fzzyhmstrs.amethyst_imbuement.item.promise

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterCriteria
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterModifier
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

class LethalGemItem(settings: Settings): IgnitedGemItem(settings) {

    private val KILL_TARGET by lazy{
        AiConfig.items.gems.killTarget.get()
    }

    override fun giveTooltipHint(nbt: NbtCompound, stack: ItemStack, tooltip: MutableList<Text>){
        if (nbt.contains("kill_count")){
            val kills = nbt.getInt("kill_count").toFloat()
            val progress = kills/ KILL_TARGET.toFloat()*100.0F
            tooltip.add(AcText.translatable("item.amethyst_imbuement.gem_of_promise.lethal", progress).formatted(Formatting.DARK_RED))
        }
    }

    override fun getModifier(): Identifier {
        return RegisterModifier.DANGEROUS.modifierId
    }

    fun lethalGemCheck(stack: ItemStack, inventory: PlayerInventory){
            val nbt = stack.orCreateNbt
            var kills = 0
            if (nbt.contains("kill_count")){
                kills = nbt.getInt("kill_count")
            }
            val newCount = kills + 1
            if (newCount >= KILL_TARGET){
                stack.decrement(1)
                val newStack = ItemStack(RegisterItem.LETHAL_GEM)
                inventory.offerOrDrop(newStack)
                val player = inventory.player
                if (player is ServerPlayerEntity) {
                    RegisterCriteria.IGNITE.trigger(player)
                }
            } else {
                nbt.putInt("kill_count", newCount)
            }
        }

}
