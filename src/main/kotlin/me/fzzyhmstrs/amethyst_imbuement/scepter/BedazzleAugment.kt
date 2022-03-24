package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.BaseAugment
import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem
import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.util.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
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
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World
import kotlin.math.min

class BedazzleAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier, maxLvl, *slot) {

    override fun effect(world: World, target: Entity?, user: LivingEntity, level: Int, hit: HitResult?): Boolean {
        val entityList: MutableList<Entity> = if (hit == null) {
            RaycasterUtil.raycastEntityArea(rangeOfEffect() + 1.0 * level)
        } else if (hit.type == HitResult.Type.MISS){
            RaycasterUtil.raycastEntityArea(rangeOfEffect() + 1.0 * level)
        } else if (hit.type == HitResult.Type.BLOCK){
            RaycasterUtil.raycastEntityArea(rangeOfEffect() + 1.0 * level,(hit as BlockHitResult).pos)
        } else if (hit.type == HitResult.Type.ENTITY){
            RaycasterUtil.raycastEntityArea(rangeOfEffect() + 1.0 * level,(hit as EntityHitResult).entity.pos)
        } else {
            RaycasterUtil.raycastEntityArea(rangeOfEffect() + 1.0 * level)
        }
        if (entityList.size <= 1) return false
        effect(world, user, entityList, level)
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.5F, 1.0F)
        return true
    }

    override fun effect(world: World, user: LivingEntity, entityList: MutableList<Entity>, level: Int): Boolean {
        val hostileEntity: MutableList<Entity> = mutableListOf()
        for (entity2 in entityList){
            if (entity2 is HostileEntity && entity2 !is WitherEntity){
                hostileEntity.add(entity2)
            }
        }
        val merchantEntity: MutableList<Entity> = mutableListOf()
        for (entity2 in entityList){
            if (entity2 is VillagerEntity){
                merchantEntity.add(entity2)
            }
        }
        if (hostileEntity.isEmpty() && merchantEntity.isEmpty()) return false

        val checkListPair = getRndEntityList(world,hostileEntity,level)
        val checkList = checkListPair.first

        if (checkList.isNotEmpty()) {
            for (entity in checkList) {
                //entity.damage(DamageSource.mob(entity3 as LivingEntity), 0.5f)
                ScepterObject.addEntityToQueue(
                    entity.uuid,
                    ScepterItem.EntityTaskInstance(RegisterEnchantment.BEDAZZLE, user, level.toDouble(), null)
                )
            }
        }

        if (merchantEntity.isNotEmpty()){
            if (checkListPair.second > 0) {
                val checkListPair2 = getRndEntityList(world,merchantEntity,checkListPair.second)
                val checkList2 = checkListPair2.first
                if (checkList.isNotEmpty()) {
                    for (entity in checkList2) {
                        //entity.damage(DamageSource.mob(entity3 as LivingEntity), 0.5f)
                        ScepterObject.addEntityToQueue(
                            entity.uuid,
                            ScepterItem.EntityTaskInstance(RegisterEnchantment.BEDAZZLE, user, level.toDouble(), null)
                        )
                    }
                }
                user.addStatusEffect(StatusEffectInstance(StatusEffects.HERO_OF_THE_VILLAGE, 1200))
            }
        }
        return true
    }

    override fun entityTask(world: World, target: Entity, user: LivingEntity, level: Double, hit: HitResult?) {
        val duration = if (target is VillagerEntity){
            1200
        } else {
            600
        }
        BaseAugment.addStatusToQueue(target.uuid,RegisterStatus.CHARMED,duration,0,true)
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.WIT,1800,75,5,imbueLevel,1, Items.DIAMOND)
    }

    override fun rangeOfEffect(): Double {
        return 13.0
    }

    override fun raycastHitRange(): Double {
        return 18.0
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.EVENT_RAID_HORN
    }

    private fun getRndEntityList(world: World, list: MutableList<Entity>, level: Int): Pair<MutableList<Entity>,Int>{
        if (list.isNotEmpty()){
            val listTmp: MutableList<Entity> = mutableListOf()
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