package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.loot.*
import me.fzzyhmstrs.fzzy_core.registry.LootRegistry.registerModLoot
import net.minecraft.loot.LootPool
import net.minecraft.loot.condition.RandomChanceLootCondition
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.util.math.MathHelper

object RegisterLoot {


    //private val COAL_ORE_LOOT_TABLE_ID = Blocks.CHEST.lootTableId
    fun registerAll(){
        registerModLoot(VanillaLoot)
        registerModLoot(BattletowersLoot)
        registerModLoot(BetterStrongholdsLoot)
        registerModLoot(MoStructuresLoot)
        registerModLoot(GraveyardLoot)
        registerModLoot(TowersOfTheWildReworkedLoot)
        registerModLoot(BetterDungeonsLoot)
        registerModLoot(BetterDesertTemplesLoot)
    }

    fun tierOneGemPool(maxCount: Float, overallChance: Float): LootPool.Builder {
        val actualChance = MathHelper.clamp(overallChance/maxCount,0.01F,1.0F)
        return LootPool.builder()
            .rolls(UniformLootNumberProvider.create(1.0F,maxCount))
            .conditionally(RandomChanceLootCondition.builder(actualChance))
            .with(ItemEntry.builder(RegisterItem.CITRINE).weight(10))
            .with(ItemEntry.builder(RegisterItem.DANBURITE).weight(10))
            .with(ItemEntry.builder(RegisterItem.SMOKY_QUARTZ).weight(10))
            .with(ItemEntry.builder(RegisterItem.IMBUED_QUARTZ).weight(10))
            .with(ItemEntry.builder(RegisterItem.IMBUED_LAPIS).weight(10))
            .with(ItemEntry.builder(RegisterItem.GEM_DUST).weight(1))
    }
    fun tierTwoGemPool(maxCount: Float, overallChance: Float): LootPool.Builder {
        val actualChance = MathHelper.clamp(overallChance/maxCount,0.01F,1.0F)
        return LootPool.builder()
            .rolls(UniformLootNumberProvider.create(1.0F,maxCount))
            .conditionally(RandomChanceLootCondition.builder(actualChance))
            .with(ItemEntry.builder(RegisterItem.MOONSTONE).weight(10))
            .with(ItemEntry.builder(RegisterItem.OPAL).weight(10))
            .with(ItemEntry.builder(RegisterItem.GARNET).weight(10))
            .with(ItemEntry.builder(RegisterItem.PYRITE).weight(10))
            .with(ItemEntry.builder(RegisterItem.GEM_DUST).weight(15))
            .with(ItemEntry.builder(RegisterItem.GLOWING_FRAGMENT).weight(15))
    }
}
