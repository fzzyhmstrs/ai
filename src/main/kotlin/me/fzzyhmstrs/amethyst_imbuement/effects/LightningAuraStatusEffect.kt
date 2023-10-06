package me.fzzyhmstrs.amethyst_imbuement.effects

import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.scepter_util.CustomDamageSources
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.AttributeContainer
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.Box

class LightningAuraStatusEffect(statusEffectCategory: StatusEffectCategory, i: Int): StatusEffect(statusEffectCategory, i), Aura {

    override fun onApplied(entity: LivingEntity, attributes: AttributeContainer, amplifier: Int) {
        super.onApplied(entity, attributes, amplifier)
        val statuses = entity.statusEffects.filter { it.effectType is Aura && it.effectType !is LightningAuraStatusEffect }
        for (status in statuses){
            entity.removeStatusEffect(status.effectType)
        }
    }

    override fun canApplyUpdateEffect(duration: Int, amplifier: Int): Boolean {
        if (amplifier < 0) return false
        return duration % (200/(1 + amplifier)) == 0
    }

    override fun applyUpdateEffect(entity: LivingEntity, amplifier: Int) {
        val world = entity.world as? ServerWorld ?: return
        val box = Box(entity.pos.add(6.0,6.0,6.0),entity.pos.subtract(6.0,6.0,6.0))
        val entities = world.getOtherEntities(entity, box) { !((it is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(entity, it, RegisterEnchantment.LIGHTNING_AURA)) || it is PassiveEntity) }
        if (entities.isNotEmpty())
            world.playSound(null,entity.blockPos, SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.NEUTRAL,0.2f,1.0f)
        for (e in entities){
            e.damage(CustomDamageSources.lightningBolt(world,null,entity),3f)
            beam(world,entity,e)
        }
    }

    private fun beam(serverWorld: ServerWorld, entity: Entity, target: Entity){
        val startPos = entity.pos.add(0.0,entity.height/2.0,0.0)
        val endPos = target.pos.add(0.0,target.height/2.0,0.0)
        val vec = endPos.subtract(startPos).multiply(0.1)
        var pos = startPos
        for (i in 1..10){
            serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK,pos.x,pos.y,pos.z,5,vec.x,vec.y,vec.z,0.0)
            pos = pos.add(vec)
        }

    }
}