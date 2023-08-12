package me.fzzyhmstrs.amethyst_imbuement.spells.pieces

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffect
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.interfaces.ModifiableEffectMobOrPlayer
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World
import kotlin.math.min

object SpellHelper {

    val CHICKEN = AI.identity("chicken")

    fun friendlyTarget(target: Entity, user: LivingEntity, spell: ScepterAugment): Boolean{
        return  target !is Monster && (target is PassiveEntity || target is GolemEntity || AiConfig.entities.isEntityPvpTeammate(user,target,spell))
    }

    fun hostileTarget(target: Entity, user: LivingEntity, spell: ScepterAugment): Boolean{
        return (target is Monster || !AiConfig.entities.isEntityPvpTeammate(user,target,spell))
    }

    fun hostileFilter(list: List<Entity>, user: LivingEntity, spell: ScepterAugment): MutableList<EntityHitResult> {
        val hostileEntityList: MutableList<EntityHitResult> = mutableListOf()
        if (list.isNotEmpty()) {
            for (entity in list) {
                if (entity !== user) {
                    if (entity is PlayerEntity && !spell.getPvpMode()) continue
                    if (entity is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(user,entity,spell)) continue
                    hostileEntityList.add(EntityHitResult(entity))
                }
            }
        }
        return hostileEntityList
    }

    fun friendlyFilter(list: List<Entity>, user: LivingEntity, spell: ScepterAugment): MutableList<EntityHitResult> {
        val friendlyEntityList: MutableList<EntityHitResult> = mutableListOf()
        if (list.isNotEmpty()) {
            for (entity in list) {
                if (entity !== user) {
                    if (entity is Monster) continue
                    if (entity is SpellCastingEntity && !AiConfig.entities.isEntityPvpTeammate(user,entity,spell)) continue
                    friendlyEntityList.add(EntityHitResult(entity))
                }
            }
        }
        return friendlyEntityList
    }

    fun getRndEntityList(world: World, list: MutableList<LivingEntity>, level: Int): MutableList<LivingEntity>{
        if (list.isNotEmpty()){
            val listTmp: MutableList<LivingEntity> = mutableListOf()
            val startSize = min(level, list.size)
            var remaining = min(level, list.size)
            for (i in 1..startSize) {
                val rnd = world.random.nextInt(remaining)
                val ent = list[rnd]
                listTmp.add(ent)
                list.remove(ent)
                remaining--
            }
            return listTmp
        } else {
            return list
        }
    }

    fun EntityHitResult.addStatus(effect: StatusEffect, duration: Int, amplifier: Int = 0): Boolean{
        val chk = this.entity
        if (chk is LivingEntity){
            return chk.addStatusEffect(StatusEffectInstance(effect,duration, amplifier))
        }
        return false
    }

    fun EntityHitResult.removeStatuses(vararg statuses: StatusEffect): Boolean{
        val chk = this.entity
        if (chk is LivingEntity){
            for (status in statuses){
                chk.removeStatusEffect(status)
            }
            return true
        }
        return false
    }

    fun EntityHitResult.addEffect(type: Identifier,effect: ModifiableEffect, duration: Int = -1): Boolean{
        val chk = this.entity
        if (chk is ModifiableEffectMobOrPlayer){
            chk.amethyst_imbuement_addTemporaryEffect(type, effect, duration)
            return true
        } else if (chk is ModifiableEffectEntity){
            chk.addTemporaryEffect(type, effect, duration)
            return true
        }
        return false
    }
    
}
