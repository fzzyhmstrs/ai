package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.util.LoreTier
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.entity.*
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

class LightningBoltAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier, maxLvl, *slot) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(14.0,0.0,0.0).withDamage(5.0F)

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
            val le = RegisterEntity.PLAYER_LIGHTNING.create(world)
            if (le != null) {
                le.entityEffects.setDamage(effect.damage(level))
                le.entityEffects.setConsumers(effect)
                le.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos))
                world.spawnEntity(le)
                world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 0.65F)
                return true
            }
        }
        return false
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.FURY,50,10,5,imbueLevel,LoreTier.LOW_TIER, Items.LIGHTNING_ROD)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ITEM_TRIDENT_THUNDER
    }

}