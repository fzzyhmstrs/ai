package me.fzzyhmstrs.amethyst_imbuement.entity.goal

import me.fzzyhmstrs.amethyst_imbuement.entity.living.SardonyxElementalEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.spell.BoneShardEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import java.util.function.Consumer
import java.util.function.Supplier
import kotlin.math.sqrt

class SardonyxElementalAttackGoal(
    sardonyxElementalEntity: SardonyxElementalEntity,
    ownerGetter: Supplier<LivingEntity>,
    activeConsumer: Consumer<Boolean>
) : ShootProjectileGoal(sardonyxElementalEntity, ownerGetter, activeConsumer, 30, 20) {

    override fun canStart(): Boolean {
        val livingEntity = mobEntity.target
        return livingEntity != null && livingEntity.isAlive && mobEntity.canTarget(livingEntity)
    }

    override fun lookAt(livingEntity: LivingEntity) {
        super.lookAt(livingEntity)
        mobEntity.moveControl.moveTo(livingEntity.x, livingEntity.y, livingEntity.z, 0.5)
    }

    override fun shootProjectile(d: Double, livingEntity: LivingEntity) {
        val h = (sqrt(sqrt(d)) * 0.5f).toFloat()
        if (!mobEntity.isSilent) {
            mobEntity.world.syncWorldEvent(null, WorldEvents.BLAZE_SHOOTS, mobEntity.blockPos, 0)
        }
        val rot = Vec3d(livingEntity.x - mobEntity.x,livingEntity.getBodyY(0.5) - mobEntity.getBodyY(0.5),livingEntity.z - mobEntity.z)
        val pos  = Vec3d(mobEntity.x,mobEntity.getBodyY(0.5) + 0.5,mobEntity.z)
        val owner = ownerGetter.get()
        val bse = BoneShardEntity(mobEntity.world,owner,4.0f,0.5f*h,pos,rot)
        bse.setOnBlockHit { bhr ->
            livingEntity.world.createExplosion(
                bse,
                bhr.pos.x,
                bhr.pos.y,
                bhr.pos.z,
                0.75f,
                World.ExplosionSourceType.NONE
            )
        }
        bse.setOnEntityHit { le ->
            le.world.createExplosion(
                bse,
                le.x,
                le.getBodyY(0.5),
                le.z,
                0.75f,
                World.ExplosionSourceType.NONE
            )
        }
        mobEntity.world.spawnEntity(bse)
        val bse2 = BoneShardEntity(mobEntity.world,owner,4.0f,2.25f*h,pos,rot)
        bse2.setOnBlockHit { bhr ->
            livingEntity.world.createExplosion(
                bse2,
                bhr.pos.x,
                bhr.pos.y,
                bhr.pos.z,
                0.75f,
                World.ExplosionSourceType.NONE
            )
        }
        bse2.setOnEntityHit { le ->
            le.world.createExplosion(
                bse2,
                le.x,
                le.getBodyY(0.5),
                le.z,
                0.75f,
                World.ExplosionSourceType.NONE
            )
        }
        mobEntity.world.spawnEntity(bse2)
        val bse3 = BoneShardEntity(mobEntity.world,owner,4.0f,2.25f*h,pos,rot)
        bse3.setOnBlockHit { bhr ->
            livingEntity.world.createExplosion(
                bse3,
                bhr.pos.x,
                bhr.pos.y,
                bhr.pos.z,
                0.75f,
                World.ExplosionSourceType.NONE
            )
        }
        bse3.setOnEntityHit { le ->
            le.world.createExplosion(
                bse3,
                le.x,
                le.getBodyY(0.5),
                le.z,
                0.75f,
                World.ExplosionSourceType.NONE
            )
        }
        mobEntity.world.spawnEntity(bse3)
    }

}