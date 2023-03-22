package me.fzzyhmstrs.amethyst_imbuement.entity.totem

import me.fzzyhmstrs.amethyst_core.entity_util.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.fzzy_core.registry.EventRegistry
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.Box
import net.minecraft.world.World

class TotemOfWitEntity(entityType: EntityType<out TotemOfWitEntity>, world: World, summoner: PlayerEntity? = null, maxAge: Int = 600):
    AbstractEffectTotemEntity(entityType, world, summoner, maxAge, RegisterItem.INQUISITIVE_GEM), ModifiableEffectEntity {

    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(2.0F).withRange(5.0)

    override fun passEffects(ae: AugmentEffect, level: Int) {
        super.passEffects(ae, level)
        entityEffects.setRange(ae.range(level))
        entityEffects.setAmplifier(ae.amplifier(level)/3)
    }

    override fun initTicker(): EventRegistry.Ticker {
        val ticker = EventRegistry.Ticker(200)
        EventRegistry.registerTickUppable(ticker)
        return ticker
    }

    override fun totemEffect() {
        if (summoner == null) return
        val range = entityEffects.range(0)
        val box = Box(this.pos.add(range,range,range),this.pos.subtract(range,range,range))
        val entities = world.getOtherEntities(this, box)
        for (entity in entities){
            if (entity !is LivingEntity) continue
            if (entity is PassiveEntity || entity is GolemEntity || entity is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(summoner,entity,
                    RegisterEnchantment.SUMMON_WIT_TOTEM)) {
                entity.addStatusEffect(StatusEffectInstance(StatusEffects.JUMP_BOOST,240,entityEffects.amplifier(0)))
                entity.addStatusEffect(StatusEffectInstance(StatusEffects.SPEED,240,entityEffects.amplifier(0)))
                val serverWorld: ServerWorld = this.world as ServerWorld
                beam(entity,serverWorld)
            }
        }
        world.playSound(null,this.blockPos,SoundEvents.BLOCK_CONDUIT_AMBIENT,SoundCategory.NEUTRAL,0.5f,1.0f)
    }

    private fun beam(entity: Entity, serverWorld: ServerWorld){
        val startPos = this.pos.add(0.0,1.2,0.0)
        val endPos = entity.pos.add(0.0,1.2,0.0)?:startPos.subtract(0.0,2.0,0.0)
        val vec = endPos.subtract(startPos).multiply(0.1)
        var pos = startPos
        for (i in 1..12){
            serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER,pos.x,pos.y,pos.z,3,vec.x,vec.y,vec.z,0.0)
            pos = pos.add(vec)
        }

    }

    companion object Stats: TotemAbstractAttributes{
        override fun createTotemAttributes(): DefaultAttributeContainer.Builder {
            return createBaseTotemAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH,14.0)
                .add(EntityAttributes.GENERIC_ARMOR,4.0)
        }
    }

}
