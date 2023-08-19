package me.fzzyhmstrs.amethyst_imbuement.registry

import fzzyhmstrs.structurized_reborn.impl.FabricStructurePoolRegistry
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.fabricmc.fabric.api.`object`.builder.v1.trade.TradeOfferHelper
import net.fabricmc.fabric.api.`object`.builder.v1.villager.VillagerProfessionBuilder
import net.fabricmc.fabric.api.`object`.builder.v1.world.poi.PointOfInterestHelper
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundEvents
import net.minecraft.structure.processor.StructureProcessorLists
import net.minecraft.util.Identifier
import net.minecraft.util.math.random.Random
import net.minecraft.village.TradeOffer
import net.minecraft.village.TradeOffers
import net.minecraft.village.VillagerProfession

object RegisterVillager {

    private val CRYSTAL_WITCH_IDENTIFIER = Identifier(AI.MOD_ID,"crystal_witch")
    private val CRYSTAL_ALTAR_POINT_OF_INTEREST = PointOfInterestHelper.register(CRYSTAL_WITCH_IDENTIFIER,1,1,RegisterBlock.CRYSTAL_ALTAR)
    private val CRYSTAL_WITCH: VillagerProfession = VillagerProfessionBuilder.create().workstation(
        Registries.POINT_OF_INTEREST_TYPE.getKey(CRYSTAL_ALTAR_POINT_OF_INTEREST).get()).id(CRYSTAL_WITCH_IDENTIFIER).workSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE).build()

    fun registerAll(){
        Registry.register(Registries.VILLAGER_PROFESSION, CRYSTAL_WITCH_IDENTIFIER , CRYSTAL_WITCH)

        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,1) { factories -> factories.add(TradeFactory(Items.QUARTZ,16,4,Items.EMERALD,1,16,2))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,1) { factories -> factories.add(TradeFactory(Items.LAPIS_LAZULI,22,6,Items.EMERALD,1,12,2))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,1) { factories -> factories.add(TradeFactory(Items.EMERALD,1,Items.LILAC,2,12,1))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,1) { factories -> factories.add(TradeFactory(Items.EMERALD,1,Items.ROSE_BUSH,2,12,1))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,1) { factories -> factories.add(TradeFactory(Items.BOOK,6,2,Items.EMERALD,1,16,2))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,2) { factories -> factories.add(TradeFactory(Items.EMERALD,1,RegisterItem.CITRINE,1,8,3))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,2) { factories -> factories.add(TradeFactory(Items.EMERALD,1,RegisterItem.SMOKY_QUARTZ,1,8,3))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,2) { factories -> factories.add(TradeFactory(Items.EMERALD,1,RegisterItem.DANBURITE,1,8,3))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,2) { factories -> factories.add(TradeFactory(Items.AMETHYST_SHARD,4,1,Items.EMERALD,1,12,4))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,2) { factories -> factories.add(TradeFactory(Items.EMERALD,16,Items.BOOK,1,RegisterItem.BOOK_OF_LORE,1,2,8))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,3) { factories -> factories.add(TradeFactory(Items.EMERALD,10,4,RegisterItem.OPAL,1,9,6))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,3) { factories -> factories.add(TradeFactory(Items.EMERALD,12,4,Items.COPPER_INGOT,1,RegisterTool.COPPER_HEADBAND,1,2,8))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,3) { factories -> factories.add(TradeFactory(Items.EMERALD,1,RegisterItem.MANA_POTION,1,12,5))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,3) { factories -> factories.add(TradeFactory(Items.EMERALD,12,4,Items.COPPER_INGOT,1,RegisterTool.COPPER_AMULET,1,2,8))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,3) { factories -> factories.add(TradeFactory(Items.EMERALD,12,4,Items.COPPER_INGOT,1,RegisterTool.COPPER_RING,1,2,8))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,4) { factories -> factories.add(TradeFactory(Items.DIAMOND,1,Items.EMERALD,4,8,6))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,4) { factories -> factories.add(TradeFactory(Items.EMERALD,24,8,RegisterItem.OPAL,1,RegisterItem.IRIDESCENT_ORB,1,1,12))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,4) { factories -> factories.add(TradeFactory(Items.EMERALD,12,RegisterItem.GLOWING_FRAGMENT,1,RegisterItem.MALACHITE_FIGURINE,1,1,24))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,5) { factories -> factories.add(TradeFactory(Items.DIAMOND,16,4,RegisterItem.BOOK_OF_LORE,1,RegisterItem.BOOK_OF_MYTHOS,1,1,20))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,5) { factories -> factories.add(TradeFactory(Items.EMERALD,16,Items.DIAMOND,1,RegisterItem.BRILLIANT_DIAMOND,1,1,20))}

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.ARMORER,3) { factories -> factories.add(TradeFactory(Items.EMERALD,16,4,RegisterArmor.STEEL_BOOTS,1,6,4))}
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.ARMORER,3) { factories -> factories.add(TradeFactory(Items.EMERALD,16,4,RegisterArmor.STEEL_HELMET,1,6,4))}

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FLETCHER,5) { factories -> factories.add(TradeFactory(Items.EMERALD,24,6,Items.CROSSBOW,1,RegisterTool.SNIPER_BOW,1,6,12))}


        if (AiConfig.villages.vanilla.enableDesertWorkshops.get()){
            val desertId = Identifier(AI.MOD_ID + ":village/desert_crystal_workshop")
            FabricStructurePoolRegistry.registerSimple(Identifier("minecraft:village/desert/houses"),desertId,AiConfig.villages.vanilla.desertWorkshopWeight.get())

            val desertZombieId = Identifier(AI.MOD_ID + ":village/desert_crystal_workshop_zombie")
            FabricStructurePoolRegistry.register(Identifier("minecraft:village/desert/zombie/houses"),desertZombieId,1, StructureProcessorLists.ZOMBIE_DESERT)
        }
        if (AiConfig.villages.vanilla.enablePlainsWorkshops.get()){
            val plainsId = Identifier(AI.MOD_ID + ":village/plains_crystal_workshop")
            FabricStructurePoolRegistry.register(Identifier("minecraft:village/plains/houses"),plainsId,AiConfig.villages.vanilla.plainsWorkshopWeight.get(), StructureProcessorLists.MOSSIFY_10_PERCENT)

            val plainsZombieId = Identifier(AI.MOD_ID + ":village/plains_crystal_workshop_zombie")
            FabricStructurePoolRegistry.register(Identifier("minecraft:village/plains/zombie/houses"),plainsZombieId,1, StructureProcessorLists.ZOMBIE_PLAINS)
        }
        if (AiConfig.villages.vanilla.enableSavannaWorkshops.get()) {
            val savannaId = Identifier(AI.MOD_ID + ":village/savanna_crystal_workshop")
            FabricStructurePoolRegistry.registerSimple(Identifier("minecraft:village/savanna/houses"),savannaId,AiConfig.villages.vanilla.savannaWorkshopWeight.get())

            val savannaZombieId = Identifier(AI.MOD_ID + ":village/savanna_crystal_workshop_zombie")
            FabricStructurePoolRegistry.register(Identifier("minecraft:village/savanna/zombie/houses"),savannaZombieId,1, StructureProcessorLists.ZOMBIE_SAVANNA)
        }
        if (AiConfig.villages.vanilla.enableSnowyWorkshops.get()) {
            val snowyId = Identifier(AI.MOD_ID + ":village/snowy_crystal_workshop")
            FabricStructurePoolRegistry.registerSimple(Identifier("minecraft:village/snowy/houses"),snowyId,AiConfig.villages.vanilla.snowyWorkshopWeight.get())

            val snowyZombieId = Identifier(AI.MOD_ID + ":village/snowy_crystal_workshop_zombie")
            FabricStructurePoolRegistry.register(Identifier("minecraft:village/snowy/zombie/houses"),snowyZombieId,1, StructureProcessorLists.ZOMBIE_SNOWY)
        }
        if (AiConfig.villages.vanilla.enableTaigaWorkshops.get()) {
            val taigaId = Identifier(AI.MOD_ID + ":village/taiga_crystal_workshop")
            FabricStructurePoolRegistry.register(Identifier("minecraft:village/taiga/houses"),taigaId,AiConfig.villages.vanilla.taigaWorkshopWeight.get(), StructureProcessorLists.MOSSIFY_20_PERCENT)

            val taigaZombieId = Identifier(AI.MOD_ID + ":village/taiga_crystal_workshop_zombie")
            FabricStructurePoolRegistry.register(Identifier("minecraft:village/taiga/zombie/houses"),taigaZombieId,1, StructureProcessorLists.ZOMBIE_TAIGA)
        }

        if(FabricLoader.getInstance().isModLoaded("ctov")){
            if (AiConfig.villages.ctov.enableCtovWorkshops.get()){
                val desertId = Identifier(AI.MOD_ID + ":village/desert_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("ctov:village/desert/house"),desertId,AiConfig.villages.vanilla.desertWorkshopWeight.get())
                val desertOasisId = Identifier(AI.MOD_ID + ":village/desert_oasis_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("ctov:village/desert_oasis/house"),desertOasisId,AiConfig.villages.vanilla.desertWorkshopWeight.get())

                val plainsId = Identifier(AI.MOD_ID + ":village/plains_crystal_workshop")
                FabricStructurePoolRegistry.register(Identifier("ctov:village/plains/house"),plainsId,AiConfig.villages.vanilla.plainsWorkshopWeight.get(), StructureProcessorLists.MOSSIFY_10_PERCENT)
                val plainsFortId = Identifier(AI.MOD_ID + ":village/plains_fortified_crystal_workshop")
                FabricStructurePoolRegistry.register(Identifier("ctov:village/plains_fortified/house"),plainsFortId,AiConfig.villages.vanilla.plainsWorkshopWeight.get(), StructureProcessorLists.MOSSIFY_10_PERCENT)

                val savannaId = Identifier(AI.MOD_ID + ":village/savanna_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("ctov:village/savanna/house"),savannaId,AiConfig.villages.vanilla.savannaWorkshopWeight.get())
                val savannaNaId = Identifier(AI.MOD_ID + ":village/savanna_na_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("ctov:village/savanna_na/house"),savannaNaId,AiConfig.villages.vanilla.savannaWorkshopWeight.get())

                val snowyId = Identifier(AI.MOD_ID + ":village/snowy_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("ctov:village/snowy_igloo/house"),snowyId,AiConfig.villages.vanilla.snowyWorkshopWeight.get())
                val xmasId = Identifier(AI.MOD_ID + ":village/christmas_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("ctov:village/christmas/house"),xmasId,AiConfig.villages.vanilla.snowyWorkshopWeight.get())

                val taigaId = Identifier(AI.MOD_ID + ":village/taiga_crystal_workshop")
                FabricStructurePoolRegistry.register(Identifier("ctov:village/taiga/house"),taigaId,AiConfig.villages.vanilla.taigaWorkshopWeight.get(), StructureProcessorLists.MOSSIFY_20_PERCENT)
                val taigaFortId = Identifier(AI.MOD_ID + ":village/taiga_fortified_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("ctov:village/taiga_fortified/house"),taigaFortId,AiConfig.villages.vanilla.taigaWorkshopWeight.get())

                val beachId = Identifier(AI.MOD_ID + ":village/beach_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("ctov:village/beach/house"),beachId,AiConfig.villages.ctov.beachWorkshopWeight.get())

                val darkForestId = Identifier(AI.MOD_ID + ":village/halloween_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("ctov:village/halloween/house"),darkForestId,AiConfig.villages.ctov.darkForestWorkshopWeight.get())

                val jungleId = Identifier(AI.MOD_ID + ":village/jungle_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("ctov:village/jungle/house"),jungleId,AiConfig.villages.ctov.jungleWorkshopWeight.get())
                val jungleTreeId = Identifier(AI.MOD_ID + ":village/jungle_tree_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("ctov:village/jungle_tree/house"),jungleTreeId,AiConfig.villages.ctov.jungleTreeWorkshopWeight.get())

                val mesaId = Identifier(AI.MOD_ID + ":village/mesa_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("ctov:village/mesa/house"),mesaId,AiConfig.villages.ctov.mesaWorkshopWeight.get())
                val mesaFortId = Identifier(AI.MOD_ID + ":village/mesa_fortified_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("ctov:village/mesa_fortified/house"),mesaFortId,AiConfig.villages.ctov.mesaFortifiedWorkshopWeight.get())

                val mountId = Identifier(AI.MOD_ID + ":village/mountain_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("ctov:village/mountain/house"),mountId,AiConfig.villages.ctov.mountainWorkshopWeight.get())
                val mountAlpId = Identifier(AI.MOD_ID + ":village/mountain_alpine_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("ctov:village/mountain_alpine/house"),mountAlpId,AiConfig.villages.ctov.mountainAlpineWorkshopWeight.get())

                val mushroomId = Identifier(AI.MOD_ID + ":village/mushroom_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("ctov:village/mushroom/house"),mushroomId,AiConfig.villages.ctov.mushroomWorkshopWeight.get())

                val swampId = Identifier(AI.MOD_ID + ":village/swamp_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("ctov:village/swamp/house"),swampId,AiConfig.villages.ctov.swampWorkshopWeight.get())
                val swampFortId = Identifier(AI.MOD_ID + ":village/swamp_fortified_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("ctov:village/swamp_fortified/house"),swampFortId,AiConfig.villages.ctov.swampFortifiedWorkshopWeight.get())
            }
        }

        if(FabricLoader.getInstance().isModLoaded("repurposed_structures")){
            if (AiConfig.villages.rs.enableRsWorkshops.get()) {
                val badlandsId = Identifier(AI.MOD_ID + ":village/badlands_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("repurposed_structures:villages/badlands/houses"), badlandsId, AiConfig.villages.rs.badlandsWorkshopWeight.get())

                val birchId = Identifier(AI.MOD_ID + ":village/birch_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("repurposed_structures:villages/birch/houses"), birchId, AiConfig.villages.rs.birchWorkshopWeight.get())

                val darkId = Identifier(AI.MOD_ID + ":village/dark_forest_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("repurposed_structures:villages/dark_forest/houses"), darkId, AiConfig.villages.rs.darkForestWorkshopWeight.get())

                val giantId = Identifier(AI.MOD_ID + ":village/giant_taiga_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("repurposed_structures:villages/giant_taiga/houses"), giantId, AiConfig.villages.rs.giantTaigaWorkshopWeight.get())

                val jungleId = Identifier(AI.MOD_ID + ":village/jungle_rs_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("repurposed_structures:villages/jungle/houses"), jungleId, AiConfig.villages.rs.jungleWorkshopWeight.get())

                val mountainsId = Identifier(AI.MOD_ID + ":village/mountains_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("repurposed_structures:villages/mountains/houses"), mountainsId, AiConfig.villages.rs.mountainsWorkshopWeight.get())

                val mushroomId = Identifier(AI.MOD_ID + ":village/mushroom_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("repurposed_structures:villages/mushroom/houses"), mushroomId, AiConfig.villages.rs.mushroomsWorkshopWeight.get())

                val oakId = Identifier(AI.MOD_ID + ":village/oak_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("repurposed_structures:villages/oak/houses"), oakId, AiConfig.villages.rs.oakWorkshopWeight.get())

                val swampId = Identifier(AI.MOD_ID + ":village/swamp_rs_crystal_workshop")
                FabricStructurePoolRegistry.registerSimple(Identifier("repurposed_structures:villages/swamp/houses"), swampId, AiConfig.villages.rs.swampWorkshopWeight.get())
            }

        }

    }


    internal class TradeFactory(offerItem: ItemConvertible, price: Int, rewardItem: ItemConvertible, reward: Int, _maxUses: Int, _experience: Int) :
        TradeOffers.Factory {

        constructor(offerItem: ItemConvertible, price: Int,randomPriceMod: Int, rewardItem: ItemConvertible, reward: Int, _maxUses: Int, _experience: Int) : this(offerItem,price, rewardItem, reward, _maxUses, _experience){
            randomPriceModifier = randomPriceMod
        }

        constructor(offerItem: ItemConvertible, price: Int,processItem: ItemConvertible, processAmount: Int, rewardItem: ItemConvertible, reward: Int, _maxUses: Int, _experience: Int) : this(offerItem,price, rewardItem, reward, _maxUses, _experience){
            process = processItem.asItem()
            amountToProcess = processAmount
        }

        constructor(offerItem: ItemConvertible, price: Int,randomPriceMod: Int, processItem: ItemConvertible, processAmount: Int, rewardItem: ItemConvertible, reward: Int, _maxUses: Int, _experience: Int) : this(offerItem,price, rewardItem, reward, _maxUses, _experience){
            process = processItem.asItem()
            amountToProcess = processAmount
            randomPriceModifier = randomPriceMod
        }

        private var process: Item? = null
        private var amountToProcess: Int? = null
        private var randomPriceModifier: Int? = null

        private val buy: Item
        private val sell: Item
        private val buyPrice: Int
        private val sellPrice: Int
        private val maxUses: Int
        private val experience: Int
        private val multiplier: Float

        override fun create(entity: Entity, random: Random): TradeOffer {
            val buyPriceFinal: Int = if (randomPriceModifier != null){
                val rpm = randomPriceModifier?:1
                val rnd = random.nextInt(2 * rpm + 1) - rpm
                buyPrice + rnd
            } else {
                buyPrice
            }
            val buyStack = ItemStack(buy, buyPriceFinal)
            val sellStack = ItemStack(sell, sellPrice)
            return if (process != null && amountToProcess != null) {
                val processAmount: Int = amountToProcess?:1
                val processStack = ItemStack(process,processAmount)
                TradeOffer(buyStack, processStack, sellStack, maxUses, experience, multiplier)
            } else {

                TradeOffer(buyStack, sellStack, maxUses, experience, multiplier)
            }
        }

        init {
            buy = offerItem.asItem()
            sell = rewardItem.asItem()
            buyPrice = price
            sellPrice = reward
            maxUses = _maxUses
            experience = _experience
            multiplier = 0.05f
        }
    }

}
