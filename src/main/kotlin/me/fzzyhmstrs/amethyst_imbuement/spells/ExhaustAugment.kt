package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.SingleTargetAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.text.Text
import net.minecraft.world.World

class ExhaustAugment: SingleTargetAugment(ScepterTier.TWO){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("exhaust"),SpellType.GRACE, PerLvlI(360,-10),40,
            7,6,1,12,LoreTier.LOW_TIER, Items.FERMENTED_SPIDER_EYE)

    //ml 6
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(250,50,0)
            .withAmplifier(0,1,0)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideVerb(this))
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
        val entity = entityHitResult.entity
        if ((othersType.empty || spells.spellsAreEqual())) {
            var bl = entityHitResult.addStatus(StatusEffects.SLOWNESS,effect.duration(level),effect.amplifier(level+1)/2)
            if(bl && entityHitResult.addStatus(StatusEffects.WEAKNESS,effect.duration(level),effect.amplifier(level)/2))
                return SpellActionResult.success(AugmentHelper.APPLIED_NEGATIVE_EFFECTS)
        }
        return SUCCESSFUL_PASS
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.WITCH
    }
    
    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.WITCH
    }
    
    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK,SoundCategory.PLAYERS,1f,1f)
    }
}
