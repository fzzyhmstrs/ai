package me.fzzyhmstrs.amethyst_imbuement.spells.tales.rpg_series

import me.fzzyhmstrs.amethyst_core.compat.spell_power.SpChecker
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.registry.RegisterTag
import me.fzzyhmstrs.amethyst_core.scepter_util.CustomDamageSources
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfTalesItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterSound
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.particle.DustParticleEffect
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.joml.Vector3f

class SolarFlareAugment: MiscAugment(ScepterTier.THREE,11), PersistentEffectHelper.PersistentEffect{

    override val baseEffect: AugmentEffect = super.baseEffect
                                                .withDamage(14.5f,0.5f)
                                                .withRange(14.0,2.0)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,400,200,
            25,imbueLevel,20, BookOfTalesItem.TALES_TIER, Items.GLOWSTONE)
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
        val newData = FlareChargeEffectData(world,user,50, effect,level)
        PersistentEffectHelper.setPersistentTickerNeed(this,5,5,newData)
        world.playSound(null, user.blockPos, RegisterSound.SOLAR_FLARE_CHARGE, SoundCategory.PLAYERS, 1.0F, 1.0F)
        return true
    }

    private fun beam(serverWorld: ServerWorld, entity: Entity, rotation: Vec3d, range: Double){
        val startPos = entity.pos.add(0.0,entity.height/2.0,0.0)
        val endPos = startPos.add(rotation.multiply(range))
        val vec = endPos.subtract(startPos).multiply(1.0/range.toInt())
        var pos = startPos
        for (i in 1..range.toInt()){
            serverWorld.spawnParticles(DustParticleEffect(Vector3f(1.0f,1.0f,0.0f),1.0f),pos.x,pos.y,pos.z,75,0.5,0.5,0.5,0.0)
            pos = pos.add(vec)
        }

    }

    override fun soundEvent(): SoundEvent {
        return RegisterSound.SOLAR_FLARE_FIRE
    }

    override val delay: PerLvlI = PerLvlI()

    override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
        if (data is FlareChargeEffectData){

            if (data.remaining > 0) {
                data.world.spawnParticles(DustParticleEffect(Vector3f(1.0f,1.0f,0.0f),1.0f),data.user.x, data.user.getBodyY(0.5),data.user.z,60 - data.remaining,1.0,1.0,1.0,0.05)
                val newData = FlareChargeEffectData(data.world,data.user,data.remaining - 5, data.effect,data.level)
                PersistentEffectHelper.setPersistentTickerNeed(this,5,5,newData)
            } else {
                if (data.user !is PlayerEntity) return
                val rotation = data.user.getRotationVec(1.0F)
                val perpendicularVector = RaycasterUtil.perpendicularVector(rotation, RaycasterUtil.InPlane.XZ)
                val raycasterPos = data.user.pos.add(rotation.multiply(data.effect.range(data.level)/2)).add(Vec3d(0.0,data.user.height/2.0,0.0))
                val entityList: List<Entity> =
                    RaycasterUtil.raycastEntityRotatedArea(
                        data.world.iterateEntities(),
                        data.user,
                        raycasterPos,
                        rotation,
                        perpendicularVector,
                        data.effect.range(data.level),
                        1.5,
                        1.5).filter { AiConfig.entities.shouldItHitBase(data.user, it,this) }
                if (entityList.isNotEmpty()) {
                    val mod = SpChecker.getModFromTags(data.user,RegisterTag.ARCANE_AUGMENTS, RegisterTag.FIRE_AUGMENTS)
                    val dmg = data.effect.damage(data.level) * (1f + mod.toFloat()/100f)
                    entityList.forEach {
                        it.damage(CustomDamageSources.lightningBolt(data.world, null, data.user), if (it is LivingEntity && (it.maxHealth > 50f || it.isUndead)) dmg*2f else dmg)
                        if (it is MobEntity && it.isUndead)
                            it.setOnFireFor(10)
                        if (it is LivingEntity)
                            it.addStatusEffect(StatusEffectInstance(StatusEffects.GLOWING, 600))
                    }
                }
                beam(data.world,data.user,rotation,data.effect.range(data.level))
                data.world.playSound(null, data.user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
            }
        }

    }

    private class FlareChargeEffectData(val world: ServerWorld, val user: LivingEntity, val remaining: Int, val effect: AugmentEffect, val level: Int): PersistentEffectHelper.PersistentEffectData
}
