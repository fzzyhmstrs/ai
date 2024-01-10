package me.fzzyhmstrs.amethyst_imbuement.entity.monster

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterSound
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import me.fzzyhmstrs.fzzy_core.coding_util.compat.FzzyDamage
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.particle.DustParticleEffect
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.util.math.Vec3d
import org.joml.Vector3f

class DevastationBeam: PersistentEffectHelper.PersistentEffect {

    companion object{
        val INSTANCE = DevastationBeam()
    }

    fun fire(world: ServerWorld,caster: SardonyxElementalEntity, pos: Vec3d, rot: Vec3d){
        world.playSound(null, caster.blockPos, RegisterSound.SOLAR_FLARE_CHARGE, SoundCategory.HOSTILE, 1.0F, 1.0F)
        val data = DevastationBeamData(world,caster, pos, rot,35)
        PersistentEffectHelper.setPersistentTickerNeed(this,5,5, data)
    }

    override val delay: PerLvlI = PerLvlI()

    override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
        if (data !is DevastationBeamData) return
        if (data.remaining > 0) {
            data.world.spawnParticles(DustParticleEffect(Vector3f(1.0f,0.0f,0.0f),1.25f),data.pos.x,data.pos.y,data.pos.z,100 - data.remaining,2.0,2.0,2.0,0.1)
            beam(data.world,data.pos,data.rot,16.0,5)
            val newData = DevastationBeamData(
                data.world,
                data.caster,
                data.pos,
                data.rot,
                data.remaining - 5
            )
            PersistentEffectHelper.setPersistentTickerNeed(this,5,5,newData)
        } else {
            data.caster.setCharging(false)
            val rotation = data.rot
            val perpendicularVector = RaycasterUtil.perpendicularVector(rotation, RaycasterUtil.InPlane.XZ)
            val raycasterPos = data.pos.add(rotation.multiply(8.0))
            val entityList: MutableList<Entity> =
                RaycasterUtil.raycastEntityRotatedArea(
                    data.world.iterateEntities(),
                    data.caster,
                    raycasterPos,
                    rotation,
                    perpendicularVector,
                    16.0,
                    3.0,
                    3.0)
            if (entityList.isNotEmpty()) {
                val dmg = AiConfig.entities.sardonyxElemental.devastationBeamDmg.get()
                for (it in entityList) {
                    if (it is SardonyxElementalEntity || it is SardonyxFragmentEntity) continue
                    it.damage(FzzyDamage.sonicBoom(data.caster), dmg)
                    if (it is LivingEntity)
                        it.addStatusEffect(StatusEffectInstance(StatusEffects.WITHER, 600,3))
                }
            }
            beam(data.world,data.pos,data.rot,16.0,300)
            data.world.playSound(null, data.pos.x,data.pos.y,data.pos.z, RegisterSound.SOLAR_FLARE_FIRE, SoundCategory.HOSTILE, 1.0F, 1.0F)
        }
    }

    private fun beam(serverWorld: ServerWorld, startPos: Vec3d, rotation: Vec3d, range: Double, count: Int){
        val endPos = startPos.add(rotation.multiply(range))
        val vec = endPos.subtract(startPos).multiply(1.0/range.toInt())
        var pos = startPos
        for (i in 1..range.toInt()){
            serverWorld.spawnParticles(DustParticleEffect(Vector3f(1.0f,0.0f,0.0f),1.0f),pos.x,pos.y,pos.z,count,0.8,0.8,0.8,0.0)
            pos = pos.add(vec)
        }

    }

    private class DevastationBeamData(val world: ServerWorld, val caster: SardonyxElementalEntity, val pos: Vec3d, val rot: Vec3d, val remaining: Int): PersistentEffectHelper.PersistentEffectData

}