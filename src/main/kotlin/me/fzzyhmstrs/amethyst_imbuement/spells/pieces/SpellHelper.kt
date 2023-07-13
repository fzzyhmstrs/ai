package me.fzzyhmstrs.amethyst_imbuement.spells.pieces

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.PassiveEntity

object SpellHelper {

    val CHICKEN = AI.identity("chicken")

    fun friendlyTarget(target: Entity, user: LivingEntity, spell: ScepterAugment): Boolean{
        return  target !is Monster && (target is PassiveEntity || target is GolemEntity || target is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(user,target,spell))
    }

    fun hostileTarget(target: Entity, user: LivingEntity, spell: ScepterAugment): Boolean{
        return (target is Monster || target is SpellCastingEntity && !AiConfig.entities.isEntityPvpTeammate(user,target,spell))
    }

}