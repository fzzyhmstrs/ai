package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterNetworking
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.math.max
import kotlin.math.min

class GustingAugment: MiscAugment(ScepterTier.ONE,3){

    override val baseEffect: AugmentEffect = super.baseEffect
                                                .withAmplifier(2,1)
                                                .withRange(8.0,0.0)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT,60,15,
            1,imbueLevel,2,LoreTier.LOW_TIER, Items.FEATHER)
    }

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        val entityList = RaycasterUtil.raycastEntityArea(effect.range(level), user).filter { AiConfig.entities.shouldItHit(user,it,AiConfig.Entities.Options.NON_BOSS,this) }
        if (entityList.isEmpty()) return false
        var minDist = 10000000.0
        var maxDist = 0.0
        for (entity in entityList){
            val dist = entity.squaredDistanceTo(user)
            minDist = min(dist,minDist)
            maxDist = max(dist,maxDist)
        }
        if (maxDist == 0.0) return false
        val minDistNorm = minDist/maxDist
        val maxDistNorm = 1.0
        for (entity in entityList){
            if (entity is ServerPlayerEntity){
                val distNorm = 1.0 - (entity.squaredDistanceTo(user) - minDist)/maxDist
                val strength = effect.amplifier(level) * MathHelper.lerp(distNorm,minDistNorm,maxDistNorm)
                RegisterNetworking.sendPlayerKnockback(entity, Vec3d(user.x - entity.x,0.0,user.z - entity.z), strength)
            } else if (entity is LivingEntity){
                val distNorm = 1.0 - (entity.squaredDistanceTo(user) - minDist)/maxDist
                val strength = effect.amplifier(level) * MathHelper.lerp(distNorm,minDistNorm,maxDistNorm)
                entityTask(world,entity,user,strength,null, effect)
            }
        }
        effect.accept(toLivingEntityList(entityList), AugmentConsumer.Type.HARMFUL)
        effect.accept(user, AugmentConsumer.Type.BENEFICIAL)
        world.playSound(null,user.blockPos,soundEvent(),SoundCategory.PLAYERS,0.8F,1.2F)
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
        if (target is LivingEntity){
            target.takeKnockback(level,user.x - target.x,user.z - target.z)
        }
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ITEM_ELYTRA_FLYING
    }

}
