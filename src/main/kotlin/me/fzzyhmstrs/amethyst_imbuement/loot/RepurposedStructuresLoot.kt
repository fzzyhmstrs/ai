package me.fzzyhmstrs.amethyst_imbuement.loot

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterLoot
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder
import net.minecraft.client.MinecraftClient
import net.minecraft.item.Items
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.util.Identifier
import java.util.*

object RepurposedStructuresLoot: AbstractModLoot {

    override fun lootBuilder(id: Identifier, table: FabricLootSupplierBuilder): Boolean {
        if (id.namespace != "repurposed_structures") return false
        if (repurposedDungeonChecker(id)) {
            VanillaLoot.dungeonLoot(table)
            return true
        } else if (repurposedVillagesChecker(id)) {
            VanillaLoot.villageHouseLoot(table)
            return true
        }else if (repurposedSVillagesWeaponsmithChecker(id)) {
            VanillaLoot.villageWeaponsmithLoot(table)
            return true
        } else if (repurposedCitiesChecker(id)) {
            VanillaLoot.desertTempleLoot(table)
            return true
        } else if (repurposedFortressesChecker(id)) {
            val poolBuilder = FabricLootPoolBuilder.builder()
                .rolls(UniformLootNumberProvider.create(1.0F,2.0F))
                .with(ItemEntry.builder(RegisterItem.PYRITE).weight(5))
                .with(ItemEntry.builder(RegisterItem.HEARTSTONE).weight(10))
                .with(ItemEntry.builder(RegisterItem.BOOK_OF_LORE).weight(10))
                .with(ItemEntry.builder(RegisterItem.IRIDESCENT_ORB).weight(10))
                .with(ItemEntry.builder(Items.AIR).weight(70))
            table.pool(poolBuilder)
            return true
        } else if (repurposedMineshaftsChecker(id)) {
            VanillaLoot.mineshaftLoot(table)
            return true
        } else if (repurposedOutpostsChecker(id)) {
            VanillaLoot.outpostLoot(table)
            return true
        } else if (repurposedPyramidsChecker(id)) {
            VanillaLoot.desertTempleLoot(table)
            return true
        } else if (repurposedRuinedPortalsChecker(id)) {
            val rnd = Random(124).nextBoolean()
            if (rnd) {
                VanillaLoot.endCityLoot(table)
            } else {
                VanillaLoot.ruinedPortalLoot(table)
            }
            return true
        } else if (repurposedRuinsChecker(id)) {
            VanillaLoot.ruinedPortalLoot(table)
            return true
        } else if (repurposedShipwrecksMapChecker(id)) {
            VanillaLoot.shipwreckMapLoot(table)
            return true
        } else if (repurposedShipwrecksSupplyChecker(id)) {
            VanillaLoot.shipwreckSupplyLoot(table)
            return true
        } else if (repurposedShipwrecksTreasureChecker(id)) {
            VanillaLoot.shipwreckTreasureLoot(table)
            return true
        } else if (repurposedTemplesChecker(id)) {
            VanillaLoot.netherBridgeLoot(table)
            return true
        } else if (repurposedStrongholdsStorageChecker(id)) {
            VanillaLoot.strongholdGenericLoot(table,2)
            return true
        } else if (repurposedStrongholdsHallwayChecker(id)) {
            VanillaLoot.strongholdGenericLoot(table,6)
            return true
        } else if (repurposedStrongholdsLibraryChecker(id)) {
            VanillaLoot.strongholdLibraryLoot(table)
            return true
        } else if (repurposedMansionsChecker(id)) {
            VanillaLoot.mansionLoot(table)
            return true
        } else if (repurposedBastionsGenericChecker(id)) {
            VanillaLoot.bastionGenericLoot(table)
            return true
        } else if (repurposedBastionsTreasureChecker(id)) {
            VanillaLoot.bastionTreasureLoot(table)
            return true
        }
        return false
    }

