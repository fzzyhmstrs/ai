package me.fzzyhmstrs.amethyst_imbuement.loot

import me.fzzyhmstrs.amethyst_core.misc_util.AbstractModLoot
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterLoot
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.util.Identifier

object BattletowersLoot: AbstractModLoot {

    override fun lootBuilder(id: Identifier, table: LootTable.Builder): Boolean{
        if (id.namespace != "battletowers") return false
        when (id) {
            Identifier("battletowers","default") -> {
                val poolBuilder = RegisterLoot.tierOneGemPool(2.0F, 0.1F)
                table.pool(poolBuilder)
                return true
            }
            Identifier("battletowers","stone/blacksmith") -> {
                val poolBuilder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(1.0F,5.0F))
                    .with(ItemEntry.builder(RegisterItem.BERYL_COPPER_INGOT).weight(1))
                    .with(ItemEntry.builder(RegisterItem.STEEL_INGOT).weight(1))
                    .with(ItemEntry.builder(Items.AIR).weight(18))
                table.pool(poolBuilder)
                return true
            }
            Identifier("battletowers","stone/jungle") -> {
                val poolBuilder = RegisterLoot.tierOneGemPool(2.0F, 0.1F)
                table.pool(poolBuilder)
                val poolBuilder2 = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .with(ItemEntry.builder(RegisterItem.HEARTSTONE).weight(1))
                    .with(ItemEntry.builder(Items.AIR).weight(29))
                table.pool(poolBuilder2)
                return true
            }
            Identifier("battletowers","stone/mineshaft") -> {
                val poolBuilder = RegisterLoot.tierTwoGemPool(1.0F, 0.05F)
                table.pool(poolBuilder)
                val poolBuilder2 = RegisterLoot.tierOneGemPool(2.0F, 0.1F)
                table.pool(poolBuilder2)
                return true
            }
            Identifier("battletowers","stone/library") -> {
                val poolBuilder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(1.0F,2.0F))
                    .with(ItemEntry.builder(RegisterItem.BOOK_OF_LORE).weight(1))
                    .with(ItemEntry.builder(Items.AIR).weight(29))
                table.pool(poolBuilder)
                return true
            }
            else -> {
                return false
            }
        }
    }
}