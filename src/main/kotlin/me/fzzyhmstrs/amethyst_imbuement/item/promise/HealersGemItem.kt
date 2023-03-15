package me.fzzyhmstrs.amethyst_imbuement.item.promise

import net.minecraft.item.Item
import net.minecraft.text.Text
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
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Formatting
import net.minecraft.world.World

class HealersGemItem(settings: Settings): IgnitedGemItem(settings) {
    
    private val HEAL_TARGET = 250.0F

    override fun giveTooltipHint(nbt: NbtCompound, stack: ItemStack, tooltip: MutableList<Text>){
        if (nbt.contains("healed")){
            val healed = nbt.getFloat("healed")
            val progress = healed/ HEAL_TARGET *100.0F
            tooltip.add(AcText.translatable("item.amethyst_imbuement.gem_of_promise.healers", progress).formatted(Formatting.GREEN))
        }
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
