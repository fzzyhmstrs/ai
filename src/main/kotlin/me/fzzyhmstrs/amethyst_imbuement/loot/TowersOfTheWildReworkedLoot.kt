package me.fzzyhmstrs.amethyst_imbuement.loot

import me.fzzyhmstrs.amethyst_core.item_util.AbstractModLoot
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterLoot
import net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.util.Identifier

object TowersOfTheWildReworkedLoot: AbstractModLoot() {
    override val targetNameSpace: String = "towers_of_the_wild_reworked"

    override fun lootBuilder(id: Identifier, table: FabricLootTableBuilder): Boolean {
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

    private fun towerLoot(table: FabricLootTableBuilder){
        val poolBuilder = RegisterLoot.tierOneGemPool(2.0F,0.5F)
        table.pool(poolBuilder.build())
        val poolBuilder2 = RegisterLoot.tierTwoGemPool(1.0F,0.05F)
        table.pool(poolBuilder2.build())
        val poolBuilder3 = LootPool.builder()
            .rolls(UniformLootNumberProvider.create(1.0F,2.0F))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_LORE).weight(1))
            .with(ItemEntry.builder(Items.AIR).weight(19))
        table.pool(poolBuilder3.build())
    }
}