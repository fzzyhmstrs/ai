package me.fzzyhmstrs.amethyst_imbuement.spells.pieces

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.ScepterHelper
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.ParticleEffect
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.*

abstract class MultiTargetAugment(tier: ScepterTier): ScepterAugment(tier, AugmentType.AREA_DAMAGE) {
    override fun <T> applyTasks(
        world: World,
        context: ProcessContext,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        spells: PairedAugments
    )
    :
    SpellActionResult
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        val onCastResults = spells.processOnCast(context,world,null,user, hand, level, effects)
        if (!onCastResults.success()) return  FAIL
        if (onCastResults.overwrite()) return onCastResults
        if (world !is ServerWorld) return FAIL
        val rotation = user.getRotationVec(1.0F)
        val perpendicularVector = RaycasterUtil.perpendicularVector(rotation, RaycasterUtil.InPlane.XZ)
        val raycasterPos = user.pos.add(rotation.multiply(effects.range(level * 2)/2)).add(Vec3d(0.0,user.height/2.0,0.0))
        val entityList: MutableList<Entity> =
            RaycasterUtil.raycastEntityRotatedArea(
                world.iterateEntities(),
                user,
                raycasterPos,
                rotation,
                perpendicularVector,
                effects.range(level * 2),
                effects.range(level),
                1.2)

        val hostileEntityList = filter(entityList,user)
        if (hostileEntityList.isEmpty()) return FAIL
        val entityDistance: SortedMap<Double, EntityHitResult> = sortedMapOf()
        for (entity in hostileEntityList){
            if (entity.entity is MobEntity){
                val dist = entity.squaredDistanceTo(user)
                entityDistance[dist] = entity
            }
        }
        val entityDistanceIterator = entityDistance.values.iterator()
        var targetsLeft = effects.amplifier(level)
        val list: MutableList<Identifier> = onCastResults.results().toMutableList()
        while (entityDistanceIterator.hasNext() && targetsLeft > 0){
            val entity = entityDistanceIterator.next()
            targetsLeft--
            val temp = spells.processSingleEntityHit(entity,context,world,null,user,hand, level, effects)
            if (temp.isNotEmpty()){
                val buf = writeBuf(user,entity.entity,spells,particleSpeed())
                ScepterHelper.sendSpellParticlesFromServer(world,user.pos,buf)
            }
            list.addAll(temp)
        }
        return if (list.isEmpty()) FAIL else SpellActionResult.success(list)

    }

    open fun particleSpeed(): Double{
        return 3.0
    }

    override fun <T> onEntityHit(
        entityHitResult: EntityHitResult,
        context: ProcessContext,
        world: World,
        source: Entity?,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    )
            :
            SpellActionResult
            where
            T: LivingEntity,
            T: SpellCastingEntity
    {
        if (othersType.empty){
            val inputDamage = effects.damage(level)
            val damage = spells.provideDealtDamage(inputDamage, context,entityHitResult, user, world, hand, level, effects)
            val damageSource = spells.provideDamageSource(context,entityHitResult, source, user, world, hand, level, effects)
            val bl  = entityHitResult.entity.damage(damageSource, damage)

            return if(bl) {
                user.applyDamageEffects(user,entityHitResult.entity)
                spells.hitSoundEvents(world, entityHitResult.entity.blockPos,context)
                if (entityHitResult.entity.isAlive) {
                    SpellActionResult.success(AugmentHelper.DAMAGED_MOB, AugmentHelper.SLASHED)
                } else {
                    spells.processOnKill(entityHitResult,context, world, source, user, hand, level, effects)
                    SpellActionResult.success(AugmentHelper.DAMAGED_MOB, AugmentHelper.SLASHED, AugmentHelper.KILLED_MOB)
                }
            } else {
                FAIL
            }
        }

        return super.onEntityHit(entityHitResult,context, world, source, user, hand, level, effects, othersType, spells)
    }


    open fun filter(list: List<Entity>, user: LivingEntity): MutableList<EntityHitResult>{
        val hostileEntityList: MutableList<EntityHitResult> = mutableListOf()
        if (list.isNotEmpty()) {
            for (entity in list) {
                if (entity !== user) {
                    if (entity is PlayerEntity && !getPvpMode()) continue
                    if (entity is SpellCastingEntity && getPvpMode() && entity.isTeammate(user)) continue
                    hostileEntityList.add(EntityHitResult(entity))
                }
            }
        }
        return hostileEntityList
    }

    init{
        ScepterHelper.registerParticleAdder(PARTICLE_BLAST){ client, buf, effect ->
            client.execute {
                val speed = buf.readDouble()
                val userX = buf.readDouble()
                val userY = buf.readDouble()
                val userZ = buf.readDouble()
                val userPos = Vec3d(userX,userY,userZ)
                val userVelX = buf.readDouble()
                val userVelY = buf.readDouble()
                val userVelZ = buf.readDouble()
                val userVel = Vec3d(userVelX,userVelY,userVelZ)
                val targetX = buf.readDouble()
                val targetY = buf.readDouble()
                val targetZ = buf.readDouble()
                val targetPos = Vec3d(targetX,targetY,targetZ)
                client.execute {
                    particleBlast(userPos, userVel, targetPos, client.world, effect, speed)
                }
            }
        }
    }

    companion object {

        internal val PARTICLE_BLAST = Identifier(AI.MOD_ID, "particle_blast")
        private fun particleBlast(userPos: Vec3d, userVel: Vec3d, targetPos:Vec3d, world: World?, effect: ParticleEffect, speed: Double){
            if (world == null) return
            val random = world.random
            val direction = targetPos.subtract(userPos).normalize()
            val perpendicularToPosX = 1.0
            val perpendicularToPosZ = (direction.x/direction.z) * -1
            val perpendicularVector = Vec3d(perpendicularToPosX,0.0,perpendicularToPosZ).normalize()
            for (i in 1..10){
                val rnd1 = (random.nextDouble() - 0.5)/2.0
                val rnd2 = (random.nextDouble() - 0.5)/2.0
                val particlePos = userPos.add(perpendicularVector.multiply(rnd1)).add(0.0, rnd2,0.0)
                val particleVelocity = direction.multiply(speed).add(userVel)
                addParticles(world,effect,particlePos,particleVelocity)
            }
        }

        private fun addParticles(world: World, particleEffect: ParticleEffect, pos: Vec3d, velocity: Vec3d){
            world.addParticle(particleEffect,true,pos.x,pos.y,pos.z,velocity.x,velocity.y,velocity.z)
        }

        internal fun writeBuf(user: LivingEntity, target: Entity, spells: PairedAugments, speed: Double): PacketByteBuf {
            val buf = ScepterHelper.prepareParticlePacket(PARTICLE_BLAST,spells.getCastParticleType())
            val userPos = user.eyePos.add(0.0,-0.3,0.0)
            val userVel = user.velocity
            val targetPos = target.pos .add(0.0,target.height / 1.5,0.0)
            buf.writeDouble(speed)
            buf.writeDouble(userPos.x)
            buf.writeDouble(userPos.y)
            buf.writeDouble(userPos.z)
            buf.writeDouble(userVel.x)
            buf.writeDouble(userVel.y)
            buf.writeDouble(userVel.z)
            buf.writeDouble(targetPos.x)
            buf.writeDouble(targetPos.y)
            buf.writeDouble(targetPos.z)
            return buf
        }

    }
}