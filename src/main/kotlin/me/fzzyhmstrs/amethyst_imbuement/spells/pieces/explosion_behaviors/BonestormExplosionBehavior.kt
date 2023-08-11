package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.explosion_behaviors

import me.fzzyhmstrs.amethyst_core.augments.CustomExplosion
import me.fzzyhmstrs.amethyst_imbuement.entity.BasicShardEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class BonestormExplosionBehavior: CustomExplosion.CustomExplosionBehavior() {
    override fun extraExplosionEffects(x: Double, y: Double, z: Double, world: World, source: Entity?) {
        for (i in 1..10){
            val rndX = world.random.nextDouble() * 2.0 -1.0
            val rndY = world.random.nextDouble() * 2.0 -1.0
            val rndZ = world.random.nextDouble() * 2.0 -1.0
            val bse = BasicShardEntity(RegisterEntity.BONE_SHARD_ENTITY,world,null,4.0f, 1.75f, Vec3d(x,y,z), Vec3d(rndX,rndY,rndZ))
            world.spawnEntity(bse)
        }
    }
}