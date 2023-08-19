package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.ActiveAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.TotemItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTool
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack

class HastingAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): ActiveAugment(weight,mxLvl,*slot) {

    override fun canActivate(user: LivingEntity, level: Int, stack: ItemStack): Boolean {
        return RegisterTool.TOTEM_OF_AMETHYST.checkCanUse(stack,user.world,user,10)
    }

    override fun activateEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        val lvl = EnchantmentHelper.getLevel(RegisterEnchantment.HASTING,stack)
        EffectQueue.addStatusToQueue(user, StatusEffects.HASTE,100,lvl-1)
        val rnd = user.world.random.nextFloat()
        if (rnd <= 0.25) {
            if (RegisterTool.TOTEM_OF_AMETHYST.manaDamage(stack, user.world, user as PlayerEntity, 1)) {
                if (AiConfig.trinkets.enableBurnout.get()) {
                    RegisterTool.TOTEM_OF_AMETHYST.burnOutHandler(
                        stack,
                        RegisterEnchantment.HASTING,
                        user,
                        AcText.translatable("augment_damage.hasting.burnout")
                    )
                } else {
                    val item = stack.item
                    if (item is TotemItem){
                        val nbt = stack.orCreateNbt
                        item.inactiveEnchantmentTasks(stack,user.world,user)
                        nbt.putBoolean(NbtKeys.TOTEM.str(), false)
                    }
                }
            }
        }
    }

}