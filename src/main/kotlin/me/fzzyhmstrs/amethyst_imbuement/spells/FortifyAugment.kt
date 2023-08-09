package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.SingleTargetOrSelfAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper.addStatus
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Items
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.math.max

class FortifyAugment: SingleTargetOrSelfAugment(ScepterTier.TWO){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("fortify"),SpellType.GRACE, PerLvlI(1320,-20),135,
            13,11,1,20, LoreTier.LOW_TIER, Items.GOLDEN_APPLE)

    //ml 11
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(210,90).withAmplifier(-1,1)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
        SpellAdvancementChecks.grant(player,SpellAdvancementChecks.PROTECTED_TRIGGER)
    }

    override fun <T> entityEffects(
        entityHitResult: EntityHitResult,
        context: ProcessContext,
        world: World,
        source: Entity?,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): SpellActionResult where T : SpellCastingEntity, T : LivingEntity {
        if (othersType.empty || spells.spellsAreEqual()) {
            val bl = entityHitResult.addStatus(StatusEffects.RESISTANCE, effects.duration(level), max(effects.amplifier(level)/4,3))
            return if (bl && entityHitResult.addStatus(StatusEffects.STRENGTH, effects.duration(level), effects.amplifier(level)/4)){
                SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            } else {
                FAIL
            }
        }
        return SUCCESSFUL_PASS
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.SOUL_FIRE_FLAME
    }

    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.SOUL_FIRE_FLAME
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN,SoundCategory.PLAYERS,1.0f,1.0f)
    }
}
