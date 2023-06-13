package me.fzzyhmstrs.amethyst_imbuement.loot

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterLoot
import me.fzzyhmstrs.fzzy_core.item_util.AbstractModLoot
import net.minecraft.entity.EntityType
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.LootTables
import net.minecraft.loot.condition.RandomChanceLootCondition
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.util.Identifier

object UniqueLoot: AbstractModLoot() {
    override val targetNameSpace: String = "battletowers"

    override fun lootBuilder(id: Identifier, table: LootTable.Builder): Boolean{
        if(id == EntityType.WITHER.lootTableId){
            val poolBuilder = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1.0F))
                .conditionally(RandomChanceLootCondition.builder(AiConfig.items.scepters.uniqueWitherChance.get()))
                .with(ItemEntry.builder(RegisterItem.FZZYHAMMER).weight(1))
                .with(ItemEntry.builder(RegisterItem.A_SCEPTER_SO_FOWL).weight(1))
                .with(ItemEntry.builder(RegisterItem.ACCURSED_FIGURINE).weight(1))
            table.pool(poolBuilder)
            return true
        } else if(LootTables.VILLAGE_BUTCHER_CHEST.equals(id)){
            val poolBuilder = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1.0F))
                .bonusRolls(ConstantLootNumberProvider.create(1.0F))
                .conditionally(RandomChanceLootCondition.builder(AiConfig.items.scepters.fowlChestChance.get()))
                .with(ItemEntry.builder(RegisterItem.A_SCEPTER_SO_FOWL).weight(1))
            table.pool(poolBuilder)
            return true
        } else if (id.path.contains("chests") || id.path.contains("chest")) {
            val poolBuilder = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1.0F))
                .conditionally(RandomChanceLootCondition.builder(AiConfig.items.scepters.fzzyChestChance.get()))
                .with(ItemEntry.builder(RegisterItem.FZZYHAMMER).weight(1))
            table.pool(poolBuilder)
            return true
        }

        return false
    }
}