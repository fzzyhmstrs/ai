package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects

import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

object ShockingEffect {
    fun shock(entity: Entity,owner: Entity?, processContext: ProcessContext){
        val world = entity.world
        if (world.time % 30 != 0L && entity.age > 20 && processContext.isBeforeRemoval()) return
        val pos = entity.pos.add(0.0,entity.height/2.0,0.0)
        val box = Box(pos.add(3.0,3.0,3.0),pos.subtract(3.0,3.0,3.0))
        val entities = world.getOtherEntities(owner, box)
        for (target in entities){
            if (target is SpellCastingEntity && owner is LivingEntity && AiConfig.entities.isEntityPvpTeammate(owner, target)) continue
            if (target !is LivingEntity) continue
            if (owner is PlayerEntity) {
                target.damage(entity.damageSources.playerAttack(owner), 3f)
            } else {
                target.damage(entity.damageSources.generic(), 3f)
            }
            if (processContext.isDouble()){
                if (world.random.nextFloat() < 0.05f){
                    target.addStatusEffect(StatusEffectInstance(RegisterStatus.STUNNED,60))
                }
            }
            if (world is ServerWorld){
                beam(pos,world,target)
            }
            world.playSound(null,entity.blockPos, SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.PLAYERS,0.3f,2.0f + world.random.nextFloat() * 0.4f - 0.2f)
        }
        if (processContext.isBeforeRemoval())
            world.playSound(null,entity.blockPos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS,0.3f,2.0f + world.random.nextFloat() * 0.4f - 0.2f)
    }

    //entity is the player in this case
    fun staticShock(entity: Entity, owner: Entity?, processContext: ProcessContext){
        val world = entity.world
        val pos = entity.pos.add(0.0,entity.height/2.0,0.0)
        val box = Box(pos.add(3.0,3.0,3.0),pos.subtract(3.0,3.0,3.0))
        if (entity !is LivingEntity) return
        val target = world.getClosestEntity(LivingEntity::class.java, TargetPredicate.DEFAULT, entity,pos.x,pos.y,pos.z, box)
        if (target is SpellCastingEntity && owner is LivingEntity && AiConfig.entities.isEntityPvpTeammate(owner, target)) return
        if (target !is LivingEntity) return
        if (entity is PlayerEntity) {
            target.damage(entity.damageSources.playerAttack(entity), 5f)
        } else {
            target.damage(entity.damageSources.generic(), 5f)
        }
        if (world.random.nextFloat() < 0.1f){
            target.addStatusEffect(StatusEffectInstance(RegisterStatus.STUNNED,60))
        }
        if (world is ServerWorld){
            beam(pos,world,target)
        }
        world.playSound(null,entity.blockPos, SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.PLAYERS,0.3f,2.0f + world.random.nextFloat() * 0.4f - 0.2f)
    }

    //entity is the player in this case
    //owner is a Target in this case
    fun chainLightning(entity: Entity, owner: Entity?, processContext: ProcessContext){

        val world = entity.world
        if (world.time % 30 != 0L && entity.age > 20 && processContext.isBeforeRemoval()) return
        val pos = entity.pos.add(0.0,entity.height/2.0,0.0)
        val box = Box(pos.add(3.0,3.0,3.0),pos.subtract(3.0,3.0,3.0))
        val entities = world.getEntitiesByClass(LivingEntity::class.java,box) {e -> e !== entity && e !== owner}
        if (entities.isEmpty()) return
        val chains = world.random.nextInt(entities.size)
        for (i in 0..chains){
            val target = entities[i]
            if (entity is PlayerEntity) {
                target.damage(entity.damageSources.playerAttack(entity), 5f)
            } else {
                target.damage(entity.damageSources.generic(), 5f)
            }
            if (world.random.nextFloat() < 0.2f){
                target.addStatusEffect(StatusEffectInstance(RegisterStatus.STUNNED,60))
            }
            if (world is ServerWorld){
                beam(pos,world,target)
            }
        }
        world.playSound(null,entity.blockPos, SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.PLAYERS,0.3f,2.0f + world.random.nextFloat() * 0.4f - 0.2f)
    }


    private fun beam(pos: Vec3d,serverWorld: ServerWorld, entity: LivingEntity){
        val startPos = pos.add(0.0,0.25,0.0)
        val endPos = entity.pos.add(0.0,entity.height/2.0,0.0)
        val vec = endPos.subtract(startPos).multiply(0.1)
        var curPos = startPos
        for (i in 1..6){
            serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK,curPos.x,curPos.y,curPos.z,2,vec.x,vec.y,vec.z,0.0)
            curPos = curPos.add(vec)
        }

    }
}