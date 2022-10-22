package me.fzzyhmstrs.amethyst_imbuement.entity.totem

import me.fzzyhmstrs.amethyst_core.entity_util.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.registry.EventRegistry
import me.fzzyhmstrs.amethyst_core.scepter_util.CustomDamageSources
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.Box
import net.minecraft.world.World

class TotemOfGraceEntity(entityType: EntityType<out TotemOfGraceEntity>, world: World, summoner: PlayerEntity? = null, maxAge: Int = 600):
    AbstractEffectTotemEntity(entityType, world, summoner, maxAge, RegisterItem.HEALERS_GEM), ModifiableEffectEntity {

    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(2.0F).withRange(5.0)

    override fun passEffects(ae: AugmentEffect, level: Int) {
        super.passEffects(ae, level)
        entityEffects.setDamage(ae.damage(level))
        entityEffects.setRange(ae.range(level))
    }

    override fun initTicker(): EventRegistry.Ticker {
        val ticker = EventRegistry.Ticker(60)
        EventRegistry.registerTickUppable(ticker)
        return ticker
    }

    override fun totemEffect() {
        println("effect")
        if (summoner == null) return
        if (summoner.distanceTo(this) > entityEffects.range(0)) return
        println("effect2")
        summoner.heal(entityEffects.damage(0))
        val serverWorld: ServerWorld = this.world as ServerWorld
        beam(serverWorld)
        world.playSound(null,this.blockPos,SoundEvents.BLOCK_CONDUIT_AMBIENT,SoundCategory.NEUTRAL,0.5f,1.0f)
    }

    private fun beam(serverWorld: ServerWorld){
        val startPos = this.pos.add(0.0,1.2,0.0)
        val endPos = summoner?.pos?.add(0.0,1.2,0.0)?:startPos.subtract(0.0,2.0,0.0)
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