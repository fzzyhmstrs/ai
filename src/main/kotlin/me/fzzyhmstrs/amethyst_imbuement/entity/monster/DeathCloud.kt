package me.fzzyhmstrs.amethyst_imbuement.entity.monster


import net.minecraft.entity.AreaEffectCloudEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents

class DeathCloud {

    companion object{
        val INSTANCE = DeathCloud()
    }

    fun cloud(sardonyxElementalEntity: SardonyxElementalEntity){
        sardonyxElementalEntity.world.playSound(null,sardonyxElementalEntity.blockPos, SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.PLAYERS,1f,1f)
        val aece = AreaEffectCloudEntity(sardonyxElementalEntity.world,sardonyxElementalEntity.x,sardonyxElementalEntity.y + 0.25,sardonyxElementalEntity.z)
        aece.owner = sardonyxElementalEntity
        aece.addEffect(StatusEffectInstance(StatusEffects.INSTANT_DAMAGE,1,2))
        aece.addEffect(StatusEffectInstance(StatusEffects.SLOWNESS,200,2))
        aece.particleType = ParticleTypes.SMOKE
        aece.duration = 400
        aece.radius = 5f
        aece.radiusGrowth = 0.005f
        sardonyxElementalEntity.world.spawnEntity(aece)
    }
}