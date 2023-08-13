package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects

import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.Box

object FrostNovaEffect {

    fun nova(entity: Entity, attackerOrOwner: Entity?, processContext: ProcessContext){
        val world = entity.world
        val pos = entity.pos.add(0.0,entity.height/2.0,0.0)
        val box = Box(pos.add(2.0,2.0,2.0),pos.subtract(2.0,2.0,2.0))
        val entities = world.getOtherEntities(attackerOrOwner, box)
        if (world is ServerWorld){
            world.spawnParticles(ParticleTypes.SNOWFLAKE,pos.x,pos.y,pos.z,250,4.0,4.0,4.0,0.2)
        }
        world.playSound(null,pos.x,pos.y,pos.z, SoundEvents.ENTITY_PLAYER_HURT_FREEZE,SoundCategory.PLAYERS,1f,1f)
        for (target in entities){
            if (target !is LivingEntity) continue
            if ( target.damage(entity.damageSources.freeze(),2f)){
                entity.frozenTicks = 180
            }
        }
    }

}