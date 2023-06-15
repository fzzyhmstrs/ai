package me.fzzyhmstrs.amethyst_imbuement.augment

import dev.emi.trinkets.api.TrinketEnums
import dev.emi.trinkets.api.event.TrinketDropCallback
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.TotemPassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack

class AccursedAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): TotemPassiveAugment(weight,mxLvl, *slot) {

    fun accursedEffect(user: LivingEntity, attacker: LivingEntity, level: Int, stack: ItemStack) {
        val lvl = EnchantmentHelper.getLevel(RegisterEnchantment.ACCURSED, stack)
        if (lvl == 0) return
        if (user.world.random.nextFolat() > (0.1 * lvl)) return
        if (!RegisterItem.TOTEM_OF_AMETHYST.checkCanUse(stack,user.world,user,5)) return
        if (RegisterItem.TOTEM_OF_AMETHYST.manaDamage(stack, user.world, user, 5)) {
            if (AiConfig.trinkets.enableBurnout.get()) {
                RegisterItem.TOTEM_OF_AMETHYST.burnOutHandler(
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
        target.addStatusEffect(StatusEffectInstance(RegisterStatus.CURSED, duration, amp + 1)
    }

}
