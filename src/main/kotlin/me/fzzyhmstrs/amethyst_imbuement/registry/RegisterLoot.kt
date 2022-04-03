package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.loot.*
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback.LootTableSetter
import net.minecraft.block.Blocks
import net.minecraft.item.Items
import net.minecraft.loot.LootManager
import net.minecraft.loot.LootTables
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.function.*
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import kotlin.math.max


object RegisterLoot {

    private val modLoots: MutableList<AbstractModLoot> = mutableListOf()

    //private val COAL_ORE_LOOT_TABLE_ID = Blocks.CHEST.lootTableId
    fun registerAll(){
        registerModLoot(VanillaLoot)
        registerModLoot(BattletowersLoot)
        registerModLoot(RepurposedStructuresLoot)
        registerModLoot(BetterStrongholdsLoot)
        registerModLoot(MoStructuresLoot)
        registerModLoot(GraveyardLoot)
        registerModLoot(TowersOfTheWildReworkedLoot)
        registerModLoot(BetterDungeonsLoot)
        LootTableLoadingCallback.EVENT.register(LootTableLoadingCallback { _: ResourceManager, _: LootManager, id: Identifier, table: FabricLootSupplierBuilder, _: LootTableSetter ->
            if (modLoots.isEmpty()) return@LootTableLoadingCallback
            for (modLoot in modLoots) {
                if (modLoot.lootBuilder(id, table)) return@LootTableLoadingCallback
            }
        })
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun registerModLoot(modLoot: AbstractModLoot){
        modLoots.add(modLoot)
    }

    fun tierOneGemPool(maxCount: Float, overallChance: Float): FabricLootPoolBuilder {
        val actualChance = MathHelper.clamp(overallChance,0.01F,1.0F*maxCount)
        val airWeight = (50 * ((1.0F + maxCount) / 2) / actualChance) - 50
        return FabricLootPoolBuilder.builder()
            .rolls(UniformLootNumberProvider.create(1.0F,maxCount))
            .with(ItemEntry.builder(RegisterItem.CITRINE).weight(10))
            .with(ItemEntry.builder(RegisterItem.DANBURITE).weight(10))
            .with(ItemEntry.builder(RegisterItem.SMOKY_QUARTZ).weight(10))
            .with(ItemEntry.builder(RegisterItem.IMBUED_QUARTZ).weight(10))
            .with(ItemEntry.builder(RegisterItem.IMBUED_LAPIS).weight(10))
            .with(ItemEntry.builder(Items.AIR).weight(max(airWeight.toInt(),1)))
    }
    fun tierTwoGemPool(maxCount: Float, overallChance: Float): FabricLootPoolBuilder {
        val actualChance = MathHelper.clamp(overallChance,0.01F,1.0F*maxCount)
        val airWeight = (40 * ((1.0F + maxCount) / 2) / actualChance) - 40
        return FabricLootPoolBuilder.builder()
            .rolls(UniformLootNumberProvider.create(1.0F,maxCount))
            .with(ItemEntry.builder(RegisterItem.MOONSTONE).weight(10))
            .with(ItemEntry.builder(RegisterItem.OPAL).weight(10))
            .with(ItemEntry.builder(RegisterItem.GARNET).weight(10))
            .with(ItemEntry.builder(RegisterItem.PYRITE).weight(10))
            .with(ItemEntry.builder(Items.AIR).weight(max(airWeight.toInt(),1)))
    }
}