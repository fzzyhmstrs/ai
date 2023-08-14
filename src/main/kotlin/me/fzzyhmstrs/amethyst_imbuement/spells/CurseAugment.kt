package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.SingleTargetAugment
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
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.BOOSTED_TRIGGER
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.HARMED_TRIGGER
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.and
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper.addStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper.getStatus
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class CurseAugment: SingleTargetAugment(ScepterTier.THREE){

    //ml 16
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("curse"),SpellType.WIT, PerLvlI(1920,-20),400,
            20,16,1,65, LoreTier.HIGH_TIER, RegisterItem.ACCURSED_FIGURINE)

    //ml 16
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(360,40).withAmplifier(0,1)

    override fun <T> canTarget(
        entityHitResult: EntityHitResult,
        context: ProcessContext,
        world: World,
        user: T,
        hand: Hand,
        spells: PairedAugments
    ): Boolean where T : SpellCastingEntity,T : LivingEntity {
        return SpellHelper.hostileTarget(entityHitResult.entity,user,this)
    }

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        when(other) {
            RegisterEnchantment.EXHAUST -> {
                description.addLang("enchantment.amethyst_imbuement.curse.exhaust.desc", SpellAdvancementChecks.UNIQUE.and(SpellAdvancementChecks.HARMED_EFFECT))
                return
            }
            RegisterEnchantment.MINOR_HEAL -> {
                description.addLang("enchantment.amethyst_imbuement.curse.minor_heal.desc", SpellAdvancementChecks.UNIQUE.and(SpellAdvancementChecks.HARMED_EFFECT))
                return
            }
        }
        if (othersType.positiveEffect)
            description.addLang("enchantment.amethyst_imbuement.curse.desc.positive", SpellAdvancementChecks.PROTECTED_EFFECT)
        if (othersType.negativeEffect)
            description.addLang("enchantment.amethyst_imbuement.curse.desc.negative", SpellAdvancementChecks.BOOSTED_EFFECT)
        if (othersType == AugmentType.DIRECTED_ENERGY || other.getTier() >= 3) {
            description.addLang("enchantment.amethyst_imbuement.curse.desc.directedEnergy", SpellAdvancementChecks.BOOSTED_EFFECT.or(SpellAdvancementChecks.HARMED_EFFECT)
            )
        } else if (othersType.has(AugmentType.DAMAGE)) {
            description.addLang("enchantment.amethyst_imbuement.curse.desc.damage", SpellAdvancementChecks.BOOSTED_EFFECT.or(SpellAdvancementChecks.HARMED_EFFECT))
        }
        if (othersType.has(AugmentType.DAMAGE))
            description.addLang("enchantment.amethyst_imbuement.curse.desc.damage2", SpellAdvancementChecks.DAMAGE)
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
        SpellAdvancementChecks.grant(player, BOOSTED_TRIGGER)
        SpellAdvancementChecks.grant(player, HARMED_TRIGGER)
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when(otherSpell) {
            RegisterEnchantment.EXHAUST ->
                AcText.translatable("enchantment.amethyst_imbuement.curse.exhaust")
            RegisterEnchantment.MINOR_HEAL ->
                AcText.translatable("enchantment.amethyst_imbuement.curse.minor_heal")
            else ->
                super.specialName(otherSpell)
        }
    }

    override fun modifyManaCost(
        manaCost: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (other == RegisterEnchantment.MINOR_HEAL) {
            return manaCost.plus(12)
        } else if (othersType.has(AugmentType.DAMAGE)) {
            return manaCost.plus(0,0,10)
        }
        return manaCost
    }

    override fun modifyAmplifier(
        amplifier: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (othersType.negativeEffect)
            return amplifier.plus(2)
        return amplifier
    }

    override fun modifyDuration(
        duration: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (other == RegisterEnchantment.EXHAUST)
            return duration.plus(0,0,100)
        return duration
    }

    override fun <T> modifyDealtDamage(
        amount: Float,
        context: ProcessContext,
        entityHitResult: EntityHitResult,
        user: T,
        world: World,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): Float where T : SpellCastingEntity, T : LivingEntity {
        val entity = entityHitResult.entity
        if (entity is LivingEntity){
            val cursed = entity.getStatusEffect(RegisterStatus.CURSED)?.amplifier?:-1
            return amount * (1f + (0.1f * (cursed + 1).toFloat()))
        }
        return amount
    }

    override fun <T> onEntityHit(
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
        val result = super.onEntityHit(entityHitResult, context, world, source, user, hand, level, effects, othersType, spells)
        if (result.acted() || !result.success())
            return result
        if (spells.primary() == RegisterEnchantment.EXHAUST){
            if(entityHitResult.addStatus(StatusEffects.WEAKNESS,effects.duration(level),effects.amplifier(level)))
                return SpellActionResult.overwrite(AugmentHelper.APPLIED_NEGATIVE_EFFECTS)
        }
        if (spells.primary() == RegisterEnchantment.MINOR_HEAL){
            if(entityHitResult.addStatus(StatusEffects.ABSORPTION, -1, 3))
                return SpellActionResult.overwrite(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
        }
        if (othersType.positiveEffect){
            val entity = entityHitResult.entity
            if (entity is LivingEntity){
                entity.removeStatusEffect(RegisterStatus.CURSED)
                return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            }
        }
        if (othersType == AugmentType.DIRECTED_ENERGY || (spells.primary()?.getTier() ?: 1) >= 3){
            val cursed = entityHitResult.getStatus(RegisterStatus.CURSED)
            val amp = cursed?.amplifier?.plus(world.random.nextInt(2)) ?: 1
            entityHitResult.addStatus(RegisterStatus.CURSED,100,amp)
            return SpellActionResult.success(AugmentHelper.APPLIED_NEGATIVE_EFFECTS)
        } else if (othersType.has(AugmentType.DAMAGE)){
            if (world.random.nextFloat() < 0.333333f) {
                entityHitResult.addStatus(RegisterStatus.CURSED, 50, 0)
                return SpellActionResult.success(AugmentHelper.APPLIED_NEGATIVE_EFFECTS)
            }
        }

        return SUCCESSFUL_PASS
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
            return if (entityHitResult.addStatus(RegisterStatus.CURSED,effects.duration(level),(effects.amplifier(level + 1)/4) + 1)) {
                SpellActionResult.success(AugmentHelper.APPLIED_NEGATIVE_EFFECTS)
            } else {
                FAIL
            }
        }
        return SUCCESSFUL_PASS
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.SMOKE
    }
    
    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.SMOKE
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN,SoundCategory.PLAYERS,1.0f,1.0f)
    }
}
