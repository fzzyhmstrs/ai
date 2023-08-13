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
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
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

/*
    Checklist
    - canTarget if entity spell
    - Build description for
        - Unique combinations
        - stat modifications
        - other type interactions
        - add Lang
    - provideArgs
    - spells are equal check
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

class CleanseAugment: SingleTargetOrSelfAugment(ScepterTier.ONE){
    override val augmentData: AugmentDatapoint = 
        AugmentDatapoint(AI.identity("cleanse"),SpellType.GRACE, PerLvlI(670,-20),45,
            1, 11,1,15, LoreTier.LOW_TIER, Items.MILK_BUCKET)

    //ml 11
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(190,10)

    override fun <T> canTarget(
        entityHitResult: EntityHitResult,
        context: ProcessContext,
        world: World,
        user: T,
        hand: Hand,
        spells: PairedAugments
    ): Boolean where T : SpellCastingEntity,T : LivingEntity {
        return SpellHelper.friendlyTarget(entityHitResult.entity,user,this)
    }

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        if (othersType.positiveEffect)
            description.addLang("enchantment.amethyst_imbuement.cleanse.desc.beneficial", SpellAdvancementChecks.STAT)
        if (othersType.negativeEffect)
            description.addLang("enchantment.amethyst_imbuement.cleanse.desc.negative", SpellAdvancementChecks.STAT)
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
        val entity = entityHitResult.entity
        if (othersType.positiveEffect && entity is LivingEntity){
            val statusCollection = entity.statusEffects
            val statusList = statusCollection.filter { !it.effectType.isBeneficial }.toList()
            val status = statusList[world.random.nextInt(statusList.size)]
            statusCollection.remove(status)
            entity.fireTicks = 0
            return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
        }
        if (othersType.negativeEffect && entity is LivingEntity){
            val statusCollection = entity.statusEffects
            val statusList = statusCollection.filter { it.effectType.isBeneficial && it.effectType != RegisterStatus.CURSED }.toList()
            val status = statusList[world.random.nextInt(statusList.size)]
            statusCollection.remove(status)
            entity.fireTicks = 0
            return SpellActionResult.success(AugmentHelper.APPLIED_NEGATIVE_EFFECTS)
        }
        if ((othersType.empty || spells.spellsAreEqual()) && entity is LivingEntity) {
            val statuses: MutableList<StatusEffectInstance> = mutableListOf()
            for (effect in entity.statusEffects) {
                if (effect.effectType.isBeneficial) {
                    if ((effect.effectType == RegisterStatus.CURSED && !spells.spellsAreEqual()) || effect.effectType != RegisterStatus.CURSED)
                        continue
                }
                statuses.add(effect)
            }
            for (effect in statuses) {
                entity.removeStatusEffect(effect.effectType)
            }
            entity.fireTicks = 0
            EffectQueue.addStatusToQueue(
                entity,
                RegisterStatus.IMMUNITY,
                effects.duration(level),
                effects.amplifier(level)
            )
            return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
        }
        return SUCCESSFUL_PASS
    }

    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.HAPPY_VILLAGER
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON,SoundCategory.PLAYERS,1f,1f)
    }
}
