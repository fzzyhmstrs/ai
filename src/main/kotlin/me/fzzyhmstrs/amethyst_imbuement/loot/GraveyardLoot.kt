package me.fzzyhmstrs.amethyst_imbuement.loot

import me.fzzyhmstrs.amethyst_core.item_util.AbstractModLoot
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterLoot
import net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.util.Identifier

object GraveyardLoot: AbstractModLoot() {
    override val targetNameSpace: String = "graveyard"

    override fun lootBuilder(id: Identifier, table: FabricLootTableBuilder): Boolean {
        when (id) {
            Identifier("graveyard","chests/candle_loot") -> {
                val poolBuilder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(1.0F,2.0F))
                    .with(ItemEntry.builder(RegisterBlock.WARDING_CANDLE.asItem()).weight(1))
                    .with(ItemEntry.builder(Items.AIR).weight(4))
                table.pool(poolBuilder.build())
                return true
            }
            Identifier("graveyard","chests/flower_loot") -> {
                val poolBuilder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(1.0F,3.0F))
                    .with(ItemEntry.builder(RegisterItem.XP_BUSH_SEED).weight(1))
                    .with(ItemEntry.builder(Items.AIR).weight(9))
                table.pool(poolBuilder.build())
                return true
            }
            Identifier("graveyard","large_loot") -> {
                VanillaLoot.shipwreckTreasureLoot(table)
                return true
            }
            Identifier("graveyard","medium_loot") -> {
                VanillaLoot.mineshaftLoot(table)
                return true
            }
            Identifier("graveyard","small_loot") -> {
                val poolBuilder = RegisterLoot.tierOneGemPool(3.0F, 0.25F)
                table.pool(poolBuilder.build())
                return true
            }
            Identifier("graveyard","totem_loot") -> {
                val poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .with(ItemEntry.builder(RegisterItem.TOTEM_OF_AMETHYST).weight(1))
                    .with(ItemEntry.builder(Items.AIR).weight(99))
                table.pool(poolBuilder.build())
                return true
            }
            Identifier("graveyard","vase_loot") -> {
                val poolBuilder = RegisterLoot.tierOneGemPool(3.0F, 0.2F)
                table.pool(poolBuilder.build())
                val poolBuilder2 = RegisterLoot.tierTwoGemPool(1.0F, 0.05F)
                table.pool(poolBuilder2.build())
                return true
            }
            else -> {
                return false
            }
        }
    }
}