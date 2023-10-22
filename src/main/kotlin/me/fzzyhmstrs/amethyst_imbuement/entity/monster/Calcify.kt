package me.fzzyhmstrs.amethyst_imbuement.entity.monster

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents

class Calcify {

    companion object{
        val INSTANCE = Calcify()
        private val searchArray = intArrayOf(0,1,-1,2,-2,3,-3)
    }

    fun physicalCalcify(sardonyxElementalEntity: SardonyxElementalEntity){
        sardonyxElementalEntity.world.playSound(null, sardonyxElementalEntity.blockPos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.HOSTILE, 1.0F, 1.0F)
        sardonyxElementalEntity.addStatusEffect(StatusEffectInstance(RegisterStatus.BONE_ARMOR,400, 3))
    }

    fun magicCalcify(sardonyxElementalEntity: SardonyxElementalEntity){
        sardonyxElementalEntity.world.playSound(null, sardonyxElementalEntity.blockPos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(), SoundCategory.HOSTILE, 1.0F, 1.0F)
        sardonyxElementalEntity.addStatusEffect(StatusEffectInstance(RegisterStatus.SOUL_SHIELD,400, 4))
    }
}