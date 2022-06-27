@file:Suppress("SpellCheckingInspection")

package me.fzzyhmstrs.amethyst_imbuement.loot

import me.fzzyhmstrs.amethyst_core.item_util.AbstractModLoot
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.util.Identifier

object BetterStrongholdsLoot: AbstractModLoot() {
    override val targetNameSpace: String = "betterstrongholds"

    override fun lootBuilder(id: Identifier, table: LootTable.Builder): Boolean {
        if (armouryChecker(id)){
            VanillaLoot.villageWeaponsmithLoot(table)
            return true
        } else if (commonChecker(id)){
            VanillaLoot.strongholdGenericLoot(table,14)
            return true
        } else if (libraryChecker(id)){
            VanillaLoot.strongholdLibraryLoot(table)
            return true
        } else if (treasureChecker(id)){
            VanillaLoot.strongholdGenericLoot(table,1)
            return true
        } else if (trapChecker(id)){
            VanillaLoot.shipwreckTreasureLoot(table)
            return true
        } else if (cryptChecker(id)){
            val poolBuilder = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1.0F))
                .with(ItemEntry.builder(RegisterItem.MOONSTONE).weight(1))
                .with(ItemEntry.builder((Items.AIR)).weight(3))
            table.pool(poolBuilder)
            return true
        }
        return false
    }

    private fun armouryChecker(id: Identifier):Boolean{
        val pathList: List<String> = listOf(
            "chests/armoury",
            "chests/end/armoury",
            "chests/nether/armoury")
        return pathList.contains(id.path)
    }

    private fun commonChecker(id: Identifier):Boolean{
        val pathList: List<String> = listOf(
            "chests/common",
            "chests/end/common",
            "chests/nether/common")
        return pathList.contains(id.path)
    }

    private fun libraryChecker(id: Identifier):Boolean{
        val pathList: List<String> = listOf(
            "chests/grand_library",
            "chests/end/grand_library",
            "chests/nether/grand_library",
            "chests/library_md",
            "chests/end/library_md",
            "chests/nether/library_md")
        return pathList.contains(id.path)
    }

    private fun treasureChecker(id: Identifier):Boolean{
        val pathList: List<String> = listOf(
            "chests/treasure",
            "chests/end/treasure",
            "chests/nether/treasure")
        return pathList.contains(id.path)
    }

    private fun trapChecker(id: Identifier):Boolean{
        val pathList: List<String> = listOf(
            "chests/trap",
            "chests/end/trap",
            "chests/nether/trap")
        return pathList.contains(id.path)
    }

    private fun cryptChecker(id: Identifier):Boolean{
        val pathList: List<String> = listOf(
            "chests/crypt",
            "chests/end/crypt",
            "chests/nether/crypt")
        return pathList.contains(id.path)
    }

}