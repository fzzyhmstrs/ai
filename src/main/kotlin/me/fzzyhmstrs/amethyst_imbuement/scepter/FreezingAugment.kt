package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.entity.FreezingEntity
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.SummonProjectileAugment
import me.fzzyhmstrs.amethyst_imbuement.util.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

class FreezingAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): SummonProjectileAugment(tier, maxLvl, *slot) {

    override fun entityClass(world: World, user: LivingEntity, level: Int): ProjectileEntity {
        val fe = FreezingEntity(world,user,level)
        fe.setVelocity(user,user.pitch,user.yaw,0.0f,
            1.3f,
            0.5f)
        return fe
    }

    override fun entityTask(world: World, target: Entity, user: LivingEntity, level: Double, hit: HitResult?) {
        target.frozenTicks = 180 + 100 * level.toInt()
        target.damage(DamageSource.GENERIC,2.0F)
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.FURY,35,8,5,imbueLevel,1, Items.PACKED_ICE)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_PLAYER_HURT_FREEZE
    }

}