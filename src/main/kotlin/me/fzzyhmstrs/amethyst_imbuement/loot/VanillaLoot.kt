package me.fzzyhmstrs.amethyst_imbuement.loot

import me.fzzyhmstrs.amethyst_core.item_util.AbstractModLoot
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterArmor
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterLoot
import net.minecraft.block.Blocks
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.LootTables
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.function.EnchantWithLevelsLootFunction
import net.minecraft.loot.function.SetDamageLootFunction
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.util.Identifier

@Suppress("MemberVisibilityCanBePrivate")
object VanillaLoot: AbstractModLoot() {
    override val targetNameSpace: String = "minecraft"

    override fun lootBuilder(id: Identifier, table: LootTable.Builder): Boolean{
        if (Blocks.NETHER_QUARTZ_ORE.lootTableId.equals(id)) {
            val poolBuilder = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1.0F))
                .with(ItemEntry.builder(Items.AMETHYST_SHARD).weight(2))
                .with(ItemEntry.builder(RegisterItem.CITRINE).weight(1))
                .with(ItemEntry.builder(RegisterItem.SMOKY_QUARTZ).weight(1))
                .with(ItemEntry.builder(Items.AIR).weight(116))
            table.pool(poolBuilder)
            return true
        } else if (LootTables.VILLAGE_ARMORER_CHEST.equals(id)) {
            val poolBuilder = LootPool.builder()
                .rolls(UniformLootNumberProvider.create(1.0F,2.0F))
                .with(ItemEntry.builder(RegisterItem.STEEL_INGOT).weight(40))
                .with(ItemEntry.builder(RegisterArmor.STEEL_HELMET).weight(8).apply(
                    SetDamageLootFunction.builder(
                        UniformLootNumberProvider.create(0.25f, 0.75f))))
                .with(ItemEntry.builder(RegisterArmor.STEEL_CHESTPLATE).weight(8).apply(
                    SetDamageLootFunction.builder(
                        UniformLootNumberProvider.create(0.25f, 0.75f))))
                .with(ItemEntry.builder(RegisterArmor.STEEL_LEGGINGS).weight(8).apply(
                    SetDamageLootFunction.builder(
                        UniformLootNumberProvider.create(0.25f, 0.75f))))
                .with(ItemEntry.builder(RegisterArmor.STEEL_BOOTS).weight(8).apply(
                    SetDamageLootFunction.builder(
                        UniformLootNumberProvider.create(0.25f, 0.75f))))
                .with(ItemEntry.builder(Items.AIR).weight(40))
            table.pool(poolBuilder)
            return true
        } else if (LootTables.VILLAGE_FLETCHER_CHEST.equals(id)) {
            val poolBuilder = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1.0F))
                .with(ItemEntry.builder(RegisterItem.SNIPER_BOW).weight(1).apply(
                    SetDamageLootFunction.builder(
                        UniformLootNumberProvider.create(0.25f, 0.75f))))
                .with(ItemEntry.builder(Items.AIR).weight(39))
            table.pool(poolBuilder)
            return true
        } else if (LootTables.VILLAGE_TEMPLE_CHEST.equals(id)) {
            val poolBuilder = LootPool.builder()
                .rolls(UniformLootNumberProvider.create(1.0F,3.0F))
                .with(ItemEntry.builder(RegisterItem.BOOK_OF_LORE).weight(5))
                .with(ItemEntry.builder(Items.AIR).weight(10))
            table.pool(poolBuilder)
            val poolBuilder2 = RegisterLoot.tierOneGemPool(3.0F, 0.25F)
            table.pool(poolBuilder2)
            return true
        } else if (LootTables.VILLAGE_WEAPONSMITH_CHEST.equals(id)) {
            villageWeaponsmithLoot(table)
            return true
        } else if (LootTables.VILLAGE_PLAINS_CHEST.equals(id) ||
            LootTables.VILLAGE_SAVANNA_HOUSE_CHEST.equals(id) ||
            LootTables.VILLAGE_SNOWY_HOUSE_CHEST.equals(id) ||
            LootTables.VILLAGE_TAIGA_HOUSE_CHEST.equals(id)) {
            villageHouseLoot(table)
            return true
        } else if (LootTables.SHIPWRECK_SUPPLY_CHEST.equals(id)) {
            shipwreckSupplyLoot(table)
            return true
        } else if (LootTables.SHIPWRECK_MAP_CHEST.equals(id)) {
            shipwreckMapLoot(table)
            return true
        } else if (LootTables.SHIPWRECK_TREASURE_CHEST.equals(id)) {
            shipwreckTreasureLoot(table)
            return true
        } else if (LootTables.DESERT_PYRAMID_CHEST.equals(id)) {
            desertTempleLoot(table)
            return true
        } else if (LootTables.UNDERWATER_RUIN_SMALL_CHEST.equals(id)) {
            val poolBuilder = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1.0F))
                .with(ItemEntry.builder(RegisterItem.COPPER_RING).weight(1))
                .with(ItemEntry.builder(RegisterItem.COPPER_AMULET).weight(1))
                .with(ItemEntry.builder(RegisterItem.COPPER_HEADBAND).weight(1))
                .with(ItemEntry.builder(Items.AIR).weight(29))
            table.pool(poolBuilder)
            return true
        } else if (LootTables.UNDERWATER_RUIN_BIG_CHEST.equals(id)) {
            val poolBuilder = LootPool.builder()
                .rolls(UniformLootNumberProvider.create(1.0F, 2.0F))
                .with(ItemEntry.builder(RegisterItem.COPPER_RING).weight(1))
                .with(ItemEntry.builder(RegisterItem.COPPER_AMULET).weight(1))
                .with(ItemEntry.builder(RegisterItem.COPPER_HEADBAND).weight(1))
                .with(ItemEntry.builder(Items.AIR).weight(16))
            table.pool(poolBuilder)
            val poolBuilder2 = RegisterLoot.tierOneGemPool(2.0F, 0.5F)
            table.pool(poolBuilder2)
            return true
        } else if (LootTables.RUINED_PORTAL_CHEST.equals(id)) {
            ruinedPortalLoot(table)
            return true
        } else if (LootTables.ABANDONED_MINESHAFT_CHEST.equals(id)) {
            mineshaftLoot(table)
            return true
        } else if (LootTables.BASTION_TREASURE_CHEST.equals(id)) {
            bastionTreasureLoot(table)
            return true
        } else if (LootTables.BASTION_BRIDGE_CHEST.equals(id)) {
            bastionGenericLoot(table)
            return true
        } else if (LootTables.BASTION_OTHER_CHEST.equals(id)) {
            bastionGenericLoot(table)
            return true
        } else if (LootTables.NETHER_BRIDGE_CHEST.equals(id)) {
            netherBridgeLoot(table)
            return true
        } else if (LootTables.PIGLIN_BARTERING_GAMEPLAY.equals(id)) {
            val poolBuilder = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1.0F))
                .with(ItemEntry.builder(RegisterItem.BRILLIANT_DIAMOND).weight(1))
                .with(ItemEntry.builder(RegisterItem.GOLDEN_HEART).weight(1))
                .with(ItemEntry.builder(RegisterItem.GEM_OF_PROMISE).weight(1))
                .with(ItemEntry.builder(Items.AIR).weight(198))
            table.pool(poolBuilder)
            return true
        } else if (LootTables.SIMPLE_DUNGEON_CHEST.equals(id)) {
            dungeonLoot(table)
            return true
        } else if (LootTables.STRONGHOLD_CORRIDOR_CHEST.equals(id)) {
            strongholdGenericLoot(table,4)
            return true
        } else if (LootTables.STRONGHOLD_CROSSING_CHEST.equals(id)) {
            strongholdGenericLoot(table,1)
            return true
        } else if (LootTables.STRONGHOLD_LIBRARY_CHEST.equals(id)) {
            strongholdLibraryLoot(table)
            return true
        } else if (LootTables.END_CITY_TREASURE_CHEST.equals(id)) {
            endCityLoot(table)
            return true
        } else if (LootTables.JUNGLE_TEMPLE_CHEST.equals(id)) {
            jungleTempleLoot(table)
            return true
        } else if (LootTables.PILLAGER_OUTPOST_CHEST.equals(id)) {
            outpostLoot(table)
            return true
        } else if (LootTables.WOODLAND_MANSION_CHEST.equals(id)) {
            mansionLoot(table)
            return true
        } else if (LootTables.BURIED_TREASURE_CHEST.equals(id)) {
            val poolBuilder = LootPool.builder()
                .rolls(UniformLootNumberProvider.create(1.0F, 2.0F))
                .with(ItemEntry.builder(RegisterItem.GOLDEN_HEART).weight(1))
                .with(ItemEntry.builder(RegisterItem.BRILLIANT_DIAMOND).weight(2))
                .with(
                    ItemEntry.builder(RegisterItem.GLISTERING_TRIDENT).weight(1).apply(
                        EnchantWithLevelsLootFunction.builder(
                            ConstantLootNumberProvider.create(30.0f)).allowTreasureEnchantments()).apply(
                        SetDamageLootFunction.builder(
                            UniformLootNumberProvider.create(0.05f, 0.35f))))
                .with(ItemEntry.builder(Items.AIR).weight(36))
            table.pool(poolBuilder)
            return true
        } else if (LootTables.FISHING_TREASURE_GAMEPLAY.equals(id)) {
            val poolBuilder = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1.0F))
                .with(ItemEntry.builder(RegisterItem.BOOK_OF_LORE).weight(1))
                .with(ItemEntry.builder(RegisterItem.HEARTSTONE).weight(1))
                .with(ItemEntry.builder(RegisterItem.BRILLIANT_DIAMOND).weight(1))
                .with(ItemEntry.builder(Items.AIR).weight(47))
            table.pool(poolBuilder)
            return true
        }
        return false
    }



    fun bastionGenericLoot(table: LootTable.Builder){
        val poolBuilder = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(ItemEntry.builder(RegisterItem.IMBUED_QUARTZ).weight(10))
            .with(ItemEntry.builder(RegisterItem.PYRITE).weight(3))
            .with(ItemEntry.builder(Items.AIR).weight(32))
        table.pool(poolBuilder)
    }

    fun bastionTreasureLoot(table: LootTable.Builder){
        val poolBuilder = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(ItemEntry.builder(RegisterItem.GOLDEN_HEART).weight(2))
            .with(ItemEntry.builder(RegisterItem.BRILLIANT_DIAMOND).weight(6))
            .with(ItemEntry.builder(RegisterItem.GEM_OF_PROMISE).weight(6))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_LORE).weight(6))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_MYTHOS).weight(2))
            .with(ItemEntry.builder(Items.AIR).weight(16))
        table.pool(poolBuilder)
        val poolBuilder2 = RegisterLoot.tierTwoGemPool(3.0F, 0.333F)
        table.pool(poolBuilder2)
    }

    fun jungleTempleLoot(table: LootTable.Builder){
        val poolBuilder = LootPool.builder()
            .rolls(UniformLootNumberProvider.create(1.0F,2.0F))
            .with(ItemEntry.builder(RegisterItem.PYRITE).weight(1))
            .with(ItemEntry.builder(RegisterItem.HEARTSTONE).weight(10))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_LORE).weight(10))
            .with(ItemEntry.builder(RegisterItem.IRIDESCENT_ORB).weight(10))
            .with(ItemEntry.builder(Items.AIR).weight(19))
        table.pool(poolBuilder)
    }

    fun mansionLoot(table: LootTable.Builder){
        val poolBuilder = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(
                ItemEntry.builder(RegisterItem.SNIPER_BOW).weight(3).apply(
                    EnchantWithLevelsLootFunction.builder(
                        ConstantLootNumberProvider.create(30.0f)).allowTreasureEnchantments()).apply(
                    SetDamageLootFunction.builder(
                        UniformLootNumberProvider.create(0.05f, 0.35f))))
            .with(ItemEntry.builder(RegisterItem.SNIPER_BOW).weight(5).apply(
                SetDamageLootFunction.builder(
                    UniformLootNumberProvider.create(0.05f, 0.35f))))
            .with(ItemEntry.builder(RegisterItem.TOTEM_OF_AMETHYST).weight(1))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_MYTHOS).weight(1))
            .with(ItemEntry.builder(Items.AIR).weight(15))
        table.pool(poolBuilder)
    }

    fun outpostLoot(table: LootTable.Builder){
        val poolBuilder = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(
                ItemEntry.builder(RegisterItem.SNIPER_BOW).weight(1).apply(
                    EnchantWithLevelsLootFunction.builder(
                        ConstantLootNumberProvider.create(30.0f)).allowTreasureEnchantments()).apply(
                    SetDamageLootFunction.builder(
                        UniformLootNumberProvider.create(0.05f, 0.35f))))
            .with(ItemEntry.builder(RegisterItem.SNIPER_BOW).weight(1).apply(
                SetDamageLootFunction.builder(
                    UniformLootNumberProvider.create(0.05f, 0.35f))))
            .with(ItemEntry.builder(Items.AIR).weight(18))
        table.pool(poolBuilder)
        val poolBuilder2 = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_LORE).weight(1))
            .with(ItemEntry.builder(Items.AIR).weight(3))
        table.pool(poolBuilder2)
    }

    fun mineshaftLoot(table: LootTable.Builder){
        val poolBuilder = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(ItemEntry.builder(RegisterItem.IRIDESCENT_ORB).weight(7))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_LORE).weight(3))
            .with(ItemEntry.builder(Items.AIR).weight(11))
        table.pool(poolBuilder)
        val poolBuilder2 = LootPool.builder()
            .rolls(UniformLootNumberProvider.create(1.0F,2.0F))
            .with(ItemEntry.builder(RegisterItem.STEEL_INGOT).weight(5))
            .with(ItemEntry.builder(RegisterItem.BERYL_COPPER_INGOT).weight(5))
            .with(ItemEntry.builder(Items.AIR).weight(20))
        table.pool(poolBuilder2)
        val poolBuilder3 = RegisterLoot.tierTwoGemPool(2.0F, 0.20F)
        table.pool(poolBuilder3)
    }

    fun ruinedPortalLoot(table: LootTable.Builder){
        val poolBuilder = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_LORE).weight(5))
            .with(ItemEntry.builder(RegisterItem.IRIDESCENT_ORB).weight(5))
            .with(ItemEntry.builder(Items.AIR).weight(10))
        table.pool(poolBuilder)
        val poolBuilder2 = LootPool.builder()
            .rolls(UniformLootNumberProvider.create(1.0F,5.0F))
            .with(ItemEntry.builder(RegisterItem.STEEL_INGOT).weight(5))
            .with(ItemEntry.builder(Items.AIR).weight(15))
        table.pool(poolBuilder2)
    }

    fun shipwreckMapLoot(table: LootTable.Builder){
        val poolBuilder = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_LORE).weight(1))
            .with(ItemEntry.builder(Items.AIR).weight(3))
        table.pool(poolBuilder)
    }

    fun shipwreckTreasureLoot(table: LootTable.Builder){
        val poolBuilder = LootPool.builder()
            .rolls(UniformLootNumberProvider.create(1.0F,2.0F))
            .with(ItemEntry.builder(RegisterItem.BRILLIANT_DIAMOND).weight(5))
            .with(ItemEntry.builder(RegisterItem.HEARTSTONE).weight(5))
            .with(ItemEntry.builder(RegisterItem.IRIDESCENT_ORB).weight(15))
            .with(ItemEntry.builder(RegisterItem.GEM_DUST).weight(10))
            .with(ItemEntry.builder(Items.AIR).weight(50))
        table.pool(poolBuilder)
        val poolBuilder2 = RegisterLoot.tierOneGemPool(4.0F, 0.75F)
        table.pool(poolBuilder2)
        val poolBuilder3 = RegisterLoot.tierTwoGemPool(1.0F, 0.15F)
        table.pool(poolBuilder3)
    }

    fun shipwreckSupplyLoot(table: LootTable.Builder){
        val poolBuilder = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(ItemEntry.builder(RegisterItem.GARNET_HOE).weight(5).apply(
                SetDamageLootFunction.builder(
                    UniformLootNumberProvider.create(0.25f, 0.75f))))
            .with(ItemEntry.builder(RegisterItem.GARNET_SHOVEL).weight(5).apply(
                SetDamageLootFunction.builder(
                    UniformLootNumberProvider.create(0.25f, 0.75f))))
            .with(ItemEntry.builder(Items.AIR).weight(40))
        table.pool(poolBuilder)
        val poolBuilder2 = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_LORE).weight(1))
            .with(ItemEntry.builder(Items.AIR).weight(9))
        table.pool(poolBuilder2)
    }

    fun strongholdGenericLoot(table: LootTable.Builder, brilliantDiamondAirWeight: Int){
        val poolBuilder = LootPool.builder()
            .rolls(UniformLootNumberProvider.create(1.0F,3.0F))
            .with(ItemEntry.builder(RegisterItem.BERYL_COPPER_INGOT).weight(3))
            .with(ItemEntry.builder(RegisterItem.STEEL_INGOT).weight(3))
            .with(ItemEntry.builder(Items.AIR).weight(6))
        table.pool(poolBuilder)
        val poolBuilder2 = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(ItemEntry.builder(RegisterItem.BRILLIANT_DIAMOND).weight(1))
            .with(ItemEntry.builder(Items.AIR).weight(brilliantDiamondAirWeight))
        table.pool(poolBuilder2)
        val poolBuilder3 = RegisterLoot.tierTwoGemPool(3.0F, 0.4F)
        table.pool(poolBuilder3)
    }

    fun strongholdLibraryLoot(table: LootTable.Builder){
        val poolBuilder = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(ItemEntry.builder(RegisterItem.IRIDESCENT_ORB).weight(3))
            .with(ItemEntry.builder(RegisterItem.GEM_OF_PROMISE).weight(1))
            .with(ItemEntry.builder(Items.AIR).weight(2))
        table.pool(poolBuilder)
        val poolBuilder2 = LootPool.builder()
            .rolls(UniformLootNumberProvider.create(1.0F,2.0F))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_LORE).weight(10))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_MYTHOS).weight(5))
            .with(ItemEntry.builder(Items.AIR).weight(5))
        table.pool(poolBuilder2)
    }

    fun dungeonLoot(table: LootTable.Builder){
        val poolBuilder = RegisterLoot.tierTwoGemPool(3.0F, 0.75F)
        table.pool(poolBuilder)
        val poolBuilder2 = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(ItemEntry.builder(RegisterItem.IRIDESCENT_ORB).weight(4))
            .with(ItemEntry.builder(RegisterItem.GEM_OF_PROMISE).weight(1))
            .with(ItemEntry.builder(Items.AIR).weight(2))
        table.pool(poolBuilder2)
        val poolBuilder3 = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_LORE).weight(9))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_MYTHOS).weight(1))
            .with(ItemEntry.builder(Items.AIR).weight(10))
        table.pool(poolBuilder3)
    }

    fun villageHouseLoot(table: LootTable.Builder){
        val poolBuilder = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_LORE).weight(2))
            .with(ItemEntry.builder(RegisterItem.XP_BUSH_SEED).weight(6))
            .with(ItemEntry.builder(Items.AIR).weight(62))
        table.pool(poolBuilder)
    }

    fun villageWeaponsmithLoot(table: LootTable.Builder){
        val poolBuilder = LootPool.builder()
            .rolls(UniformLootNumberProvider.create(1.0F,3.0F))
            .with(ItemEntry.builder(RegisterItem.BERYL_COPPER_INGOT).weight(15))
            .with(ItemEntry.builder(RegisterItem.GEM_OF_PROMISE).weight(3))
            .with(ItemEntry.builder(Items.AIR).weight(42))
        table.pool(poolBuilder)
    }

    fun desertTempleLoot(table: LootTable.Builder){
        val poolBuilder = LootPool.builder()
            .rolls(UniformLootNumberProvider.create(1.0F,3.0F))
            .with(ItemEntry.builder(RegisterItem.PYRITE).weight(18))
            .with(ItemEntry.builder(RegisterItem.HEARTSTONE).weight(6))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_LORE).weight(3))
            .with(ItemEntry.builder(Items.AIR).weight(62))
        table.pool(poolBuilder)
        val poolBuilder2 = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(ItemEntry.builder(RegisterArmor.STEEL_HELMET).weight(4).apply(
                SetDamageLootFunction.builder(
                    UniformLootNumberProvider.create(0.25f, 0.75f))))
            .with(ItemEntry.builder(RegisterArmor.STEEL_CHESTPLATE).weight(4).apply(
                SetDamageLootFunction.builder(
                    UniformLootNumberProvider.create(0.25f, 0.75f))))
            .with(ItemEntry.builder(RegisterArmor.STEEL_LEGGINGS).weight(4).apply(
                SetDamageLootFunction.builder(
                    UniformLootNumberProvider.create(0.25f, 0.75f))))
            .with(ItemEntry.builder(RegisterArmor.STEEL_BOOTS).weight(4).apply(
                SetDamageLootFunction.builder(
                    UniformLootNumberProvider.create(0.25f, 0.75f))))
            .with(
                ItemEntry.builder(RegisterArmor.STEEL_HELMET).weight(1).apply(
                    EnchantWithLevelsLootFunction.builder(
                        UniformLootNumberProvider.create(15.0f,39.0f)).allowTreasureEnchantments()).apply(
                    SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.25f, 0.95f))))
            .with(
                ItemEntry.builder(RegisterArmor.STEEL_CHESTPLATE).weight(1).apply(
                    EnchantWithLevelsLootFunction.builder(
                        UniformLootNumberProvider.create(15.0f,39.0f)).allowTreasureEnchantments()))
            .with(
                ItemEntry.builder(RegisterArmor.STEEL_LEGGINGS).weight(1).apply(
                    EnchantWithLevelsLootFunction.builder(
                        UniformLootNumberProvider.create(15.0f,39.0f)).allowTreasureEnchantments()).apply(
                    SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.25f, 0.95f))))
            .with(
                ItemEntry.builder(RegisterArmor.STEEL_BOOTS).weight(1).apply(
                    EnchantWithLevelsLootFunction.builder(
                        UniformLootNumberProvider.create(15.0f,39.0f)).allowTreasureEnchantments()).apply(
                    SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.25f, 0.95f))))
            .with(ItemEntry.builder(Items.AIR).weight(40))
        table.pool(poolBuilder2)
        val poolBuilder3 = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(ItemEntry.builder(RegisterItem.IRIDESCENT_ORB).weight(3))
            .with(ItemEntry.builder(RegisterItem.GEM_OF_PROMISE).weight(1))
            .with(ItemEntry.builder(Items.AIR).weight(18))
        table.pool(poolBuilder3)
    }

    fun endCityLoot(table: LootTable.Builder){
        val poolBuilder = LootPool.builder()
            .rolls(UniformLootNumberProvider.create(1.0F,2.0F))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_LORE).weight(1))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_MYTHOS).weight(9))
            .with(ItemEntry.builder(Items.AIR).weight(20))
        table.pool(poolBuilder)
        val poolBuilder2 = LootPool.builder()
            .rolls(UniformLootNumberProvider.create(1.0F,2.0F))
            .with(ItemEntry.builder(RegisterItem.BRILLIANT_DIAMOND).weight(2))
            .with(ItemEntry.builder(RegisterItem.AMETRINE).weight(1))
            .with(ItemEntry.builder(RegisterItem.GEM_OF_PROMISE).weight(1))
            .with(ItemEntry.builder(Items.AIR).weight(16))
        table.pool(poolBuilder2)
        val poolBuilder3 = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(ItemEntry.builder(RegisterItem.LUSTROUS_SPHERE).weight(1))
            .with(ItemEntry.builder(Items.AIR).weight(2))
        table.pool(poolBuilder3)
        val poolBuilder4 = LootPool.builder()
            .rolls(UniformLootNumberProvider.create(1.0F,2.0F))
            .with(
                ItemEntry.builder(RegisterArmor.STEEL_HELMET).weight(2).apply(
                    EnchantWithLevelsLootFunction.builder(
                        UniformLootNumberProvider.create(20.0f,49.0f)).allowTreasureEnchantments()))
            .with(
                ItemEntry.builder(RegisterArmor.STEEL_CHESTPLATE).weight(2).apply(
                    EnchantWithLevelsLootFunction.builder(
                        UniformLootNumberProvider.create(20.0f,49.0f)).allowTreasureEnchantments()))
            .with(
                ItemEntry.builder(RegisterArmor.STEEL_LEGGINGS).weight(2).apply(
                    EnchantWithLevelsLootFunction.builder(
                        UniformLootNumberProvider.create(20.0f,49.0f)).allowTreasureEnchantments()))
            .with(
                ItemEntry.builder(RegisterArmor.STEEL_BOOTS).weight(2).apply(
                    EnchantWithLevelsLootFunction.builder(
                        UniformLootNumberProvider.create(20.0f,49.0f)).allowTreasureEnchantments()))
            .with(
                ItemEntry.builder(RegisterItem.GARNET_SWORD).weight(2).apply(
                    EnchantWithLevelsLootFunction.builder(
                        UniformLootNumberProvider.create(20.0f,49.0f)).allowTreasureEnchantments()))
            .with(
                ItemEntry.builder(RegisterItem.GARNET_AXE).weight(2).apply(
                    EnchantWithLevelsLootFunction.builder(
                        UniformLootNumberProvider.create(20.0f,49.0f)).allowTreasureEnchantments()))
            .with(
                ItemEntry.builder(RegisterItem.GARNET_HOE).weight(2).apply(
                    EnchantWithLevelsLootFunction.builder(
                        UniformLootNumberProvider.create(20.0f,49.0f)).allowTreasureEnchantments()))
            .with(
                ItemEntry.builder(RegisterItem.GARNET_SHOVEL).weight(2).apply(
                    EnchantWithLevelsLootFunction.builder(
                        UniformLootNumberProvider.create(20.0f,49.0f)).allowTreasureEnchantments()))
            .with(
                ItemEntry.builder(RegisterItem.GARNET_PICKAXE).weight(2).apply(
                    EnchantWithLevelsLootFunction.builder(
                        UniformLootNumberProvider.create(20.0f,49.0f)).allowTreasureEnchantments()))
            .with(ItemEntry.builder(RegisterItem.IMBUED_RING).weight(1))
            .with(ItemEntry.builder(RegisterItem.IMBUED_HEADBAND).weight(1))
            .with(ItemEntry.builder(RegisterItem.IMBUED_AMULET).weight(1))
            .with(ItemEntry.builder(Items.AIR).weight(32))
        table.pool(poolBuilder4)
    }

    fun netherBridgeLoot(table: LootTable.Builder){
        val poolBuilder = LootPool.builder()
            .rolls(UniformLootNumberProvider.create(1.0F,2.0F))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_LORE).weight(5))
            .with(ItemEntry.builder(RegisterItem.BOOK_OF_MYTHOS).weight(1))
            .with(ItemEntry.builder(Items.AIR).weight(10))
        table.pool(poolBuilder)
        val poolBuilder2 = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(
                ItemEntry.builder(RegisterArmor.STEEL_HELMET).weight(2).apply(
                    EnchantWithLevelsLootFunction.builder(
                        UniformLootNumberProvider.create(15.0f,39.0f)).allowTreasureEnchantments()).apply(
                    SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.25f, 0.95f))))
            .with(
                ItemEntry.builder(RegisterArmor.STEEL_CHESTPLATE).weight(2).apply(
                    EnchantWithLevelsLootFunction.builder(
                        UniformLootNumberProvider.create(15.0f,39.0f)).allowTreasureEnchantments()).apply(
                    SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.25f, 0.95f))))
            .with(
                ItemEntry.builder(RegisterArmor.STEEL_LEGGINGS).weight(2).apply(
                    EnchantWithLevelsLootFunction.builder(
                        UniformLootNumberProvider.create(15.0f,39.0f)).allowTreasureEnchantments()).apply(
                    SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.25f, 0.95f))))
            .with(
                ItemEntry.builder(RegisterArmor.STEEL_BOOTS).weight(2).apply(
                    EnchantWithLevelsLootFunction.builder(
                        UniformLootNumberProvider.create(15.0f,39.0f)).allowTreasureEnchantments()).apply(
                    SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.25f, 0.95f))))
            .with(ItemEntry.builder(Items.AIR).weight(24))
        table.pool(poolBuilder2)
        val poolBuilder3 = RegisterLoot.tierTwoGemPool(3.0F, 1.0F)
        table.pool(poolBuilder3)
    }

}