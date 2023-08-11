package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.SummonAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.totem.TotemOfGraceEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import net.minecraft.entity.LivingEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class SummonGraceTotemAugment: SummonAugment<TotemOfGraceEntity>(ScepterTier.TWO){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("summon_grace_totem"),SpellType.GRACE,2400,240,
            14,11,1,50,LoreTier.LOW_TIER, RegisterItem.DAZZLING_MELON_SLICE)

    //ml 11
    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDuration(650,50)
            .withDamage(1.8f,0.2f)
            .withRange(6.8,0.2)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
    }

    override fun entitiesToSpawn(
        world: World,
        user: LivingEntity,
        hand: Hand,
        hit: HitResult,
        level: Int,
        effects: AugmentEffect,
        spells: PairedAugments,
        count: Int
    ): List<TotemOfGraceEntity> {
        val startPos = BlockPos.ofFloored(hit.pos)
        val list: MutableList<TotemOfGraceEntity> = mutableListOf()
        for (i in 1..count){
            val totemOfFuryEntity = TotemOfGraceEntity(RegisterEntity.TOTEM_OF_GRACE_ENTITY, world, user, effects.duration(level))
            val success = AugmentHelper.findSpawnPos(world, startPos, totemOfFuryEntity, 3, 15, user.pitch, user.yaw)
            if (success) {
                totemOfFuryEntity.passEffects(spells, effects, level)
                list.add(totemOfFuryEntity)
            }
        }

        return list
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.BLOCK_DEEPSLATE_BRICKS_PLACE, SoundCategory.PLAYERS,1f,1f)
    }
}
