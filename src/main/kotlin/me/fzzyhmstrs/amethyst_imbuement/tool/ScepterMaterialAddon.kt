package me.fzzyhmstrs.amethyst_imbuement.tool

interface ScepterMaterialAddon {

    fun minCooldown(): Long{
        return 20L
    }
    fun baseCooldown(): Long{
        return 150L
    }

    fun healCooldown(): Long

}