package me.fzzyhmstrs.amethyst_imbuement.entity.totem

import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.fzzy_core.registry.EventRegistry
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.Box
import net.minecraft.world.World

class TotemOfGraceEntity(entityType: EntityType<out TotemOfGraceEntity>, world: World, summoner: LivingEntity? = null, maxAge: Int = 600):
    AbstractEffectTotemEntity(entityType, world, summoner, maxAge, RegisterItem.HEALERS_GEM) {

    override fun initTicker(): EventRegistry.Ticker {
        val ticker = EventRegistry.Ticker(60)
        EventRegistry.registerTickUppable(ticker)
        return ticker
    }

    override fun totemEffect() {
        val summoner = owner ?: return
        val range = entityEffects.range(0)
        val box = Box(this.pos.add(range,range,range),this.pos.subtract(range,range,range))
        val entities = world.getOtherEntities(this, box)
        for (entity in entities){
            if (entity !is LivingEntity) continue
            if (entity is PassiveEntity || entity is GolemEntity || entity is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(summoner,entity,spells.primary())) {
                entity.heal(entityEffects.damage(0))
                val serverWorld: ServerWorld = this.world as ServerWorld
                beam(entity,serverWorld)
            }
        }
        
        world.playSound(null,this.blockPos,SoundEvents.BLOCK_CONDUIT_AMBIENT,SoundCategory.NEUTRAL,0.5f,1.0f)
    }

    private fun beam(target: Entity, serverWorld: ServerWorld){
        val startPos = this.pos.add(0.0,1.2,0.0)
        val endPos = target.pos.add(0.0,1.2,0.0)?:startPos.subtract(0.0,2.0,0.0)
        val vec = endPos.subtract(startPos).multiply(0.125)
        var pos = startPos
        for (i in 1..8){
            serverWorld.spawnParticles(ParticleTypes.HEART,pos.x,pos.y,pos.z,2,vec.x,vec.y,vec.z,0.0)
            pos = pos.add(vec)
        }

    }

    companion object Stats: TotemAbstractAttributes{
        override fun createTotemAttributes(): DefaultAttributeContainer.Builder {
            return createBaseTotemAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH,12.0)
        }
    }

}
