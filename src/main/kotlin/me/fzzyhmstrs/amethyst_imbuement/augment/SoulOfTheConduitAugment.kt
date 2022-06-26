package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_core.trinket_util.EffectQueue
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.ActiveAugment
import me.fzzyhmstrs.amethyst_imbuement.item.TotemItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

class SoulOfTheConduitAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): ActiveAugment(weight,mxLvl,*slot) {

    override fun activateEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        if(user.isTouchingWaterOrRain){
            EffectQueue.addStatusToQueue(user,StatusEffects.CONDUIT_POWER,260,0)
            if (TotemItem.damageHandler(stack, user.world, user as PlayerEntity, 1)){
                TotemItem.burnOutHandler(
                    stack,
                    RegisterEnchantment.SOUL_OF_THE_CONDUIT,
                    user,
                    Text.translatable("augment_damage.soul_of_the_conduit.burnout").toString()
                )
            }
        }
    }

}