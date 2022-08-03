package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_core.entity_util.MissileEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.projectile.SmallFireballEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World

class SoulMissileEntity: MissileEntity {

    constructor(entityType: EntityType<SoulMissileEntity>, world: World): super(entityType, world)
    constructor(world: World,owner: LivingEntity, pierce: Boolean) : super(world, owner, pierce){
    }

    override fun getParticleType(): ParticleEffect {
        return ParticleTypes.SOUL
    }
}