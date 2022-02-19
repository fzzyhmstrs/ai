package me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments

import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World


open class SummonEntityAugment(weight: Rarity, tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): ScepterAugment(weight,tier,maxLvl,EnchantmentTarget.WEAPON, *slot) {

    open fun placeEntity(world: World, user: PlayerEntity, hit: HitResult, level: Int): Boolean{
        val vec3d2: Vec3d
        val vec3d: Vec3d = user.getRotationVec(1.0f)
        val list: List<Entity> = world.getOtherEntities(
            user,
            user.boundingBox.stretch(vec3d.multiply(5.0)).expand(1.0),
            EntityPredicates.EXCEPT_SPECTATOR.and { obj: Entity -> obj.collides() }
        )
        if (list.isNotEmpty()) {
            vec3d2 = user.eyePos
            for (entity in list) {
                val box = entity.boundingBox.expand(entity.targetingMargin.toDouble())
                if (!box.contains(vec3d2)) continue
                return false
            }
        }
        val boat = BoatEntity(world, hit.pos.x, hit.pos.y, hit.pos.z)
        boat.boatType = BoatEntity.Type.OAK
        boat.yaw = user.yaw
        if (!world.isSpaceEmpty(boat, boat.boundingBox)) {
            return false
        }
        world.spawnEntity(boat)
        world.playSound(null,user.blockPos,soundEvent(),SoundCategory.PLAYERS,1.0F,1.0F)
        return true
    }

    open fun soundEvent():SoundEvent{
        return SoundEvents.ENTITY_BOAT_PADDLE_WATER
    }

}