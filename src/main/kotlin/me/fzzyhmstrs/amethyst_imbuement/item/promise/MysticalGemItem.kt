package me.fzzyhmstrs.amethyst_imbuement.item.promise

import me.fzzyhmstrs.amethyst_core.event.AfterSpellEvent
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.Reagent
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterCriteria
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class MysticalGemItem(settings: Settings): IgnitedGemItem(settings), Reagent {
  
    private val SPELL_XP_TARGET by lazy{
        AiConfig.items.gems.spellXpTarget.get()
    }
    
    init{
        AfterSpellEvent.EVENT.register{ _, user, _, spell ->
            val offhand = user.offHandStack
            val item = offhand.item
            if (item is MysticalGemItem && user is PlayerEntity){
                mysticalGemCheck(offhand, spell, user)
            }
        }
    }

    override fun giveTooltipHint(nbt: NbtCompound, stack: ItemStack, tooltip: MutableList<Text>){
        if (nbt.contains("xp_cast")){
            val xp = nbt.getInt("xp_cast")
            val progress = xp/ SPELL_XP_TARGET * 100.0F
            tooltip.add(AcText.translatable("item.amethyst_imbuement.gem_of_promise.mystical", progress).formatted(Formatting.LIGHT_PURPLE))
        }
    }
    
    fun mysticalGemCheck(stack: ItemStack, spell: ScepterAugment, user: PlayerEntity){
            val nbt = stack.orCreateNbt
            val id = spell.id?.toString()?:return
            val newXp = AugmentHelper.getAugmentCastXp(id)
            var xp = 0
            if (nbt.contains("xp_cast")){
                xp = nbt.getInt("xp_cast")
            }
            val newCurrentXp = xp + newXp
            if (newCurrentXp >= SPELL_XP_TARGET){
                stack.decrement(1)
                val newStack = ItemStack(RegisterItem.MYSTICAL_GEM)
                user.inventory.offerOrDrop(newStack)
                if (user is ServerPlayerEntity) {
                    RegisterCriteria.IGNITE.trigger(user)
                }
            } else {
                nbt.putInt("xp_cast",newCurrentXp)
            }
        }

}
