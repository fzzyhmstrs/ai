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
import me.fzzyhmstrs.amethyst_imbuement.mixins.MobEntityAccessor
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.ai.goal.ActiveTargetGoal
import net.minecraft.entity.ai.goal.PrioritizedGoal
import net.minecraft.entity.ai.goal.RevengeGoal
import net.minecraft.entity.ai.goal.UniversalAngerGoal
import net.minecraft.entity.boss.WitherEntity
import net.minecraft.entity.boss.dragon.EnderDragonEntity
import net.minecraft.entity.mob.Angerable
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class PersuadeAugment: SingleTargetAugment(ScepterTier.TWO) {
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("persuade"),SpellType.WIT, PerLvlI(2240,-40),300,
            20,11,1,65, LoreTier.NO_TIER, Items.COAL)

    //ml 11
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(360,40)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
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
        if (othersType.empty || spells.spellsAreEqual()){
            val entity = entityHitResult.entity
            return if (entity is MobEntity && entity !is WitherEntity && entity !is EnderDragonEntity ) {
                persuadeMob(entity,world,effects, level)
                SpellActionResult.success(AugmentHelper.APPLIED_NEGATIVE_EFFECTS)
            } else {
                FAIL
            }
        }
        return SUCCESSFUL_PASS
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO,SoundCategory.PLAYERS,1f,1f)
    }

    companion object Data: PersistentEffectHelper.PersistentEffect{
        fun persuadeMob(target: MobEntity, world: World,effects: AugmentEffect, level: Int){
            val targetSelector = (target as MobEntityAccessor).targetSelector
            val targets = targetSelector.goals.toMutableSet()
            targetSelector.clear {true}
            target.target = null
            if (target is PathAwareEntity)
                targetSelector.add(2, RevengeGoal(target, *arrayOfNulls(0)))
            targetSelector.add(1, ActiveTargetGoal(target, MobEntity::class.java, 5, false, false) { entity: LivingEntity? -> entity is Monster })
            val predicate = TargetPredicate.createAttackable()
            val newTarget = world.getClosestEntity(world.getEntitiesByClass(MobEntity::class.java,target.boundingBox.expand(16.0,6.0,16.0)) { true },predicate,target,target.x,target.eyeY,target.z)
            target.target = newTarget
            if (target is Angerable) {
                targetSelector.add(1, ActiveTargetGoal(target, PlayerEntity::class.java, 10, true, false) { entity: LivingEntity? -> target.shouldAngerAt(entity) })
                targetSelector.add(4, UniversalAngerGoal(target, true))
            }


            PersistentEffectHelper.setPersistentTickerNeed(this,effects.duration(level),effects.duration(level),PersuadePersistentEffectData(target,targets))
        }

        override val delay: PerLvlI
            get() = PerLvlI()

        override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
            if (data !is PersuadePersistentEffectData) return
            (data.entity as MobEntityAccessor).targetSelector.clear {true}
            for (prioritizedGoal in data.targets){
                (data.entity as MobEntityAccessor).targetSelector.add(prioritizedGoal.priority,prioritizedGoal.goal)
            }
        }

        class PersuadePersistentEffectData(val entity: MobEntity, val targets: Set<PrioritizedGoal>): PersistentEffectHelper.PersistentEffectData
    }
}
