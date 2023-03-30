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

class HealersGemItem(settings: Settings): IgnitedGemItem(settings) {
    
    private val HEAL_TARGET by lazy{
        AiConfig.items.gems.healTarget.get()
    }

    override fun giveTooltipHint(nbt: NbtCompound, stack: ItemStack, tooltip: MutableList<Text>){
        if (nbt.contains("healed")){
            val healed = nbt.getFloat("healed")
            val progress = healed/ HEAL_TARGET *100.0F
            tooltip.add(AcText.translatable("item.amethyst_imbuement.gem_of_promise.healers", progress).formatted(Formatting.GREEN))
        }
    }

    override fun getModifier(): Identifier {
        return RegisterModifier.SAINTS_GRACE.modifierId
    }

    fun healersGemCheck(stack: ItemStack, inventory: PlayerInventory, healAmount: Float){
            val nbt = stack.orCreateNbt
            val player = inventory.player
            if (!player.hungerManager.isNotFull){
                nbt.putBoolean("healed_with_food",true)
            }
            var healed = 0.0F
            if (nbt.contains("healed")){
                healed = nbt.getFloat("healed")
            }
            val newHeal = healed + healAmount
            if (newHeal >= HEAL_TARGET){
                stack.decrement(1)
                val newStack = ItemStack(RegisterItem.HEALERS_GEM)
                inventory.offerOrDrop(newStack)
                if (player is ServerPlayerEntity) {
                    RegisterCriteria.IGNITE.trigger(player)
                    if (!nbt.contains("healed_with_food")) {
                        RegisterCriteria.DEVOUT_CLERIC.trigger(player)
                    }
                }
            } else {
                nbt.putFloat("healed",newHeal)
            }
        }

}
