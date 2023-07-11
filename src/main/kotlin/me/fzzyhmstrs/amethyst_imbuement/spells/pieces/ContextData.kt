package me.fzzyhmstrs.amethyst_imbuement.spells.pieces

import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext

object ContextData {

    val CRIT = object : ProcessContext.Data<Boolean>("critical_strike", ProcessContext.BooleanDataType){}
    val HERO = object : ProcessContext.Data<Int>("hero_of_the_village", ProcessContext.IntDataType){}

}