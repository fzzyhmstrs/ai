package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.SummonAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.entity.living.BaseHamsterEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.item.Items
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.text.Text
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class SummonSeahorseAugment: SummonAugment<BaseHamsterEntity>(ScepterTier.ONE, AugmentType.SUMMON_GOOD) {

    override val augmentData: AugmentDatapoint
        get() = TODO("Not yet implemented")

    override val baseEffect: AugmentEffect
        get() = super.baseEffect

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        TODO("Not yet implemented")
    }

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