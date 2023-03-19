package me.fzzyhmstrs.amethyst_imbuement.augment

import dev.emi.trinkets.api.TrinketEnums
import dev.emi.trinkets.api.event.TrinketDropCallback
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.TotemPassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack

class SoulbindingAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): TotemPassiveAugment(weight,mxLvl, *slot) {

    override fun tickEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        EffectQueue.addStatusToQueue(user, RegisterStatus.SOULBINDING, 260, 0)
    }

    init{
        TrinketDropCallback.EVENT.register{rule,_,_,entity->
            if (entity.hasStatusEffect(RegisterStatus.SOULBINDING)){
                TrinketEnums.DropRule.KEEP
            } else {
                rule
            }
        }
    }

}