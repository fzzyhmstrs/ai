package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.SingleTargetOrSelfAugment
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
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.golem.CholemEntity
import me.fzzyhmstrs.amethyst_imbuement.modifier.ModifierPredicates
import me.fzzyhmstrs.amethyst_imbuement.modifier.ModifierPredicates.isIn
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlD
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlF
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.item.Items
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
import kotlin.math.min

/*
    Checklist
     */

class ChickenformAugment: SingleTargetOrSelfAugment(ScepterTier.TWO){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI .identity("chickenform"),SpellType.GRACE, PerLvlI(720,-20),65,
            7,11,1,10, LoreTier.NO_TIER, Items.COOKED_CHICKEN)

    //ml 11
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(215,35).withAmplifier(1)

    override fun <T> canTarget(
        entityHitResult: EntityHitResult,
        context: ProcessContext,
        world: World,
        user: T,
        hand: Hand,
        spells: PairedAugments
    ): Boolean where T : SpellCastingEntity, T : LivingEntity {
        return SpellHelper.friendlyTarget(entityHitResult.entity,user,this)
    }

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        when(other) {
            RegisterEnchantment.SUMMON_GOLEM -> {
                description.addLang("enchantment.amethyst_imbuement.chickenform.summon_golem.desc1", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.CHICKEN))
                description.addLang("enchantment.amethyst_imbuement.chickenform.summon_golem.desc2", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.CHICKEN))
            }
            RegisterEnchantment.DASH -> {
                description.addLang("enchantment.amethyst_imbuement.chickenform.dash.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.DAMAGE))
                return
            }

        }
        if (othersType.positiveEffect)
            description.addLang("enchantment.amethyst_imbuement.chickenform.desc.positive", SpellAdvancementChecks.STAT)
        if (other.isIn(ModifierPredicates.CHICKEN_AUGMENTS))
            description.addLang("enchantment.amethyst_imbuement.chickenform.desc.chicken", SpellAdvancementChecks.CHICKEN)
        if (othersType.has(AugmentType.DAMAGE)) {
            description.addLang("enchantment.amethyst_imbuement.chickenform.desc.damage", SpellAdvancementChecks.DAMAGE)
            description.addLang("enchantment.amethyst_imbuement.chickenform.desc.kill", SpellAdvancementChecks.ON_KILL)
        }

    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        if (pair.spellsAreEqual()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DOUBLE_TRIGGER)
        }
        if (pair.spellsAreUnique()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.UNIQUE_TRIGGER)
        }
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.ENTITY_EFFECT_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.CHICKEN_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DAMAGE_TRIGGER)
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when (otherSpell) {
            RegisterEnchantment.SUMMON_GOLEM ->
                AcText.translatable("enchantment.amethyst_imbuement.chickenform.summon_golem")
            RegisterEnchantment.DASH ->
                AcText.translatable("enchantment.amethyst_imbuement.chickenform.dash")
            else ->
                super.specialName(otherSpell)
        }
    }

    override fun modifyDamage(
        damage: PerLvlF,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlF {
        if (other.isIn(ModifierPredicates.CHICKEN_AUGMENTS))
            damage.plus(1f)
        return damage
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
            return duration.plus(0,0,100)
        if (other.isIn(ModifierPredicates.CHICKEN_AUGMENTS))
            return duration.plus(0,0,10)
        if (other == RegisterEnchantment.SUMMON_GOLEM)
            return PerLvlI(AiConfig.entities.cholem.baseLifespan.get())
        return duration
    }

    override fun modifyRange(
        range: PerLvlD,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlD {
        if (other.isIn(ModifierPredicates.CHICKEN_AUGMENTS))
            range.plus(0.0,0.0,33.3)
        return range
    }

    override fun <T> modifyCount(
        start: Int,
        context: ProcessContext,
        user: T,
        world: World,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): Int where T : SpellCastingEntity, T : LivingEntity {
        if (spells.primary()?.isIn(ModifierPredicates.CHICKEN_AUGMENTS) == true)
            return start + 2
        return start
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
        if (othersType.has(AugmentType.DAMAGE) && world.random.nextFloat() < 0.05){
            if (user.hasStatusEffect(StatusEffects.SPEED)){
                val effect = user.getStatusEffect(StatusEffects.SPEED)
                val amp = min(effect?.amplifier?:0,4)
                val duration = effect?.duration?:0
                if (duration > 0){
                    val duration2 = if(duration < 200) {200} else {duration}
                    user.addStatusEffect(StatusEffectInstance(StatusEffects.SPEED,duration2,amp + 1))
                }
            } else {
                user.addStatusEffect(StatusEffectInstance(StatusEffects.SPEED, 200))
            }
            return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
        }
        if (spells.primary() == RegisterEnchantment.DASH){
            val target = entityHitResult.entity
            target.addVelocity(0.0,2.0,0.0)
            if (target is LivingEntity){
                target.addStatusEffect(StatusEffectInstance(StatusEffects.SLOW_FALLING, 200))
                return SpellActionResult.overwrite(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            }
        }
        if (othersType.positiveEffect){
            val target = entityHitResult.entity
            if (target is LivingEntity){
                target.addStatusEffect(StatusEffectInstance(StatusEffects.JUMP_BOOST, effects.duration(level), min(2,effects.amplifier(level))))
                target.addStatusEffect(StatusEffectInstance(StatusEffects.SLOW_FALLING, effects.duration(level)))
                return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
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
                target.addStatusEffect(StatusEffectInstance(StatusEffects.JUMP_BOOST, effects.duration(level), effects.amplifier(level)))
                target.addStatusEffect(StatusEffectInstance(StatusEffects.SPEED, effects.duration(level), effects.amplifier(level)))
                target.addStatusEffect(StatusEffectInstance(StatusEffects.SLOW_FALLING, effects.duration(level)))
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
        if (user.hasStatusEffect(StatusEffects.SPEED)){
            val effect = user.getStatusEffect(StatusEffects.SPEED)
            val amp = min(effect?.amplifier?:0,4)
            val duration = effect?.duration?:0
            if (duration > 0){
                val duration2 = if(duration < 200) {200} else {duration}
                user.addStatusEffect(StatusEffectInstance(StatusEffects.SPEED,duration2,amp + 1))
            }
        } else {
            user.addStatusEffect(StatusEffectInstance(StatusEffects.SPEED, 200))
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

        if (spells.primary() == RegisterEnchantment.SUMMON_GOLEM){
            val list: MutableList<CholemEntity> = mutableListOf()
            for (i in 1..3){
                val cholem = CholemEntity(RegisterEntity.CHOLEM_ENTITY,world,effects.duration(level),user)
                val startPos = BlockPos.ofFloored(hit.pos)
                val bl = AugmentHelper.findSpawnPos(world,startPos,cholem,4,16,user.pitch,user.yaw)
                if (bl){
                    cholem.passEffects(spells,effects,level)
                    list.add(cholem)
                }
            }
            return list
        }

        return summons
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_CHICKEN_AMBIENT,SoundCategory.PLAYERS,1.0f,1.0f)
    }
}
