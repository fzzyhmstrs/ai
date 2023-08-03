package me.fzzyhmstrs.amethyst_imbuement.augment

import dev.emi.trinkets.api.TrinketEnums
import dev.emi.trinkets.api.event.TrinketDropCallback
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.TotemPassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTool
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack

class SoulbindingAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): TotemPassiveAugment(weight,mxLvl, *slot) {

    override fun tickEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        if (RegisterTool.TOTEM_OF_AMETHYST.checkCanUse(stack,user.world,user,90,AcText.empty()))
            EffectQueue.addStatusToQueue(user, RegisterStatus.SOULBINDING, 260, 0)
    }

    override fun specialEffect(user: LivingEntity, level: Int, stack: ItemStack): Boolean {
        if (!RegisterTool.TOTEM_OF_AMETHYST.checkCanUse(stack,user.world,user,90)) {
            user.removeStatusEffect(RegisterStatus.SOULBINDING)
            return false
        }
        stack.orCreateNbt.putBoolean("cloneInventory",true)
        if (RegisterTool.TOTEM_OF_AMETHYST.manaDamage(stack, user.world, user, 90)) {
            if (AiConfig.trinkets.enableBurnout.get()) {
                RegisterTool.TOTEM_OF_AMETHYST.burnOutHandler(
                    stack,
                    RegisterEnchantment.SOULBINDING,
                    user,
                    AcText.translatable("augment_damage.soulbinding.burnout")
                )
            }
        }
        return true
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