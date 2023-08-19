package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.TotemPassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTool
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.item.ItemStack

class AccursedAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): TotemPassiveAugment(weight,mxLvl, *slot) {

    fun accursedEffect(user: LivingEntity, attacker: LivingEntity, level: Int, stack: ItemStack) {
        if (user.world.random.nextFloat() > (0.1 * level)) return
        if (RegisterTool.TOTEM_OF_AMETHYST.manaDamage(stack, user.world, user, 1)) {
            if (AiConfig.trinkets.enableBurnout.get()) {
                RegisterTool.TOTEM_OF_AMETHYST.burnOutHandler(
                    stack,
                    RegisterEnchantment.ACCURSED,
                    user,
                    AcText.translatable("augment_damage.accursed.burnout")
                )
            }
        }
        val effect = attacker.getStatusEffect(RegisterStatus.CURSED)
        val amp = effect?.amplifier?:-1
        var duration = effect?.duration?:160
        if (duration < 160) duration = 160
        attacker.addStatusEffect(StatusEffectInstance(RegisterStatus.CURSED, duration, amp + 1))
    }

}
