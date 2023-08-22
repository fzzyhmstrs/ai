package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonEntityAugment
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.item.Items
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class SummonBoatAugment: SummonEntityAugment(ScepterTier.ONE,1) {

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT,1200,50,
            1,imbueLevel,40,LoreTier.LOW_TIER, Items.OAK_BOAT)
    }

    override fun placeEntity(
        world: World,
        user: PlayerEntity,
        hit: HitResult,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        val vec3d2: Vec3d
        val vec3d: Vec3d = user.getRotationVec(1.0f)
        val list: List<Entity> = world.getOtherEntities(
            user,
            user.boundingBox.stretch(vec3d.multiply(5.0)).expand(1.0),
            EntityPredicates.EXCEPT_SPECTATOR.and { obj: Entity -> obj.isCollidable }
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
        boat.variant = BoatEntity.Type.OAK
        boat.yaw = user.yaw
        if (!world.isSpaceEmpty(boat, boat.boundingBox)) {
            return false
        }
        if (world.spawnEntity(boat)) {
            return super.placeEntity(world, user, hit, level, effects)
        }
        return false
    }
}