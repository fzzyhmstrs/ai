package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.EntityAoeAugment
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
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper.addStatus
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
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

class MassRevivifyAugment: EntityAoeAugment(ScepterTier.THREE,true){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("mass_revivify"),SpellType.GRACE,300,150,
            29,5,1,12,LoreTier.HIGH_TIER, RegisterItem.GOLDEN_HEART)

    //ml 5
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(9.0,1.0,0.0)
            .withAmplifier(0,1,0)
            .withDuration(80,180,0)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun filter(list: List<Entity>, user: LivingEntity): MutableList<EntityHitResult> {
        return SpellHelper.friendlyFilter(list, user, this)
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
        if (othersType.empty && entity is LivingEntity) {
            var successes = 0
            val bl = entityHitResult.addStatus(StatusEffects.REGENERATION,effects.duration(level), effects.amplifier(1))
            if(bl && entityHitResult.addStatus(StatusEffects.ABSORPTION, effects.duration(level + 3), effects.amplifier(level)))
                successes++
            if (entity.health < entity.maxHealth){
                entity.heal(effects.damage(level))
                successes++
            }
            val statuses: MutableList<StatusEffectInstance> = mutableListOf()
            if (entity.statusEffects.isNotEmpty()){
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
                successes++
            }
            return if (successes > 0){
                SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            } else {
                FAIL
            }
        }
        return SUCCESSFUL_PASS
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.COMPOSTER
    }
    
    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.COMPOSTER
    }
    
    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, SoundCategory.PLAYERS,1f,1f)
    }
}
