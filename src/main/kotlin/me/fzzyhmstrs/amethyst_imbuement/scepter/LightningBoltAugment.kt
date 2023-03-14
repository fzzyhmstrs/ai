package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerLightningEntity
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class LightningBoltAugment: MiscAugment(ScepterTier.TWO,11){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withRange(13.8,0.2,0.0)
            .withDamage(4.8F,0.2f)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY, PerLvlI(51,-1),20,
            6,imbueLevel,3,LoreTier.LOW_TIER, Items.LIGHTNING_ROD)
    }

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        val entity = RaycasterUtil.raycastEntity(effect.range(level),user)

        val blockPos: BlockPos = if (entity == null){
            if (hit == null) {
                return false
            } else if (hit.type == HitResult.Type.BLOCK){
                val bp = (hit as BlockHitResult).blockPos
                if (world.isSkyVisible(bp)){
                    bp
                } else {
                    hit.blockPos.add(0,1,0)
                }

            } else if (hit.type == HitResult.Type.ENTITY){
                (hit as EntityHitResult).entity.blockPos
            } else if (hit.type == HitResult.Type.MISS){
                return false
            } else {
                return false
            }
        } else {
            entity.blockPos
        }

        if (world.isSkyVisible(blockPos)) {
            //replace with a player version that can pass consumers?
            val le = PlayerLightningEntity.createLightning(world, Vec3d.ofBottomCenter(blockPos),user,effect, level)
            val bl = world.spawnEntity(le)
            if (bl) {
                effect.accept(user, AugmentConsumer.Type.BENEFICIAL)
                world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 0.65F)
            }
            return bl
        }
        return false
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ITEM_TRIDENT_THUNDER
    }

}