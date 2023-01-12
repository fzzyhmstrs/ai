package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.raycaster_util.RaycasterUtil
import me.fzzyhmstrs.amethyst_core.scepter_util.CustomDamageSources
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.AIClient
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class ZapAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier, maxLvl, *slot){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(6.8,0.2).withDamage(3.4f,0.1f)

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
        entityList.forEach {
            it.damage(CustomDamageSources.LightningDamageSource(user), effect.damage(level))
        }
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.7F, 1.1F)
        return true
    }

    override fun clientTask(world: World, user: LivingEntity, hand: Hand, level: Int) {
        val random = AIClient.aiRandom()
        val rotation = user.getRotationVec(MinecraftClient.getInstance().tickDelta).normalize()
        val perpendicularToPosX = 1.0
        val perpendicularToPosZ = (rotation.x/rotation.z) * -1
        val perpendicularVector = Vec3d(perpendicularToPosX,0.0,perpendicularToPosZ).normalize()
        val userPos = user.eyePos.add(0.0,-0.3,0.0)
        val increment = rotation.multiply(baseEffect.range(level) / 60)
        var particleBasePos = userPos
        for (i in 0..60){
            particleBasePos = particleBasePos.add(increment)
            for (j in 0..3){
                val rnd1 = random.nextDouble() * 0.4 - 0.2
                val rnd2 = random.nextDouble() * 0.4 - 0.2
                val particlePos = particleBasePos.add(perpendicularVector.multiply(rnd1))
                world.addParticle(ParticleTypes.ELECTRIC_SPARK,true, particlePos.x, particlePos.y + rnd2, particlePos.z, 0.0, 0.0, 0.0)
            }
        }
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,18,6,1,imbueLevel, LoreTier.NO_TIER, RegisterItem.BERYL_COPPER_INGOT)
    }


}