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
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class MassCleanseAugment: EntityAoeAugment(ScepterTier.TWO,true){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("mass_cleanse"),SpellType.GRACE,1200,120,
            15,5,1,30,LoreTier.LOW_TIER, Items.MILK_BUCKET)

    //ml 5
    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDuration(200,100)
            .withRange(7.0,1.0)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun filter(list: List<Entity>, user: LivingEntity): MutableList<EntityHitResult> {
        return SpellHelper.friendlyFilter(list, user, this)
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
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
            val statuses: MutableList<StatusEffectInstance> = mutableListOf()
            if (entity.statusEffects.isEmpty()) return FAIL
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

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON,SoundCategory.PLAYERS,1f,1f)
    }
}
