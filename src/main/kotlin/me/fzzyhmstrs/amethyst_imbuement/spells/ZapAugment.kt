package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.BeamAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.modifier.ModifierPredicates
import me.fzzyhmstrs.amethyst_imbuement.modifier.ModifierPredicates.isIn
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects.ModifiableEffects
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlD
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
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

class ZapAugment: BeamAugment(ScepterTier.ONE){
    override val augmentData: AugmentDatapoint =
         AugmentDatapoint(AI.identity("zap"),SpellType.FURY,18,6,
            1,11,1,1, LoreTier.NO_TIER, RegisterItem.BERYL_COPPER_INGOT)

    //ml 11
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(6.8,0.2).withDamage(3.4f,0.1f)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        if (othersType.has(AugmentType.DAMAGE))
            description.addLang("enchantment.amethyst_imbuement.zap.desc.stun", SpellAdvancementChecks.LIGHTNING.or(SpellAdvancementChecks.STUNS))
        if (othersType.has(AugmentType.SUMMONS))
            description.addLang("enchantment.amethyst_imbuement.zap.desc.summons", SpellAdvancementChecks.LIGHTNING.or(SpellAdvancementChecks.SUMMONS))
        if (othersType == AugmentType.DIRECTED_ENERGY || othersType == AugmentType.AOE_POSITIVE)
            description.addLang("enchantment.amethyst_imbuement.zap.desc.range", SpellAdvancementChecks.RANGE)
        if (other.isIn(ModifierPredicates.LIGHTNING_AUGMENTS))
            description.addLang("enchantment.amethyst_imbuement.zap.desc.lightning", SpellAdvancementChecks.LIGHTNING.or(SpellAdvancementChecks.AMPLIFIER))
        when(other) {
            RegisterEnchantment.SUMMON_ZOMBIE -> {
                description.addLang("enchantment.amethyst_imbuement.zap.summon_zombie.desc1", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.CHICKEN))
                description.addLang("enchantment.amethyst_imbuement.zap.summon_zombie.desc2", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.CHICKEN))
            }
            RegisterEnchantment.SPECTRAL_SLASH ->
                description.addLang("enchantment.amethyst_imbuement.zap.spectral_slash.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.DAMAGE))

        }
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when (otherSpell) {
            RegisterEnchantment.SUMMON_GOLEM ->
                AcText.translatable("enchantment.amethyst_imbuement.zap.summon_golem")
            RegisterEnchantment.SPECTRAL_SLASH ->
                AcText.translatable("enchantment.amethyst_imbuement.zap.spectral_slash")
            else ->
                super.specialName(otherSpell)
        }
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        if (pair.spellsAreEqual()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DOUBLE_TRIGGER)
        }
        if (pair.spellsAreUnique()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.UNIQUE_TRIGGER)
        }
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.LIGHTNING_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.RANGE_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.STUNNED_TRIGGER)
    }

    override fun modifyAmplifier(
        amplifier: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (other.isIn(ModifierPredicates.LIGHTNING_AUGMENTS))
            return amplifier.plus(2)
        return amplifier
    }

    override fun modifyRange(
        range: PerLvlD,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlD {
        if (spells.spellsAreEqual())
            return range.plus(0.0,0.0,50.0)
        if (othersType == AugmentType.DIRECTED_ENERGY || othersType == AugmentType.AOE_POSITIVE)
            return range.plus(0.0,0.0,35.0)
        return super.modifyRange(range, other, othersType, spells)
    }

    override fun <T> onCast(
        context: ProcessContext,
        world: World,
        source: Entity?,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): SpellActionResult where T : SpellCastingEntity,T : LivingEntity {
        return super.onCast(context, world, source, user, hand, level, effects, othersType, spells)
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
        val livingEntity = entityHitResult.entity
        if (othersType.has(AugmentType.DAMAGE) && livingEntity is LivingEntity){
            if (AI.aiRandom().nextFloat() < 0.1f){
                livingEntity.addStatusEffect(StatusEffectInstance(RegisterStatus.STUNNED,60))
                return SpellActionResult.success(AugmentHelper.APPLIED_NEGATIVE_EFFECTS)
            }
        }
        return SUCCESSFUL_PASS
    }

    override fun <T, U> modifySummons(
        summons: List<T>,
        hit: HitResult,
        context: ProcessContext,
        user: U,
        world: World,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): List<Entity> where T : ModifiableEffectEntity, T : Entity, U : SpellCastingEntity, U : LivingEntity {
        for (summon in summons){
            summon.addEffect(ModifiableEffectEntity.ON_REMOVED,ModifiableEffects.STATIC_SHOCK_EFFECT)
        }
        return summons
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.ELECTRIC_SPARK
    }

    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.ELECTRIC_SPARK
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null, blockPos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(), SoundCategory.PLAYERS,1.0f,world.random.nextFloat() * 0.8f + 0.4f)
    }
}
