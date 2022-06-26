package me.fzzyhmstrs.amethyst_imbuement.loot

import me.fzzyhmstrs.amethyst_core.misc_util.AbstractModLoot
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterLoot
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder
import net.minecraft.item.Items
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.util.Identifier

object TowersOfTheWildReworkedLoot: AbstractModLoot {

    override fun lootBuilder(id: Identifier, table: FabricLootSupplierBuilder): Boolean {
        if (id.namespace != "towers_of_the_wild_reworked") return false
        return when (id) {
            Identifier("towers_of_the_wild_reworked","chests/tower/ocean/ocean_tower_chest") -> {
                towerLoot(table)
                true
            }
            Identifier("towers_of_the_wild_reworked","chests/tower/regular/tower_chest") -> {
                towerLoot(table)
                true
            }
            else -> {
                false
            }
        }
    }

    private fun towerLoot(table: FabricLootSupplierBuilder){
        val poolBuilder = RegisterLoot.tierOneGemPool(2.0F,0.5F)
        table.pool(poolBuilder)
        val poolBuilder2 = RegisterLoot.tierTwoGemPool(1.0F,0.1F)
        table.pool(poolBuilder2)
        val poolBuilder3 = FabricLootPoolBuilder.builder()
            .rolls(UniformLootNumberProvider.create(1.0F,2.0F))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_LORE).weight(1))
            .with(ItemEntry.builder(Items.AIR).weight(19))
        table.pool(poolBuilder3)
    }
}