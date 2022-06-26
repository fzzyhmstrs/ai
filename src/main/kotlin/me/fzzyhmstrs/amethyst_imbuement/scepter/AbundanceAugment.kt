package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.coding_util.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import net.minecraft.block.CropBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

class AbundanceAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier,maxLvl, *slot) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(1.0,1.0).withDamage(0.15F,0.05F)

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        var successes = 0
        val range = (effect.range(level)).toInt()
        val userPos = user.blockPos
        for (i in -range..range) {
            for (j in -range..range) {
                for (k in -1..1) {
                    val bs = world.getBlockState(userPos.add(i, k, j))
                    val bsb = bs.block
                    if (bsb is CropBlock) {
                        val rnd1 = world.random.nextDouble()
                        if (rnd1 < effect.damage(level)) {
                            successes++
                            if (bsb.isMature(bs)) {
                                world.breakBlock(userPos.add(i, k, j), true)
                                world.setBlockState(userPos.add(i, k, j), bsb.defaultState)
                                continue
                            }
                            bsb.grow(world as ServerWorld, world.random, userPos.add(i, k, j), bs)
                        }
                    }
                }
            }
        }
        return successes > 0
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE,10,2,1,imbueLevel, LoreTier.NO_TIER, Items.HAY_BLOCK)
    }

}