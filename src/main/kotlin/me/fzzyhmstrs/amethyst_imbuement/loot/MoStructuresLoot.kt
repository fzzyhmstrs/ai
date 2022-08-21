package me.fzzyhmstrs.amethyst_imbuement.loot

import me.fzzyhmstrs.amethyst_core.item_util.AbstractModLoot
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterLoot
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder
import net.minecraft.item.Items
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.util.Identifier

object MoStructuresLoot: AbstractModLoot() {
    override val targetNameSpace: String = "mostructures"

    override fun lootBuilder(id: Identifier, table: FabricLootSupplierBuilder): Boolean{
        when (id) {
            Identifier("mostructures","bunny_skeleton") -> {
                VanillaLoot.shipwreckTreasureLoot(table)
                return true
            }
            Identifier("mostructures","bunny_spider") -> {
                VanillaLoot.shipwreckTreasureLoot(table)
                return true
            }
            Identifier("mostructures","ice_tower") -> {
                val poolBuilder = RegisterLoot.tierOneGemPool(3.0F, 0.20F)
                table.pool(poolBuilder)
                return true
            }
            Identifier("mostructures","barnhouse_3") -> {
                val poolBuilder = FabricLootPoolBuilder.builder()
                    .rolls(UniformLootNumberProvider.create(1.0F,4.0F))
                    .with(ItemEntry.builder(RegisterItem.XP_BUSH_SEED).weight(1))
                    .with(ItemEntry.builder(Items.AIR).weight(19))
                table.pool(poolBuilder)
                return true
            }
            Identifier("mostructures","jungle_temple_treasure") -> {
                VanillaLoot.jungleTempleLoot(table)
                return true
            }
            Identifier("mostructures","pillager_treasure") -> {
                VanillaLoot.outpostLoot(table)
                return true
            }
            Identifier("mostructures","pirate_ship_cargo_misc") -> {
                val poolBuilder = RegisterLoot.tierOneGemPool(3.0F, 0.333F)
                table.pool(poolBuilder)
                val poolBuilder2 = RegisterLoot.tierTwoGemPool(1.0F, 0.1F)
                table.pool(poolBuilder2)
                return true
            }
            Identifier("mostructures","pyramid_custom") -> {
                VanillaLoot.desertTempleLoot(table)
                return true
            }
            Identifier("mostructures","villager_tower") -> {
                VanillaLoot.villageHouseLoot(table)
                return true
            }
            else -> {
                return false
            }
        }
    }
}