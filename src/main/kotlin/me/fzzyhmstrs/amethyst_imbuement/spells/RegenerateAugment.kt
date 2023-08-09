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
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper.addStatus
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Items
import net.minecraft.particle.DustParticleEffect
import net.minecraft.particle.ParticleEffect
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class RegenerateAugment: SingleTargetOrSelfAugment(ScepterTier.ONE){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("regenerate"),SpellType.GRACE, PerLvlI(835,-5),60,
            1,17,1,15,LoreTier.NO_TIER, Items.GHAST_TEAR)

    //ml 17
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(384,12)
            .withAmplifier(0)
            .withDamage(-1.0f,0.2f)

    override fun <T> canTarget(entityHitResult: EntityHitResult, context: ProcessContext, world: World, user: T, hand: Hand, spells: PairedAugments)
            : Boolean where T : SpellCastingEntity, T : LivingEntity {
        return SpellHelper.friendlyTarget(entityHitResult.entity,user,this)
    }
            
    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
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
        if ((othersType.empty || spells.spellsAreEqual())) {
            if (entityHitResult.addStatus(StatusEffects.REGENERATION, effects.duration(level), effects.amplifier(level) + effects.damage(level).toInt())){
                return SpellActionResult.success(AugmentHelper.APPLIED_NEGATIVE_EFFECTS)
            } else {
                return FAIL
            }
        }
        return SUCCESSFUL_PASS
    }

    override fun castParticleType(): ParticleEffect? {
        return DustParticleEffect.DEFAULT
    }
    
    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return DustParticleEffect.DEFAULT
    }
    
    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.BLOCK_CONDUIT_AMBIENT,SoundCategory.PLAYERS, 0.6F, 1.2F)
    }
}
