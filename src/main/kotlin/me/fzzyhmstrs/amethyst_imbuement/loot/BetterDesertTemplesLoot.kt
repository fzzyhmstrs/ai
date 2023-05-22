@file:Suppress("SpellCheckingInspection")

package me.fzzyhmstrs.amethyst_imbuement.loot

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterLoot
import me.fzzyhmstrs.fzzy_core.item_util.AbstractModLoot
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.util.Identifier

object BetterDesertTemplesLoot: AbstractModLoot() {

    override val targetNameSpace: String = "betterdeserttemples"

    override fun lootBuilder(id: Identifier, table: LootTable.Builder): Boolean {
        when (id) {
            Identifier("betterdeserttemples", "chests/lab") -> {
                val poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .conditionally(RandomChanceLootCondition.builder(0.125f))
                    .with(ItemEntry.builder(RegisterItem.KNOWLEDGE_POWDER).weight(1))
                    .with(ItemEntry.builder(RegisterItem.GEM_DUST).weight(1).apply(
                      SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
                table.pool(poolBuilder)
                return true
            }
            Identifier("betterdeserttemples", "chests/library") -> {
                val poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .conditionally(RandomChanceLootCondition.builder(0.125f))
                    .with(ItemEntry.builder(RegisterItem.EMPTY_SPELL_SCROLL).weight(4))
                    .with(ItemEntry.builder(RegisterItem.BOOK_OF_LORE).weight(15).apply(
                      SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
                    .with(ItemEntry.builder(RegisterItem.BOOK_OF_MYTHOS).weight(1))
                table.pool(poolBuilder)
                return true
            }
            Identifier("betterdeserttemples", "chests/storage") -> {
                val poolBuilder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(1.0F,3.0F))
                    .conditionally(RandomChanceLootCondition.builder(0.375f))
                    .with(ItemEntry.builder(RegisterItem.BERYL_COPPER_INGOT).weight(3))
                    .with(ItemEntry.builder(RegisterItem.STEEL_INGOT).weight(3))
                    .with(ItemEntry.builder(RegisterItem.GLOWING_FRAGMENT).weight(1))
                    .with(ItemEntry.builder(RegisterItem.IRIDESCENT_ORB).weight(1))
                table.pool(poolBuilder)
                val poolBuilder2 = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .conditionally(RandomChanceLootCondition.builder(0.1))
                    .with(ItemEntry.builder(RegisterItem.BRILLIANT_DIAMOND).weight(2))
                    .with(ItemEntry.builder(RegisterItem.GEM_OF_PROMISE).weight(1))
                table.pool(poolBuilder2)
                return true
            }
            Identifier("betterdeserttemples", "chests/tomb") -> {
                val poolBuilder = RegisterLoot.tierOneGemPool(2.0F, 0.5F)
                table.pool(poolBuilder)
                return true
            }
            Identifier("betterdeserttemples", "chests/tomb_pharaoh") -> {
                VanillaLoot.shipwreckTreasureLoot(table)
                return true
            }
            Identifier("betterdeserttemples", "chests/wardrobe") -> {
                val poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .conditionally(RandomChanceLootCondition.builder(0.2f))
                    .with(ItemEntry.builder(RegisterItem.COPPER_RING).weight(1))
                    .with(ItemEntry.builder(RegisterItem.COPPER_AMULET).weight(1))
                    .with(ItemEntry.builder(RegisterItem.COPPER_HEADBAND).weight(1))
                table.pool(poolBuilder)
                return true
            }
            Identifier("betterdeserttemples", "skeleton_dungeon/chests/common") -> {
                VanillaLoot.mineshaftLoot(table)
                return true
            }
            else -> {
                return false
            }
        }
    }

}
