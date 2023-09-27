package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MinorSupportAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.mixins.MobEntityAccessor
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import me.fzzyhmstrs.fzzy_core.entity_util.PlayerCreatable
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.boss.WitherEntity
import net.minecraft.entity.boss.dragon.EnderDragonEntity
import net.minecraft.entity.mob.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class ChainLightningAugment: MinorSupportAugment(ScepterTier.THREE,9), PersistentEffectHelper.PersistentEffect{

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(75,5).withDamage(15.5f,0.5f).withRange(12.0).withAmplifier(6)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY, 200, 100,
            22, imbueLevel, 12, BookOfTalesItem.TALES_TIER, Items.COAL)
    }

    override fun supportEffect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
          return if (target is LivingEntity && (target is Monster || target is HostileEntity || target is SpellCastingEntity && !AiConfig.entities.isEntityPvpTeammate(user, target,this))) {
              val bl = target.damage(CustomDamageSources.lightningBolt(world,null,user), effects.damage(level))
              if (bl){
                  if (world.random.nextFloat() < 0.25f)
                      target.addStatusEffect(StatusEffectInstance(RegisterStatus.STUNNED, effects.duration(level))
                  val data = ChainLightningPersistentEffectData(target, user, listOf(target.uuid), effects.amplifier(level) - 1, effects.damage(level) - 1f, effects, level)
                  PersistentEffectHelper.setPersistentTickerNeed(this, 4, 4, data)
                  effects.accept(target,AugmentConsumer.Type.HARMFUL)
                  effects.accept(user, AugmentConsumer.Type.BENEFICIAL)
                  if (world is ServerWorld){
                      beam(world,user,target)
                  }
                
                  world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
              }
          } else {
              false
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

    override fun soundEvent(): SoundEvent {
        return SoundEvents.LIGHTNING_BOLT
    }

    override val delay: PerLvlI
        get() = PerLvlI()

    override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
        if (data !is ChainLightningPersistentEffectData) return
        val range = data.effects.range(data.level)
        val box = Box(data.entity.pos.add(range,range/2.0,range),data.entity.pos.subtract(range,range/2.0,range))
        val newEntities = world.getOtherEntities(data.entity, box) {(!data.struckEntities.contains(it.uuid)) && (it is LivingEntity && (it is Monster || it is HostileEntity || it is SpellCastingEntity && !AiConfig.entities.isEntityPvpTeammate(user, it, this))) && data.entity.canSee(it)}
        if (newEntities.isEmpty()) return
        val nextTarget = newEntities.random()
        if (nextTarget.damage(CustomDamageSources.lightningBolt(data.user.world,null,data.user), data.damage){
            if (data.user.world.random.nextFloat() < 0.25f)
                nextTarget.addStatusEffect(StatusEffectInstance(RegisterStatus.STUNNED, data.effects.duration(data.level))
            effects.accept(nextTarget,AugmentConsumer.Type.HARMFUL)
            data.user.world.playSound(null, nextTarget.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.75F, 1.0F)
            val serverWorld = data.user.world
            if (serverWorld is ServerWorld){
                beam(world,data.entity,nextTarget)
            }
            if (data.chainsLeft <= 0) return
            val newList = data.struckEntities.toMutableList()
            newList.add(nextTarget.uuid)
            val newData = ChainLightningPersistentEffectData(nextTarget, data.user, newList, data.chainsLeft - 1, data.damage - 1f,data.effects, data.level)
            PersistentEffectHelper.setPersistentTickerNeed(this, 4, 4, newData)
        }
    }

    class ChainLightningPersistentEffectData(val entity: LivingEntity, val user: LivingEntity, val struckEntities: List<UUID>, val chainsLeft: Int, val damage: Float, val effects: AugmentEffects, val level: Int): PersistentEffectHelper.PersistentEffectData
}
