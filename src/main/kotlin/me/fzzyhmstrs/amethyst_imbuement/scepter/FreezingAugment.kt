package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.CustomDamageSources
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonProjectileAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.FreezingEntity
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

class FreezingAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): SummonProjectileAugment(tier, maxLvl, *slot){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDuration(230,50,0)
            .withDamage(2.9F,0.1f)
            .withRange(3.8,0.2)

    override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val fe = FreezingEntity(world,user,level)
        fe.passEffects(effects, level)
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
        target.damage(CustomDamageSources.FreezingDamageSource(user),effects.damage(0)*2/3)
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY, PerLvlI(36,-2),8,4,imbueLevel,1, LoreTier.LOW_TIER, Items.PACKED_ICE)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_PLAYER_HURT_FREEZE
    }

}
