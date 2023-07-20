package me.fzzyhmstrs.amethyst_imbuement.spells.pieces

import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext

object ContextData {

    val CRIT = object : ProcessContext.Data<Boolean>("critical_strike", ProcessContext.BooleanDataType){}
    val SPEEDY = object : ProcessContext.Data<Float>("projectile_cooldown_fraction", ProcessContext.FloatDataType){}
    val HERO = object : ProcessContext.Data<Int>("hero_of_the_village", ProcessContext.IntDataType){}
    val DRACONIC_BOXES = object : ProcessContext.Data<Int>("draconic_boxes_found", ProcessContext.IntDataType){}

}