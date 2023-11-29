package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.CustomDamageSources
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellDamageSource
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class ZapAugment: MiscAugment(ScepterTier.ONE,11){

    override val baseEffect: AugmentEffect = super.baseEffect
                                                .withDamage(3.4f,0.1f)
                                                .withRange(6.8,0.2)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,18,6,
            1,imbueLevel,1, LoreTier.NO_TIER, RegisterItem.BERYL_COPPER_INGOT)
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
                0.8,
                0.8)
        filter(entityList,user).forEach {
            it.damage(SpellDamageSource(CustomDamageSources.lightningBolt(world,null,user),this), effect.damage(level))
        }
        beam(world,user,rotation,effect.range(level))
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.7F, 1.1F)
        return true
    }

    private fun beam(serverWorld: ServerWorld, entity: Entity, rotation: Vec3d, range: Double){
        val startPos = entity.pos.add(0.0,entity.height/2.0,0.0)
        val endPos = startPos.add(rotation.multiply(range))
        val vec = endPos.subtract(startPos).multiply(0.05)
        var pos = startPos
        for (i in 1..20){
            serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK,pos.x,pos.y,pos.z,10,vec.x,vec.y,vec.z,0.0)
            pos = pos.add(vec)
        }

    }

    private fun filter(list: List<Entity>, user: LivingEntity): MutableList<Entity>{
        val hostileEntityList: MutableList<Entity> = mutableListOf()
        if (list.isNotEmpty()) {
            for (entity in list) {
                if (entity !== user) {
                    if (AiConfig.entities.shouldItHitBase(user, entity,this))
                        hostileEntityList.add(entity)
                }
            }
        }
        return hostileEntityList
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value()
    }
}
