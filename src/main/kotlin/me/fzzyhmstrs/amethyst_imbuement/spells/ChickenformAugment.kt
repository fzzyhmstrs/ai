package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.SingleTargetOrSelfAugment
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
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.world.World

class ChickenformAugment: SingleTargetOrSelfAugment(ScepterTier.TWO){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI .identity("chickenform"),SpellType.GRACE, PerLvlI(720,-20),65,
            7,11,1,10, LoreTier.NO_TIER, Items.COOKED_CHICKEN)

    //ml 11
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(215,35).withAmplifier(1)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        TODO("Not yet implemented")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        TODO()
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
        if(target != null) {
            if ((target is PassiveEntity || target is GolemEntity || target is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(user,target,this)) && target is LivingEntity) {
                target.addStatusEffect(StatusEffectInstance(StatusEffects.JUMP_BOOST, effects.duration(level), effects.amplifier(level)))
                target.addStatusEffect(StatusEffectInstance(StatusEffects.SPEED, effects.duration(level), effects.amplifier(level)))
                target.addStatusEffect(StatusEffectInstance(StatusEffects.SLOW_FALLING, effects.duration(level)))
                effects.accept(target, AugmentConsumer.Type.BENEFICIAL)
                world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.6F, 1.2F)
                return true
            }
        }
        user.addStatusEffect(StatusEffectInstance(StatusEffects.JUMP_BOOST, effects.duration(level), effects.amplifier(level)))
        user.addStatusEffect(StatusEffectInstance(StatusEffects.SPEED, effects.duration(level), effects.amplifier(level)))
        user.addStatusEffect(StatusEffectInstance(StatusEffects.SLOW_FALLING, effects.duration(level)))
        effects.accept(user, AugmentConsumer.Type.BENEFICIAL)
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.6F, 1.2F)
        return true
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_CHICKEN_AMBIENT
    }
}
