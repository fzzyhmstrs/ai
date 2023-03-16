package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MinorSupportAugment
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class CurseAugment: MinorSupportAugment(ScepterTier.TWO,15){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(225,45).withAmplifier(0,1)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT, PerLvlI(1920,-20),175,
            15,imbueLevel,25, LoreTier.LOW_TIER, Items.COAL_BLOCK)
    }

    override fun supportEffect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        return if(target != null) {
            if (target is Monster || target is HostileEntity || target is SpellCastingEntity && !AiConfig.entities.isEntityPvpTeammate(user,target,this)) {
                EffectQueue.addStatusToQueue(
                    target as LivingEntity,
                    RegisterStatus.CURSED,
                    effects.duration(level),
                    effects.amplifier(level)/3)
                world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
                effects.accept(target, AugmentConsumer.Type.HARMFUL)
                true
            } else {
                false
            }
        } else {
            false
        }
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN
    }
}