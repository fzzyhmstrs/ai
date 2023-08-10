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
import me.fzzyhmstrs.amethyst_imbuement.entity.totem.TotemOfFuryEntity
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

class SummonFuryTotemAugment: SummonAugment<TotemOfFuryEntity>(ScepterTier.TWO) {
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("summon_fury_totem"),SpellType.FURY,2400,240,
            14,5,1,50,LoreTier.LOW_TIER, RegisterItem.RESONANT_ROD)

    //ml 5
    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDuration(750,50)
            .withDamage(2.75f,0.25f)
            .withRange(4.5,0.5)
            .withAmplifier(8,1)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
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
    ): List<TotemOfFuryEntity> {
        val startPos = BlockPos.ofFloored(hit.pos)
        val list: MutableList<TotemOfFuryEntity> = mutableListOf()
        for (i in 1..count){
            val totemOfFuryEntity = TotemOfFuryEntity(RegisterEntity.TOTEM_OF_FURY_ENTITY, world, user, effects.duration(level),effects.amplifier(level)/2)
            val success = AugmentHelper.findSpawnPos(world, startPos, totemOfFuryEntity, 3, 15, user.pitch, user.yaw)
            if (success) {
                totemOfFuryEntity.passEffects(spells, effects, level)
                list.add(totemOfFuryEntity)
            }
        }

        return list
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.BLOCK_DEEPSLATE_BRICKS_PLACE,SoundCategory.PLAYERS,1f,1f)
    }
}
