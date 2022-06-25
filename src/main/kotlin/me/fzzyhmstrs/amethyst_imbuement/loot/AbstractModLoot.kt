package me.fzzyhmstrs.amethyst_imbuement.loot

import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder
import net.minecraft.util.Identifier

@Deprecated("moving to amethyst_core")
interface AbstractModLoot {

    fun lootBuilder(id: Identifier, table: FabricLootSupplierBuilder): Boolean

}