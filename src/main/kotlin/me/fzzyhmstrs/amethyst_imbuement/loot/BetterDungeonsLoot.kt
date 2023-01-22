@file:Suppress("SpellCheckingInspection")

package me.fzzyhmstrs.amethyst_imbuement.loot

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterLoot
import me.fzzyhmstrs.fzzy_core.item_util.AbstractModLoot
import net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.util.Identifier

object BetterDungeonsLoot: AbstractModLoot() {

    override val targetNameSpace: String = "betterdungeons"

    override fun lootBuilder(id: Identifier, table: FabricLootTableBuilder): Boolean {
        when (id) {
            Identifier("betterdungeons", "zombie_dungeon/chests/common") -> {
                VanillaLoot.mineshaftLoot(table)
                return true
            }
            Identifier("betterdungeons", "zombie_dungeon/chests/special") -> {
                VanillaLoot.mineshaftLoot(table)
                return true
            }
            Identifier("betterdungeons", "zombie_dungeon/chests/tombstone") -> {
                val poolBuilder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(1.0F, 2.0F))
                    .with(ItemEntry.builder(RegisterItem.COPPER_RING).weight(1))
                    .with(ItemEntry.builder(RegisterItem.COPPER_HEADBAND).weight(1))
                    .with(ItemEntry.builder(RegisterItem.COPPER_AMULET).weight(1))
                    .with(ItemEntry.builder(Items.AIR).weight(25))
                table.pool(poolBuilder.build())
                return true
            }
            Identifier("betterdungeons", "spider_dungeon/chests/egg_room") -> {
                VanillaLoot.mineshaftLoot(table)
                return true
            }
            Identifier("betterdungeons", "skeleton_dungeon/chests/middle") -> {
                val poolBuilder = RegisterLoot.tierOneGemPool(2.0F, 0.25F)
                table.pool(poolBuilder.build())
                return true
            }
            Identifier("betterdungeons", "skeleton_dungeon/chests/common") -> {
                VanillaLoot.mineshaftLoot(table)
                return true
            }
            else -> {
                return false
            }
        }
    }

}