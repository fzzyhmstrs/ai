package me.fzzyhmstrs.amethyst_imbuement.entity.monster

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents

class Calcify {

    companion object{
        val INSTANCE = Calcify()
    }

    fun physicalCalcify(sardonyxElementalEntity: SardonyxElementalEntity){
        sardonyxElementalEntity.world.playSound(null, sardonyxElementalEntity.blockPos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(), SoundCategory.HOSTILE, 1.0F, 1.0F)
        sardonyxElementalEntity.addStatusEffect(StatusEffectInstance(RegisterStatus.BONE_ARMOR,400, 3))
        sardonyxElementalEntity.addStatusEffect(StatusEffectInstance(StatusEffects.RESISTANCE,400, 0))
        sardonyxElementalEntity.addStatusEffect(StatusEffectInstance(RegisterStatus.SOUL_SHIELD,400, 1))
    }

    fun magicCalcify(sardonyxElementalEntity: SardonyxElementalEntity){
        sardonyxElementalEntity.world.playSound(null, sardonyxElementalEntity.blockPos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(), SoundCategory.HOSTILE, 1.0F, 1.0F)
        sardonyxElementalEntity.addStatusEffect(StatusEffectInstance(RegisterStatus.SOUL_SHIELD,400, 4))
        sardonyxElementalEntity.addStatusEffect(StatusEffectInstance(StatusEffects.RESISTANCE,400, 0))
        sardonyxElementalEntity.addStatusEffect(StatusEffectInstance(RegisterStatus.BONE_ARMOR,400, 1))
    }

    fun emergencyCalcify(sardonyxElementalEntity: SardonyxElementalEntity){
        sardonyxElementalEntity.world.playSound(null, sardonyxElementalEntity.blockPos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(), SoundCategory.HOSTILE, 1.0F, 1.0F)
        sardonyxElementalEntity.addStatusEffect(StatusEffectInstance(RegisterStatus.SOUL_SHIELD,400, 4))
        sardonyxElementalEntity.addStatusEffect(StatusEffectInstance(StatusEffects.RESISTANCE,400, 1))
        sardonyxElementalEntity.addStatusEffect(StatusEffectInstance(RegisterStatus.BONE_ARMOR,400, 3))
    }
}