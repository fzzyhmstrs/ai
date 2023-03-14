package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.boss.WitherEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World
import kotlin.math.min

class BedazzleAugment: MiscAugment(ScepterTier.TWO,1) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(13.0,1.0)
            .withDuration(1200,0,0)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT,1500,85,
            5, imbueLevel,40, LoreTier.LOW_TIER, Items.DIAMOND)
    }

    override fun effect(world: World, target: Entity?, user: LivingEntity, level: Int, hit: HitResult?, effect: AugmentEffect): Boolean {
        val (_,entityList) = RaycasterUtil.raycastEntityArea(user,hit,effect.range(level))
        if (entityList.size <= 1) return false
        if (effect(world, user, entityList, level, effect)) {
            world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.5F, 1.0F)
        } else {
            return false
        }
        return true
    }

    override fun effect(
        world: World,
        user: LivingEntity,
        entityList: MutableList<Entity>,
        level: Int,
        effect: AugmentEffect
    ): Boolean {
        val hostileEntity: MutableList<LivingEntity> = mutableListOf()
        for (entity2 in entityList){
            if (entity2 is HostileEntity && entity2 !is WitherEntity){
                hostileEntity.add(entity2)
            }
        }
        val merchantEntity: MutableList<LivingEntity> = mutableListOf()
        for (entity2 in entityList){
            if (entity2 is VillagerEntity){
                merchantEntity.add(entity2)
            }
        }
        if (hostileEntity.isEmpty() && merchantEntity.isEmpty()) return false

        val (checkList,levelRemaining) = getRndEntityList(world,hostileEntity,level)

        if (checkList.isNotEmpty()) {
            for (entity in checkList) {
                entityTask(world,entity,user,level.toDouble(),null, effect)
            }
            effect.accept(checkList, AugmentConsumer.Type.HARMFUL)
        }

        if (merchantEntity.isNotEmpty()){
            if (levelRemaining > 0) {
                val (checkList2, _) = getRndEntityList(world,merchantEntity,levelRemaining)
                if (checkList.isNotEmpty()) {
                    for (entity in checkList2) {
                        entityTask(world,entity,user,level.toDouble(),null,effect)
                    }
                }
                effect.accept(checkList2,AugmentConsumer.Type.BENEFICIAL)
                user.addStatusEffect(StatusEffectInstance(StatusEffects.HERO_OF_THE_VILLAGE, effect.duration(level)))
            }
        }
        effect.accept(user,AugmentConsumer.Type.BENEFICIAL)
        return true
    }

    override fun entityTask(
        world: World,
        target: Entity,
        user: LivingEntity,
        level: Double,
        hit: HitResult?,
        effects: AugmentEffect
    ) {
        if (target !is LivingEntity) return
        val duration = if (target is VillagerEntity){
            effects.duration(level.toInt())
        } else {
            effects.duration(level.toInt())/2
        }
        EffectQueue.addStatusToQueue(target,RegisterStatus.CHARMED,duration,0)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.EVENT_RAID_HORN
    }

    private fun getRndEntityList(world: World, list: MutableList<LivingEntity>, level: Int): Pair<MutableList<LivingEntity>,Int>{
        if (list.isNotEmpty()){
            val listTmp: MutableList<LivingEntity> = mutableListOf()
            val startSize = min(level, list.size)
            var remaining = min(level, list.size)
            val leftOver = if(list.size < level) {
                level - list.size
            } else {
                0
            }
            for (i in 1..startSize) {
                val rnd = world.random.nextInt(remaining)
                val ent = list[rnd]
                listTmp.add(ent)
                list.remove(ent)
                remaining--
            }
            return Pair(listTmp,leftOver)
        } else {
            return Pair(list,level)
        }
    }
}