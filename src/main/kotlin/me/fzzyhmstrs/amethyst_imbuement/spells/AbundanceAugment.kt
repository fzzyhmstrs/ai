package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.MiscAugment
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.block.CropBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

class AbundanceAugment: ScepterAugment(ScepterTier.ONE, AugmentType.BLOCK_AREA) {
    override val augmentData: AugmentDatapoint
        get() = TODO("Not yet implemented")

    //ml 6
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(1.5,0.5).withDamage(0.18F,0.02F)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        TODO("Not yet implemented")
    }

    override fun <T : LivingEntity> applyTasks(
        world: World,
        context: ProcessContext,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        spells: PairedAugments
    ): SpellActionResult where T : SpellCastingEntity {
        TODO("Not yet implemented")
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE, PerLvlI(15,-1),3,
            1,imbueLevel,1, LoreTier.NO_TIER, Items.HAY_BLOCK)
    }

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
}