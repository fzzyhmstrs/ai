package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SlashAugment
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.mob.MobEntity
import net.minecraft.item.Items
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.*

class CrippleAugment: SlashAugment(ScepterTier.TWO,13) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(4.5F,0.5F,0.0F)
            .withRange(7.75,0.25,0.0)
            .withDuration(110,10)
            .withAmplifier(-1,1,0)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,20,12,
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
        val bl = target.damage(if (user is PlayerEntity) DamageSource.player(user) else DamageSource.mob(user), damage)
        if (bl) {
            if (user is ServerPlayerEntity) {
                ServerPlayNetworking.send(user, NOTE_BLAST, writeBuf(user, target))
            }
            effect.accept(target,AugmentConsumer.Type.HARMFUL)
            val amp = if (crit) 4 else 1
            target.addStatusEffect(StatusEffectInstance(StatusEffects.WEAKNESS,effect.duration(level), amp))
            target.addStatusEffect(StatusEffectInstance(StatusEffects.SLOWNESS,effect.duration(level), amp))
        }
        return bl
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_BELL_RESONATE
    }

    override fun particleType(): DefaultParticleType {
        return ParticleTypes.ELECTRIC_SPARK
    }

    override fun particleSpeed(): Double {
        return 3.0
    }

    companion object {

        private val NOTE_BLAST = Identifier(AI.MOD_ID, "note_blast")

        internal fun registerClient() {
            ClientPlayNetworking.registerGlobalReceiver(NOTE_BLAST) { client, _, buf, _ ->
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
                    noteBlast(userPos, userVel, targetPos, client.world)
                }
            }
        }

        private fun noteBlast(userPos: Vec3d,userVel: Vec3d, targetPos:Vec3d, world: World?){
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
                val particleVelocity = direction.multiply(RegisterEnchantment.RESONATE.particleSpeed()).add(userVel)
                addParticles(world,RegisterEnchantment.RESONATE.particleType(),particlePos,particleVelocity)
            }
        }

        private fun addParticles(world: World, particleEffect: ParticleEffect, pos: Vec3d, velocity: Vec3d){
            world.addParticle(particleEffect,true,pos.x,pos.y,pos.z,velocity.x,velocity.y,velocity.z)
        }

        internal fun writeBuf(user: LivingEntity, target: Entity): PacketByteBuf{
            val buf = PacketByteBufs.create()
            val userPos = user.eyePos.add(0.0,-0.3,0.0)
            val userVel = user.velocity
            val targetPos = target.pos .add(0.0,target.height / 1.5,0.0)
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
