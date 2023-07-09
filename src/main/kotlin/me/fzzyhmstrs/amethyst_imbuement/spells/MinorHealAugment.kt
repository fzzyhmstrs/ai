package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.MinorSupportAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.world.World

class MinorHealAugment: MinorSupportAugment(ScepterTier.ONE,6){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(2.5F,0.5F,0.0F)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE, PerLvlI(130,-10),12,
            1,imbueLevel,5,LoreTier.LOW_TIER, Items.GLISTERING_MELON_SLICE)
    }

    override fun supportEffect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        if(target != null) {
            if (target is PassiveEntity || target is GolemEntity || target is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(user,target,this)) {
                val healthCheck = (target as LivingEntity).health
                target.heal(effects.damage(level))
                if (target.health == healthCheck) return false
                world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
                effects.accept(target, AugmentConsumer.Type.BENEFICIAL)
                return true
            }
        }

        val healthCheck = user.health
        user.heal(effects.damage(level))
        if (user.health == healthCheck) return false
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
        effects.accept(user,AugmentConsumer.Type.BENEFICIAL)
        return true
    }
}
