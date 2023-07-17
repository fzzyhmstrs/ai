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
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.Monster
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World

class MassExhaustAugment: EntityAoeAugment(ScepterTier.THREE,false) {
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("mass_exhaust"),SpellType.GRACE, PerLvlI(450,-50),80,
            16,3,1,13,LoreTier.HIGH_TIER, Items.FERMENTED_SPIDER_EYE)

    //ml 3
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(12.0,0.0,0.0)
            .withDuration(240,100,0)
            .withAmplifier(0,1,0)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        TODO("Not yet implemented")
    }

    override fun filter(list: List<Entity>, user: LivingEntity): MutableList<EntityHitResult> {
        TODO("Not yet implemented")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        if (pair.spellsAreEqual()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DOUBLE_TRIGGER)
        }
        if (pair.spellsAreUnique()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.UNIQUE_TRIGGER)
        }
    }

    override fun effect(
        world: World,
        user: LivingEntity,
        entityList: MutableList<Entity>,
        level: Int,
        effect: AugmentEffect
    ): Boolean {
        if (entityList.isEmpty()) return false
        var successes = 0
        for (entity3 in entityList) {
            if(entity3 is Monster || entity3 is SpellCastingEntity && !AiConfig.entities.isEntityPvpTeammate(user,entity3,this)){
                if (entity3 is LivingEntity){
                    successes++
                    EffectQueue.addStatusToQueue(entity3,StatusEffects.SLOWNESS,effect.duration(level),effect.amplifier(level+ 1))
                    EffectQueue.addStatusToQueue(entity3,StatusEffects.WEAKNESS,effect.duration(level),effect.amplifier(level))
                    effect.accept(entity3, AugmentConsumer.Type.HARMFUL)
                }
            }
        }
        val bl = successes > 0
        if (bl){
            effect.accept(user,AugmentConsumer.Type.BENEFICIAL)
        }
        return bl
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK
    }
}
