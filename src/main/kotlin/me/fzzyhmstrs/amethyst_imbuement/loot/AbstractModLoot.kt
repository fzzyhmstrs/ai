package me.fzzyhmstrs.amethyst_imbuement.loot

import net.minecraft.loot.LootTable
import net.minecraft.util.Identifier

@Deprecated("moving to amethyst_core")
interface AbstractModLoot {

    fun lootBuilder(id: Identifier, table: LootTable.Builder): Boolean

}