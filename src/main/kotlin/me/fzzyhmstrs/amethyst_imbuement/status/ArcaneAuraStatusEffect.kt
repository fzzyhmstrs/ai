package me.fzzyhmstrs.amethyst_imbuement.status

import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.scepter_util.CustomDamageSources
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.AttributeContainer
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.Box

class ArcaneAuraStatusEffect(statusEffectCategory: StatusEffectCategory, i: Int): StatusEffect(statusEffectCategory, i), Aura {

    override fun onApplied(entity: LivingEntity, attributes: AttributeContainer, amplifier: Int) {
        super.onApplied(entity, attributes, amplifier)
        val statuses = entity.statusEffects.filter { it.effectType is Aura && it.effectType !is ArcaneAuraStatusEffect }
        for (status in statuses){
            entity.removeStatusEffect(status.effectType)
        }
    }

    override fun canApplyUpdateEffect(duration: Int, amplifier: Int): Boolean {
        if (amplifier < 0) return false
        return duration % (200/(1 + amplifier)) == 0
    }

    override fun applyUpdateEffect(entity: LivingEntity, amplifier: Int) {
        if (entity !is PlayerEntity) return
        val stacks: MutableList<ItemStack> = mutableListOf()
        for (stack2 in entity.inventory.main){
            if (stack2.item is ManaItem && stack2.isDamaged){
                stacks.add(stack2)
            }
        } // iterate over the inventory and look for items that are interfaced with "ManaItem"
        for (stack2 in entity.inventory.offHand){
            if (stack2.item is ManaItem && stack2.isDamaged){
                stacks.add(stack2)
            }
        }
        for (stack2 in entity.inventory.armor){
            if (stack2.item is ManaItem && stack2.isDamaged){
                stacks.add(stack2)
            }
        }
        val stacks2 = TrinketUtil.getTrinketStacks(entity)
        stacks2.forEach {
            if (it.item is ManaItem && it.isDamaged){
                stacks.add(it)
            }
        }
        val leftOverMana = manaHealItems(stacks,world,healLeft)
        if (leftOverMana > 0) {
            user.addExperience(leftOverMana)
        }
    }

    private fun manaHealItems(list: MutableList<ItemStack>,world: World): Int{
        if (list.isEmpty()) return 1
        val stack = list.random()
        val healedAmount = (stack.item as ManaItem).healDamage(1,stack)
        if (healedAmount > 0) return 0 else 1
    }
}
