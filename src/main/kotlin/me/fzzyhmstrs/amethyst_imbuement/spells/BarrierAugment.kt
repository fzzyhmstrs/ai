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
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
/*
    Checklist
    - canTarget if entity spell
    - Build description for
        - Unique combinations
        - stat modifications
        - other type interactions
        - add Lang
    - provideArgs
    - special names for uniques
    - onPaired to grant relevant adv.
    - implement all special combinations
    - fill up interaction methods
        - onEntityHit?
        - onEntityKill?
        - onBlockHit?
        - Remember to call and check results of the super for the "default" behavior
    - modify stats. don't forget mana cost and cooldown!
        - modifyDealtDamage for unique interactions
    - modifyDamageSource?
        - remember DamageSourceBuilder for a default damage source
    - modify other things
        - summon?
        - projectile?
        - explosion?
        - drops?
        - count? (affects some things like summon count and projectile count)
    - sound and particles
     */
class BarrierAugment: SingleTargetOrSelfAugment(ScepterTier.TWO){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("barrier"),SpellType.GRACE,600,50,
            10,10,1,15, LoreTier.NO_TIER, Items.SHIELD)

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withAmplifier(0,1)
            .withDuration(540,80)

    override fun <T> canTarget(entityHitResult: EntityHitResult, context: ProcessContext, world: World, user: T, hand: Hand, spells: PairedAugments)
            : Boolean where T : SpellCastingEntity, T : LivingEntity {
        return SpellHelper.friendlyTarget(entityHitResult.entity,user,this)
    }

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        if (othersType.has(AugmentType.BENEFICIAL) && othersType.has(AugmentType.ENTITY))
            description.addLang("enchantment.amethyst_imbuement.barrier.desc.barrier", SpellAdvancementChecks.ENTITY_EFFECT)
        if (othersType.has(AugmentType.PROJECTILE))
            description.addLang("enchantment.amethyst_imbuement.barrier.desc.pierce", SpellAdvancementChecks.ENTITY_EFFECT.or(SpellAdvancementChecks.DAMAGE))
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
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.PROTECTED_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DAMAGE_SOURCE_TRIGGER)
        SpellAdvancementChecks.grant(player,SpellAdvancementChecks.ENTITY_EFFECT_TRIGGER)
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
        if (othersType.has(AugmentType.BENEFICIAL) && othersType.has(AugmentType.ENTITY)){
            val target = entityHitResult.entity
            if (target is LivingEntity){
                EffectQueue.addStatusToQueue(target, StatusEffects.ABSORPTION, 400, 0)
                return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            }
        }
        if (othersType.has(AugmentType.BENEFICIAL) && othersType.has(AugmentType.ENTITY)){
            if (!canTarget(entityHitResult, context, world, user, hand, spells)) return SUCCESSFUL_PASS

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

   /* override fun supportEffect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        if(target != null) {
            if ((target is PassiveEntity || target is GolemEntity || target is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(user,target,this)) && target is LivingEntity) {
                EffectQueue.addStatusToQueue(target, StatusEffects.ABSORPTION, effects.duration(level), effects.amplifier(level)/5)
                effects.accept(target, AugmentConsumer.Type.BENEFICIAL)
                world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
                return true
            }
        }
        EffectQueue.addStatusToQueue(user, StatusEffects.ABSORPTION, effects.duration(level), effects.amplifier(level)/5)
        effects.accept(user,AugmentConsumer.Type.BENEFICIAL)
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
        return true
    }*/

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ITEM_ARMOR_EQUIP_CHAIN,SoundCategory.PLAYERS,1.0f,1.0f)
    }
}
