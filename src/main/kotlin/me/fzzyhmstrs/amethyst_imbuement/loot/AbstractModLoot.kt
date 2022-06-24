package me.fzzyhmstrs.amethyst_imbuement.loot

import net.minecraft.loot.LootTable
import net.minecraft.util.Identifier

interface AbstractModLoot {

    fun lootBuilder(id: Identifier, table: LootTable.Builder): Boolean

}