    private fun repurposedDungeonChecker(id: Identifier): Boolean{
        val pathList: List<String> = listOf(
            "chests/dungeons/badlands",
            "chests/dungeons/dark_forest",
            "chests/dungeons/deep",
            "chests/dungeons/desert",
            "chests/dungeons/icy",
            "chests/dungeons/jungle",
            "chests/dungeons/mushroom",
            "chests/dungeons/nether",
            "chests/dungeons/ocean",
            "chests/dungeons/snow",
            "chests/dungeons/swamp")

        return pathList.contains(id.path)
    }

    private fun repurposedCitiesChecker(id: Identifier): Boolean{
        val pathList: List<String> = listOf(
            "chests/cities/nether",
            "chests/cities/overworld")
        return pathList.contains(id.path)
    }

    private fun repurposedBastionsGenericChecker(id: Identifier): Boolean{
        val pathList: List<String> = listOf(
            "chests/bastions/underground/bridge",
            "chests/bastions/underground/other")
        return pathList.contains(id.path)
    }

    private fun repurposedBastionsTreasureChecker(id: Identifier): Boolean{
        val pathList: List<String> = listOf(
            "chests/bastions/underground/treasure")
        return pathList.contains(id.path)
    }

    private fun repurposedRuinedPortalsChecker(id: Identifier): Boolean{
        val pathList: List<String> = listOf(
            "chests/ruined_portals/end/large_portal",
            "chests/ruined_portals/end/small_portal")
        return pathList.contains(id.path)
    }

    private fun repurposedFortressesChecker(id: Identifier): Boolean{
        val pathList: List<String> = listOf(
            "chests/fortresses/jungle_center",
            "chests/fortresses/jungle_hallway",
            "chests/fortresses/jungle_shrine")
        return pathList.contains(id.path)
    }

    private fun repurposedMineshaftsChecker(id: Identifier): Boolean{
        val pathList: List<String> = listOf(
            "chests/mineshafts/birch",
            "chests/mineshafts/crimson",
            "chests/mineshafts/dark_forest",
            "chests/mineshafts/desert",
            "chests/mineshafts/end",
            "chests/mineshafts/icy",
            "chests/mineshafts/jungle",
            "chests/mineshafts/nether",
            "chests/mineshafts/ocean",
            "chests/mineshafts/savanna",
            "chests/mineshafts/stone",
            "chests/mineshafts/swamp",
            "chests/mineshafts/taiga",
            "chests/mineshafts/warped")
        return pathList.contains(id.path)
    }

    private fun repurposedOutpostsChecker(id: Identifier): Boolean{
        val pathList: List<String> = listOf(
            "chests/outposts/badlands",
            "chests/outposts/birch",
            "chests/outposts/crimson",
            "chests/outposts/desert",
            "chests/outposts/giant_tree_taiga",
            "chests/outposts/icy",
            "chests/outposts/jungle",
            "chests/outposts/nether_brick",
            "chests/outposts/oak",
            "chests/outposts/snowy",
            "chests/outposts/taiga",
            "chests/outposts/warped",
            "shulker_boxes/outposts/end")
        return pathList.contains(id.path)
    }

    private fun repurposedPyramidsChecker(id: Identifier): Boolean{
        val pathList: List<String> = listOf(
            "chests/pyramids/dark_forest",
            "chests/pyramids/end",
            "chests/pyramids/flower_forest",
            "chests/pyramids/giant_tree_taiga",
            "chests/pyramids/icy",
            "chests/pyramids/jungle",
            "chests/pyramids/mushroom",
            "chests/pyramids/snowy",
            "trapped_chests/pyramids/badlands",
            "trapped_chests/pyramids/end",
            "trapped_chests/pyramids/nether",
            "trapped_chests/pyramids/ocean")
        return pathList.contains(id.path)
    }

