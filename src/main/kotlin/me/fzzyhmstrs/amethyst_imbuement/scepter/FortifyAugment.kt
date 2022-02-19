package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MinorSupportAugment
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class FortifyAugment(weight: Rarity, tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MinorSupportAugment(weight,tier,maxLvl, *slot) {

    override fun supportEffect(world: World, target: Entity?, user: LivingEntity?, level: Int): Boolean {
        if(target != null) {
            if (target is PassiveEntity || target is GolemEntity || target is PlayerEntity) {
                (target as LivingEntity).addStatusEffect(StatusEffectInstance(StatusEffects.RESISTANCE, 400 * level, level - 1))
                target.addStatusEffect(StatusEffectInstance(StatusEffects.STRENGTH, 400 * level, level - 1))
                world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.6F, 1.2F)
                return true
            }
        }
        return if(user != null){
            if (user is PlayerEntity) {
                user.addStatusEffect(StatusEffectInstance(StatusEffects.RESISTANCE, 400 * level, level - 1))
                user.addStatusEffect(StatusEffectInstance(StatusEffects.STRENGTH, 400 * level, level - 1))
                world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.6F, 1.2F)
                true
            } else {
                false
            }
        } else {
            false
        }
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN
    }
}