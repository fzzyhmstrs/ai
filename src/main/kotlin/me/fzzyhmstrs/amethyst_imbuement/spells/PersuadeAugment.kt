package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.SingleTargetAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.mixins.MobEntityAccessor
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import me.fzzyhmstrs.fzzy_core.entity_util.PlayerCreatable
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.boss.WitherEntity
import net.minecraft.entity.boss.dragon.EnderDragonEntity
import net.minecraft.entity.mob.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.world.World

class PersuadeAugment: SingleTargetAugment(ScepterTier.TWO), PersistentEffectHelper.PersistentEffect{
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("persuade"),SpellType.WIT, PerLvlI(2240,-40),300,
            20,imbueLevel,65, LoreTier.NO_TIER, Items.COAL)

    //ml 11
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(360,40)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        TODO("Not yet implemented")
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
    }

    override fun supportEffect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        return if(target != null) {
            if (target is Monster || target is HostileEntity || target is PlayerCreatable && target.entityOwner != user && AiConfig.entities.isEntityPvpTeammate(target.entityOwner, user,RegisterEnchantment.PERSUADE)) {
                if (target is MobEntity && target !is WitherEntity && target !is EnderDragonEntity ) {
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
                    world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        } else {
            false
        }
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO
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
