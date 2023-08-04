package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.SingleTargetOrSelfAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.DamageSourceBuilder
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
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBoost
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.PROTECTED_EFFECT
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper.addStatus
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.PassiveEntity
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
class BarrierAugment: SingleTargetOrSelfAugment(ScepterTier.TWO){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("barrier"),SpellType.GRACE,600,50,
            10,11,1,15, LoreTier.NO_TIER, Items.SHIELD)

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withAmplifier(0,1)
            .withDuration(540,60)

    override fun <T> canTarget(entityHitResult: EntityHitResult, context: ProcessContext, world: World, user: T, hand: Hand, spells: PairedAugments)
            : Boolean where T : SpellCastingEntity, T : LivingEntity {
        return SpellHelper.friendlyTarget(entityHitResult.entity,user,this)
    }

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        when(other) {
            RegisterEnchantment.CREATE_LAVA -> {
                description.addLang("enchantment.amethyst_imbuement.barrier.create_lava.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.FLAME))
                return
            }
            RegisterEnchantment.CLEANSE -> {
                description.addLang("enchantment.amethyst_imbuement.barrier.cleanse.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.HEALTH))
                return
            }
            RegisterEnchantment.SOUL_MISSILE ->
                description.addLang("enchantment.amethyst_imbuement.barrier.soul_missile.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.PROJECTILE))
        }
        if (othersType.has(AugmentType.BENEFICIAL) && othersType.has(AugmentType.ENTITY))
            description.addLang("enchantment.amethyst_imbuement.barrier.desc.barrier", SpellAdvancementChecks.ENTITY_EFFECT.or(PROTECTED_EFFECT))
        if (othersType.has(AugmentType.SUMMONS))
            description.addLang("enchantment.amethyst_imbuement.barrier.desc.summon", SpellAdvancementChecks.SUMMONS.or(PROTECTED_EFFECT))
        description.addLang("enchantment.amethyst_imbuement.barrier.desc.pierce", SpellAdvancementChecks.ENTITY_EFFECT.or(SpellAdvancementChecks.DAMAGE))
        if (othersType.has(AugmentType.PROJECTILE) || othersType.negativeEffect)
            description.addLang("enchantment.amethyst_imbuement.barrier.desc.pierce2", SpellAdvancementChecks.HARMED_EFFECT)
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when(otherSpell) {
            RegisterEnchantment.CREATE_LAVA ->
                AcText.translatable("enchantment.amethyst_imbuement.barrier.create_lava")
            RegisterEnchantment.CLEANSE ->
                AcText.translatable("enchantment.amethyst_imbuement.barrier.cleanse")
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
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.PROTECTED_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DAMAGE_SOURCE_TRIGGER)
        SpellAdvancementChecks.grant(player,SpellAdvancementChecks.ENTITY_EFFECT_TRIGGER)
    }

    override fun modifyManaCost(
        manaCost: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (spells.spellsAreEqual())
            return manaCost.plus(0,0,25)
        return manaCost
    }

    override fun modifyAmplifier(
        amplifier: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (spells.spellsAreEqual())
            return amplifier.plus(1)
        return amplifier
    }

    override fun modifyDuration(
        duration: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (spells.spellsAreEqual())
            return duration.plus(160,40)
        if (other == RegisterEnchantment.CREATE_LAVA || other == RegisterEnchantment.CLEANSE)
            return PerLvlI(540,80)
        return duration
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
    )
    :
    SpellActionResult
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        if (spells.primary() == RegisterEnchantment.CREATE_LAVA){
            val target = RaycasterUtil.raycastHit(distance = effects.range(level),user)
            if (target is EntityHitResult){
                if (canTarget(target,context, world, user, hand, spells)){
                    if (target.addStatus(StatusEffects.FIRE_RESISTANCE,effects.duration(level))){
                        world.playSound(null,user.blockPos,SoundEvents.ENTITY_WANDERING_TRADER_DRINK_POTION,SoundCategory.PLAYERS,0.8f,0.8f)
                        return SpellActionResult.overwrite(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
                    }
                }
            } else {
                user.addStatusEffect(StatusEffectInstance(StatusEffects.FIRE_RESISTANCE,effects.duration(level)))
                world.playSound(null,user.blockPos,SoundEvents.ENTITY_WANDERING_TRADER_DRINK_POTION,SoundCategory.PLAYERS,0.8f,0.8f)
                return SpellActionResult.overwrite(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            }
        }
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
    )
    :
    SpellActionResult
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        val result = super.onEntityHit(entityHitResult, context, world, source, user, hand, level, effects, othersType, spells)
        if (result.acted() || !result.success())
            return result
        if (spells.primary() == RegisterEnchantment.CLEANSE){
            if (!entityHitResult.addStatus(RegisterStatus.IMMUNITY,effects.duration(level))){
                user.addStatusEffect(StatusEffectInstance(RegisterStatus.IMMUNITY,effects.duration(level)))
            }
            return SpellActionResult.overwrite(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
        }
        if (othersType.has(AugmentType.BENEFICIAL) && othersType.has(AugmentType.ENTITY)){
            if (entityHitResult.addStatus(StatusEffects.ABSORPTION, 400, 0))
                return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
        }
        if (othersType.has(AugmentType.PROJECTILE) || othersType.negativeEffect){
            if (!canTarget(entityHitResult, context, world, user, hand, spells)) return SUCCESSFUL_PASS
            val entity = entityHitResult.entity
            if (entity is LivingEntity) {
                val absorption = entity.absorptionAmount
                entity.absorptionAmount = max(0f,absorption - 2f)
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
    )
    :
    SpellActionResult
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        if (othersType.empty){
            val target = entityHitResult.entity
            if ((target is PassiveEntity || target is GolemEntity || target is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(user,target,this)) && target is LivingEntity) {
                EffectQueue.addStatusToQueue(target, StatusEffects.ABSORPTION, effects.duration(level), effects.amplifier(level)/5)
                return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            }
        }
        return SUCCESSFUL_PASS
    }

    override fun <T> onEntityKill(
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
        if (spells.primary() == RegisterEnchantment.SOUL_MISSILE){
            if (world.random.nextFloat() < 0.2){
                user.addStatusEffect(StatusEffectInstance(StatusEffects.ABSORPTION,300))
                return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            }
        }
        return SUCCESSFUL_PASS
    }

    override fun <T> modifyDamageSource(
        builder: DamageSourceBuilder,
        context: ProcessContext,
        entityHitResult: EntityHitResult,
        source: Entity?,
        user: T,
        world: World,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    )
    :
    DamageSourceBuilder
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        return builder.add(DamageTypes.FALLING_ANVIL)
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
    )
    :
    List<Entity>
    where
    T : ModifiableEffectEntity,
    T : Entity,
    U : SpellCastingEntity,
    U : LivingEntity
    {
        for (summon in summons){
            if (summon is LivingEntity) {
                val amp = if (spells.boost() == RegisterBoost.HEARTSTONE_BOOST) 1 else 0
                summon.addStatusEffect(StatusEffectInstance(StatusEffects.ABSORPTION, effects.duration(level), amp))
            }
        }
        return summons
    }

    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.END_ROD
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ITEM_ARMOR_EQUIP_CHAIN,SoundCategory.PLAYERS,1.0f,1.0f)
    }
}
