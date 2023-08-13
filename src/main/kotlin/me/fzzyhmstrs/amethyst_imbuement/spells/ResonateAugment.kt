package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.ExplosionBuilder
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
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.MultiTargetAugment
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper.addEffect
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper.addStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects.ModifiableEffects
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.explosion_behaviors.ResonanceExplosionBehavior
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Items
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
import kotlin.math.max

/*
    Checklist
     */
class ResonateAugment: MultiTargetAugment(ScepterTier.THREE) {
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity( "resonate"),SpellType.FURY,18,18,
            18,5,1,1, LoreTier.NO_TIER, Items.NOTE_BLOCK)

    //ml 5
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(4.25F,0.75F,0.0F)
            .withRange(10.25,0.75,0.0)
            .withDuration(72,8)
            .withAmplifier(0,1,0)

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
        when(other){
            RegisterEnchantment.FORTIFY -> {
                description.addLang("enchantment.amethyst_imbuement.resonate.fortify.desc", SpellAdvancementChecks.UNIQUE)
                description.addLang("enchantment.amethyst_imbuement.resonate.fortify.desc2", SpellAdvancementChecks.UNIQUE)
            }
            RegisterEnchantment.INSPIRING_SONG ->
                description.addLang("enchantment.amethyst_imbuement.resonate.inspiring_song.desc", SpellAdvancementChecks.UNIQUE)
        }
        if (othersType.has(AugmentType.DAMAGE)) {
            description.addLang("enchantment.amethyst_imbuement.resonate.desc.damage", SpellAdvancementChecks.DAMAGE)
            description.addLang("enchantment.amethyst_imbuement.resonate.desc.damage2", SpellAdvancementChecks.DAMAGE)
        }
        if (othersType.has(AugmentType.BENEFICIAL))
            description.addLang("enchantment.amethyst_imbuement.resonate.desc.amplifier", SpellAdvancementChecks.STAT)
        if (othersType.has(AugmentType.SUMMONS))
            description.addLang("enchantment.amethyst_imbuement.resonate.desc.summons", SpellAdvancementChecks.STAT.or(SpellAdvancementChecks.SUMMONS))
        if (othersType.has(AugmentType.EXPLODES))
            description.addLang("enchantment.amethyst_imbuement.resonate.desc.explodes", SpellAdvancementChecks.EXPLODES)

    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when(otherSpell) {
            RegisterEnchantment.FORTIFY ->
                AcText.translatable("enchantment.amethyst_imbuement.resonate.fortify")
            RegisterEnchantment.INSPIRING_SONG ->
                AcText.translatable("enchantment.amethyst_imbuement.resonate.inspiring_song")
            else ->
                return super.specialName(otherSpell)
        }
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
        SpellAdvancementChecks.grant(player,SpellAdvancementChecks.DAMAGE_TRIGGER)
        SpellAdvancementChecks.grant(player,SpellAdvancementChecks.AMPLIFIER_TRIGGER)
        SpellAdvancementChecks.grant(player,SpellAdvancementChecks.STAT_TRIGGER)
    }

    override fun modifyCooldown(
        cooldown: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (spells.spellsAreEqual())
            return cooldown.plus(-4)
        return cooldown
    }

    override fun modifyAmplifier(
        amplifier: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (othersType.has(AugmentType.BENEFICIAL)){
            return amplifier.plus(2)
        }
        return amplifier
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
    )
    :
    Float
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        val entity = entityHitResult.entity
        if (entity !is LivingEntity) return amount
        val resonance = entity.getStatusEffect(RegisterStatus.RESONATING)
        if (resonance == null){
            entity.addStatusEffect(StatusEffectInstance(RegisterStatus.RESONATING,80,0))
        } else {
            val resonanceLevel = if(othersType.empty) resonance.amplifier + 1 else if (spells.spellsAreEqual())  resonance.amplifier + 2 else resonance.amplifier + world.random.nextInt(2)
            entity.removeStatusEffect(RegisterStatus.RESONATING)
            entity.addStatusEffect(StatusEffectInstance(RegisterStatus.RESONATING, 80, resonanceLevel))
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
    ): SpellActionResult where T : SpellCastingEntity,T : LivingEntity {
        val result = super.onEntityHit(entityHitResult, context, world, source, user, hand, level, effects, othersType, spells)
        if (result.acted() || !result.success())
            return result
        if (spells.primary() == RegisterEnchantment.INSPIRING_SONG){
            if(entityHitResult.addEffect(ModifiableEffectEntity.DAMAGE, ModifiableEffects.ECHO_EFFECT, effects.duration(level)))
                return SpellActionResult.overwrite(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
        }
        if (spells.primary() == RegisterEnchantment.FORTIFY){
            val bl = entityHitResult.addEffect(ModifiableEffectEntity.DAMAGE, ModifiableEffects.ECHO_EFFECT, effects.duration(level))
            if(bl && entityHitResult.addStatus(StatusEffects.RESISTANCE,effects.duration(level), max(effects.amplifier(level)/6,2)))
                return SpellActionResult.overwrite(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
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
    ): List<Entity> where T : ModifiableEffectEntity,T : Entity, U : SpellCastingEntity,U : LivingEntity {
        for (summon in summons){
            summon.addEffect(ModifiableEffectEntity.DAMAGE,ModifiableEffects.ECHO_EFFECT)
        }
        return summons
    }

    override fun modifyExplosion(
        builder: ExplosionBuilder,
        context: ProcessContext,
        user: LivingEntity?,
        world: World,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): ExplosionBuilder {
        return builder.withCustomBehavior(ResonanceExplosionBehavior())
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.BLOCK_BELL_RESONATE,SoundCategory.PLAYERS,1f,1f)
    }

    override fun hitSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.BLOCK_BELL_RESONATE,SoundCategory.PLAYERS,1f,1f)
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.ELECTRIC_SPARK
    }

    override fun particleSpeed(): Double {
        return 3.0
    }
}