    private fun repurposedRuinsChecker(id: Identifier): Boolean{
        val pathList: List<String> = listOf(
            "chests/ruins/land_hot/large",
            "chests/ruins/land_hot/small",
            "chests/ruins/land_warm/large",
            "chests/ruins/land_warm/small",
            "chests/ruins/land_cold/large",
            "chests/ruins/land_cold/small",
            "chests/ruins/land_icy/large",
            "chests/ruins/land_icy/small",
            "chests/ruins/nether")
        return pathList.contains(id.path)
    }

    private fun repurposedShipwrecksTreasureChecker(id: Identifier): Boolean{
        val pathList: List<String> = listOf(
            "chests/shipwrecks/crimson/treasure",
            "chests/shipwrecks/end/treasure",
            "chests/shipwrecks/warped/treasure",
            "chests/shipwrecks/nether_bricks/treasure")
        return pathList.contains(id.path)
    }

    private fun repurposedShipwrecksSupplyChecker(id: Identifier): Boolean{
        val pathList: List<String> = listOf(
            "chests/shipwrecks/crimson/supply",
            "chests/shipwrecks/end/supply",
            "chests/shipwrecks/warped/supply")
        return pathList.contains(id.path)
    }

    private fun repurposedShipwrecksMapChecker(id: Identifier): Boolean{
        val pathList: List<String> = listOf(
            "chests/shipwrecks/crimson/map",
            "chests/shipwrecks/end/map",
            "chests/shipwrecks/warped/map")
        return pathList.contains(id.path)
    }

    private fun repurposedTemplesChecker(id: Identifier): Boolean{
        val pathList: List<String> = listOf(
            "chests/temples/basalt",
            "chests/temples/crimson",
            "chests/temples/soul",
            "chests/temples/warped",
            "chests/temples/wasteland")
        return pathList.contains(id.path)
    }

    private fun repurposedMansionsChecker(id: Identifier): Boolean{
        val pathList: List<String> = listOf(
            "chests/mansions/birch",
            "chests/mansions/desert",
            "chests/mansions/jungle",
            "chests/mansions/oak",
            "chests/mansions/savanna",
            "chests/mansions/snowy",
            "chests/mansions/taiga")
        return pathList.contains(id.path)
    }

    private fun repurposedStrongholdsStorageChecker(id: Identifier): Boolean{
        val pathList: List<String> = listOf(
            "shulker_boxes/strongholds/end_storage_room",
            "chests/strongholds/nether_storage_room")
        return pathList.contains(id.path)
    }

    private fun repurposedStrongholdsHallwayChecker(id: Identifier): Boolean{
        val pathList: List<String> = listOf(
            "shulker_boxes/strongholds/end_hallway",
            "chests/strongholds/nether_hallway")
        return pathList.contains(id.path)
    }

    private fun repurposedStrongholdsLibraryChecker(id: Identifier): Boolean{
        val pathList: List<String> = listOf(
            "shulker_boxes/strongholds/end_library",
            "chests/strongholds/nether_library")
        return pathList.contains(id.path)
    }

    private fun repurposedVillagesChecker(id: Identifier): Boolean{
        val pathList: List<String> = listOf(
            "chests/villages/badlands_house",
            "chests/villages/birch_house",
            "chests/villages/dark_forest_house",
            "chests/villages/giant_taiga_house",
            "chests/villages/jungle_house",
            "chests/villages/mountains_house",
            "chests/villages/mushroom_house",
            "chests/villages/oak_house",
            "chests/villages/swamp_house",
            "chests/villages/crimson_house",
            "chests/villages/warped_house")
        return pathList.contains(id.path)
    }

    private fun repurposedSVillagesWeaponsmithChecker(id: Identifier): Boolean{
        val pathList: List<String> = listOf(
            "chests/villages/crimson_weaponsmith",
            "chests/villages/warped_weaponsmith")
        return pathList.contains(id.path)
    }

}