package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.loot.*
import me.fzzyhmstrs.fzzy_core.registry.LootRegistry.registerModLoot
import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.util.math.MathHelper
import kotlin.math.max

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
    }

    fun tierOneGemPool(maxCount: Float, overallChance: Float): LootPool.Builder {
        val actualChance = MathHelper.clamp(overallChance,0.01F,1.0F*maxCount)
        val airWeight = (50 * ((1.0F + maxCount) / 2) / actualChance) - 50
        return LootPool.builder()
            .rolls(UniformLootNumberProvider.create(1.0F,maxCount))
            .with(ItemEntry.builder(RegisterItem.CITRINE).weight(10))
            .with(ItemEntry.builder(RegisterItem.DANBURITE).weight(10))
            .with(ItemEntry.builder(RegisterItem.SMOKY_QUARTZ).weight(10))
            .with(ItemEntry.builder(RegisterItem.IMBUED_QUARTZ).weight(10))
            .with(ItemEntry.builder(RegisterItem.IMBUED_LAPIS).weight(10))
            .with(ItemEntry.builder(RegisterItem.GEM_DUST).weight(1))
            .with(ItemEntry.builder(Items.AIR).weight(max(airWeight.toInt(),1)))
    }
    fun tierTwoGemPool(maxCount: Float, overallChance: Float): LootPool.Builder {
        val actualChance = MathHelper.clamp(overallChance,0.01F,1.0F*maxCount)
        val airWeight = (60 * ((1.0F + maxCount) / 2) / actualChance) - 60
        return LootPool.builder()
            .rolls(UniformLootNumberProvider.create(1.0F,maxCount))
            .with(ItemEntry.builder(RegisterItem.MOONSTONE).weight(10))
            .with(ItemEntry.builder(RegisterItem.OPAL).weight(10))
            .with(ItemEntry.builder(RegisterItem.GARNET).weight(10))
            .with(ItemEntry.builder(RegisterItem.PYRITE).weight(10))
            .with(ItemEntry.builder(RegisterItem.GEM_DUST).weight(10))
            .with(ItemEntry.builder(RegisterItem.GLOWING_FRAGMENT).weight(10))
            .with(ItemEntry.builder(Items.AIR).weight(max(airWeight.toInt(),1)))
    }
}