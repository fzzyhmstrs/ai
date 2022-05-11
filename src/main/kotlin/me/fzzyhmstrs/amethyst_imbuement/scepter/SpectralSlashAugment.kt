package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.*

@Suppress("SameParameterValue")
class SpectralSlashAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier, maxLvl, *slot) {

    private val particleSpeed = 2.5
    private val particles: Array<Pair<Double,Double>> = arrayOf(
        Pair(-1.0,0.0),
        Pair(-0.9,0.05),
        Pair(-0.8,0.1),
        Pair(-0.7,0.1),
        Pair(-0.6,0.15),
        Pair(-0.5,0.15),
        Pair(-0.4,0.15),
        Pair(-0.3,0.15),
        Pair(-0.2,0.2),
        Pair(-0.1,0.2),
        Pair(0.0,0.2))
    private val particleOffsets: Array<Double> = arrayOf(
        0.0,
        0.05,
        0.15,
        0.3)


    override fun effect(world: World, target: Entity?, user: LivingEntity, level: Int, hit: HitResult?): Boolean {
        if (world !is ServerWorld) return false
        if (user !is PlayerEntity) return false
        val rotation = user.getRotationVec(1.0F)
        val perpendicularVector = RaycasterUtil.perpendicularVector(rotation, RaycasterUtil.InPlane.XZ)
        val raycasterPos = user.pos.add(rotation.multiply(rangeOfEffect()/2.0 + 0.25 * level)).add(Vec3d(0.0,user.height/2.0,0.0))
        val entityList: MutableList<Entity> =
            RaycasterUtil.raycastEntityRotatedArea(
                world.iterateEntities(),
                user,
                raycasterPos,
                rotation,
                perpendicularVector,
                rangeOfEffect() + 0.5 * level,
                rangeOfEffect() + 0.25 * level,
                1.2)
        //val entityList: MutableList<Entity> = RaycasterUtil.raycastEntityArea(rangeOfEffect() + 0.5 * level,BlockPos(raycasterPos))
        val hostileEntityList: MutableList<Entity> = mutableListOf()
        if (entityList.isNotEmpty()) {
            for (entity in entityList) {
                if (entity !== user) {
                    if (!entity.isTeammate(user)) {
                        hostileEntityList.add(entity)
                    }
                }
            }
        }
        if (!effect(world, user, hostileEntityList, level)) return false
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
        return true
    }

    override fun effect(world: World, user: LivingEntity, entityList: MutableList<Entity>, level: Int): Boolean {
        val entityDistance: SortedMap<Double, Entity> = mutableMapOf<Double, Entity>().toSortedMap()
        for (entity in entityList){
            if (entity is MobEntity){
                val dist = entity.squaredDistanceTo(user)
                entityDistance[dist] = entity
            }
        }
        if (entityDistance.isNotEmpty()) {
            val baseDamage = 5.0F
            val splashDamage = 2.5F
            var closestHit = false
            for (entity in entityDistance){
                if (!closestHit) {
                    entity.value.damage(DamageSource.magic(entity.value, user), baseDamage)
                    closestHit = true
                } else {
                    entity.value.damage(DamageSource.magic(entity.value, user), splashDamage)
                }
            }
        }
        return true
    }

    override fun clientTask(world: World, user: LivingEntity,
                            hand: Hand, level: Int) {
        val rotation = user.getRotationVec(MinecraftClient.getInstance().tickDelta).normalize()
        val perpendicularToPosX = 1.0
        val perpendicularToPosZ = (rotation.x/rotation.z) * -1
        val perpendicularVector = Vec3d(perpendicularToPosX,0.0,perpendicularToPosZ).normalize()
        val userPos = user.eyePos.add(0.0,-0.3,0.0)
        val scale = rangeOfEffect()/2.0 + 0.125 * level
        for (p in particles){
            for (p2 in particleOffsets) {
                val particlePos =
                    userPos.add(perpendicularVector.multiply(p.first * scale)).add(rotation.multiply(p.second + p2))
                val particleVelocity = rotation.multiply(particleSpeed + level * 0.25).add(user.velocity)
                addParticles(world,ParticleTypes.ELECTRIC_SPARK,particlePos,particleVelocity)
                val particlePos2 =
                    userPos.add(perpendicularVector.multiply(p.first * -1 * scale)).add(rotation.multiply(p.second + p2))
                val particleVelocity2 = rotation.multiply(particleSpeed+ level * 0.25).add(user.velocity)
                addParticles(world,ParticleTypes.ELECTRIC_SPARK,particlePos2,particleVelocity2)
            }

        }
    }

    override fun rangeOfEffect(): Double {
        return 2.5
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.FURY,16,4,8,imbueLevel,1, Items.IRON_SWORD)
    }

    private fun addParticles(world: World,particleEffect: ParticleEffect,pos: Vec3d,velocity: Vec3d){
        world.addParticle(particleEffect,pos.x,pos.y,pos.z,velocity.x,velocity.y,velocity.z)
    }

}