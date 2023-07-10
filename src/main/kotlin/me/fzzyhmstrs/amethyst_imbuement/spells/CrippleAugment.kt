package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.SlashAugment
import me.fzzyhmstrs.amethyst_core.augments.base.SlashAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.spells.ResonateAugment.Companion.NOTE_BLAST
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.world.World
import java.util.*

class CrippleAugment: SlashAugment(ScepterTier.TWO,13) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(3.4F,0.2F,0.0F)
            .withRange(7.75,0.25,0.0)
            .withDuration(110,10)
            .withAmplifier(-1,1,0)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,20,5,
            13,imbueLevel,1, LoreTier.NO_TIER, Items.STONE_SWORD)
    }
    
    override fun filter(list: List<Entity>, user: LivingEntity): MutableList<Entity>{
        val hostileEntityList: MutableList<Entity> = mutableListOf()
        if (list.isNotEmpty()) {
            for (entity in list) {
                if (entity !== user) {
                    if (entity is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(user, entity,this)) continue
                    hostileEntityList.add(entity)
                }
            }
        }
        return hostileEntityList
    }

    override fun effect(world: World, user: LivingEntity, entityList: MutableList<Entity>, level: Int, effect: AugmentEffect): Boolean {
        val entityDistance: SortedMap<Double, Entity> = mutableMapOf<Double, Entity>().toSortedMap()
        for (entity in entityList){
            if (entity is MobEntity){
                val dist = entity.squaredDistanceTo(user)
                entityDistance[dist] = entity
            }
        }
        var bl = false
        if (entityDistance.isNotEmpty()) {
            val entityDistance2 = entityDistance.toList()
            val entity1 = entityDistance2[0].second
            bl = critTarget(world,user,entity1,level, effect)
            var nextTarget = 1
            while (entityDistance.size > nextTarget && effect.amplifier(level) > nextTarget){
                val entity2 = entityDistance2[nextTarget].second
                bl = bl || critTarget(world, user, entity2, level, effect, true)
                nextTarget++
            }
            if (bl){
                effect.accept(user, AugmentConsumer.Type.BENEFICIAL)
            }
        }
        return bl
    }

    private fun critTarget(world: World,user: LivingEntity,target: Entity,level: Int,effect: AugmentEffect, splash: Boolean = false): Boolean{
        val crit = if (world.random.nextFloat() < 0.15) 2f else 1f
        val damage = if(!splash) {
            effect.damage(level) * crit
        } else {
            effect.damage(level)/2 * crit
        }
        val bl = target.damage(if (user is PlayerEntity) user.damageSources.playerAttack(user) else user.damageSources.mobAttack(user), damage)
        if (bl) {
            if (user is ServerPlayerEntity) {
                ServerPlayNetworking.send(user, NOTE_BLAST, ResonateAugment.writeBuf(user, target))
            }
            if (target is LivingEntity) {
                effect.accept(target, AugmentConsumer.Type.HARMFUL)
                val amp = if (crit > 1f) 4 else 1
                target.addStatusEffect(StatusEffectInstance(StatusEffects.WEAKNESS, effect.duration(level), amp))
                target.addStatusEffect(StatusEffectInstance(StatusEffects.SLOWNESS, effect.duration(level), amp))
                target.addStatusEffect(StatusEffectInstance(StatusEffects.JUMP_BOOST, effect.duration(level), (amp + 1) * -1))
            }
        }
        return bl
    }

    override fun clientTask(world: World, user: LivingEntity, hand: Hand, level: Int) {
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_PLAYER_BIG_FALL
    }

    override fun particleType(): DefaultParticleType {
        return ParticleTypes.ELECTRIC_SPARK
    }

    override fun particleSpeed(): Double {
        return 3.0
    }
}
