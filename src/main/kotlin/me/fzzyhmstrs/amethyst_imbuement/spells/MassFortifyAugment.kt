package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.EntityAoeAugment
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
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World
import kotlin.math.max

class MassFortifyAugment: EntityAoeAugment(ScepterTier.THREE,true){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("mass_fortify"),SpellType.GRACE,1200,275,
            22,9,1,35,LoreTier.HIGH_TIER, Items.GOLDEN_APPLE)

    //ml 9
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(700,100,0)
            .withAmplifier(-1,1,0)
            .withRange(9.0,1.0,0.0)

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
        if ((othersType.empty || spells.spellsAreEqual())) {
            val bl = entityHitResult.addStatus(StatusEffects.RESISTANCE, effect.duration(level), max(effect.amplifier((level-1)/4+3),3))
            if (bl && entityHitResult.addStatus(StatusEffects.STRENGTH, effect.duration(level), max(effect.amplifier((level-1)/4+3),3))
            return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
        }
        return SUCCESSFUL_PASS
    }
    
    override fun effect(
        world: World,
        user: LivingEntity,
        entityList: MutableList<Entity>,
        level: Int,
        effect: AugmentEffect
    ): Boolean {
        var successes = 0

        if (entityList.isEmpty()){
            successes++
            EffectQueue.addStatusToQueue(user,StatusEffects.RESISTANCE, effect.duration(level), max(effect.amplifier((level-1)/4+3),3))
            EffectQueue.addStatusToQueue(user,StatusEffects.STRENGTH, (effect.duration(level) * 1.25).toInt(), effect.amplifier((level-1)/4))
            effect.accept(user, AugmentConsumer.Type.BENEFICIAL)
        } else {
            entityList.add(user)
            for (entity3 in entityList) {
                if (entity3 !is Monster && entity3 !is PassiveEntity && entity3 is LivingEntity) {
                    if (entity3 is SpellCastingEntity && !AiConfig.entities.isEntityPvpTeammate(user,entity3,this)) continue
                    successes++
                    EffectQueue.addStatusToQueue(entity3, StatusEffects.RESISTANCE, effect.duration(level),  max(effect.amplifier((level-1)/4+2),2))
                    EffectQueue.addStatusToQueue(entity3, StatusEffects.STRENGTH,  effect.duration(level), effect.amplifier((level-1)/4))
                    effect.accept(entity3,AugmentConsumer.Type.BENEFICIAL)
                }
            }
        }
        return successes > 0
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.ENCHANTED_HIT
    }
    
    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.ENCHANTED_HIT
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.BLOCK_BEACON_ACTIVATE,SoundCategory.PLAYERS,1f,1f)
    }
}
