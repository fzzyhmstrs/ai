package me.fzzyhmstrs.amethyst_imbuement.entity.monster

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents

class BlastBack {

    companion object{
        val INSTANCE = BlastBack()
    }

    fun blast(sardonyxElementalEntity: SardonyxElementalEntity){
        sardonyxElementalEntity.world.playSound(null, sardonyxElementalEntity.blockPos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.HOSTILE, 1.0F, 1.0F)
        sardonyxElementalEntity.addStatusEffect(StatusEffectInstance(RegisterStatus.BONE_ARMOR,400, 3))
    }
}