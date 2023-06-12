package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.CustomDamageSources
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.AIClient
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.*

class BodySwapAugment: MiscAugment(ScepterTier.THREE,11){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(15.75,0.75)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,600,125,
            19,imbueLevel,40, LoreTier.HIGH_TIER, Items.ENDER_EYE)
    }

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        if (world !is ServerWorld) return false
        if (user !is PlayerEntity) return false
        val rotation = user.getRotationVec(1.0F)
        val perpendicularVector = RaycasterUtil.perpendicularVector(rotation, RaycasterUtil.InPlane.XZ)
        val raycasterPos = user.pos.add(rotation.multiply(effect.range(level)/2)).add(Vec3d(0.0,user.height/2.0,0.0))
        val entityList: MutableList<Entity> =
            RaycasterUtil.raycastEntityRotatedArea(
                world.iterateEntities(),
                user,
                raycasterPos,
                rotation,
                perpendicularVector,
                effect.range(level),
                2.5,
                3.0)
        if (entityList.isEmpty()) return false
        val map: SortedMap<Double,Entity> = sortedMapOf()
        entityList.forEach {
            map[it.squaredDistanceTo(user)] = it
        }
        val entity = map[map.lastKey()]?:return false
        val pos1 = Vec3d(entity.pos.toVector3f())
        val pos2 = Vec3d(user.pos.toVector3f())
        entity.teleport(pos2.x,pos2.y,pos2.z)
        user.teleport(pos1.x,pos1.y,pos1.z)
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.7F, 1.1F)
        return true
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_PORTAL_TRIGGER
    }
}