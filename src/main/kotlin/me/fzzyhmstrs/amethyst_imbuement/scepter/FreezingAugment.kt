package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.augments.SummonProjectileAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.CustomDamageSources
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

class FreezingAugment: SummonProjectileAugment(ScepterTier.ONE,6){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDuration(230,50,0)
            .withDamage(2.9F,0.1f)
            .withRange(3.8,0.2)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY, PerLvlI(36,-2),8,
            5
            ,imbueLevel,1, LoreTier.LOW_TIER, Items.PACKED_ICE)
    }

    override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val fe = FreezingEntity(world,user,level)
        fe.passEffects(effects, level)
        fe.setAugment(this)
        fe.setVelocity(user,user.pitch,user.yaw,0.0f,
            1.3f,
            0.5f)
        return fe
    }

    override fun entityTask(
        world: World,
        target: Entity,
        user: LivingEntity,
        level: Double,
        hit: HitResult?,
        effects: AugmentEffect
    ) {
        if (target is LivingEntity) target.frozenTicks = effects.duration(0)
        target.damage(CustomDamageSources.freeze(world,null,user),effects.damage(0)*2/3)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_PLAYER_HURT_FREEZE
    }

}
