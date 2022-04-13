package me.fzzyhmstrs.amethyst_imbuement.registry

import fzzyhmstrs.structurized_reborn.impl.FabricStructurePoolRegistry
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.fabricmc.fabric.api.`object`.builder.v1.trade.TradeOfferHelper
import net.fabricmc.fabric.api.`object`.builder.v1.villager.VillagerProfessionBuilder
import net.fabricmc.fabric.api.`object`.builder.v1.world.poi.PointOfInterestHelper
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvents
import net.minecraft.structure.processor.StructureProcessorLists
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.village.TradeOffer
import net.minecraft.village.TradeOffers
import net.minecraft.village.VillagerProfession
import java.util.*

object RegisterVillager {

    private val CRYSTAL_ALTAR_POINT_OF_INTEREST = PointOfInterestHelper.register(Identifier(AI.MOD_ID,"crystal_altar_poi"),1,1,RegisterBlock.CRYSTAL_ALTAR)
    private val CRYSTAL_WITCH: VillagerProfession = VillagerProfessionBuilder.create().workstation(CRYSTAL_ALTAR_POINT_OF_INTEREST).id(Identifier("amethyst_imbuement","crystal_witch")).workSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE).build()
    val locationMap: MutableMap<String,Triple<String,String,String>> = mutableMapOf()

    fun registerAll(){
        Registry.register(Registry.VILLAGER_PROFESSION, Identifier(AI.MOD_ID,"crystal_witch") , CRYSTAL_WITCH)

        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,1) { factories -> factories.add(TradeFactory(Items.QUARTZ,16,Items.EMERALD,1,16,2))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,1) { factories -> factories.add(TradeFactory(Items.LAPIS_LAZULI,22,Items.EMERALD,1,12,2))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,1) { factories -> factories.add(TradeFactory(Items.EMERALD,1,Items.LILAC,2,12,1))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,1) { factories -> factories.add(TradeFactory(Items.EMERALD,1,Items.ROSE_BUSH,2,12,1))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,2) { factories -> factories.add(TradeFactory(Items.EMERALD,1,RegisterItem.CITRINE,2,9,3))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,2) { factories -> factories.add(TradeFactory(Items.EMERALD,1,RegisterItem.SMOKY_QUARTZ,2,9,3))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,2) { factories -> factories.add(TradeFactory(Items.AMETHYST_SHARD,4,Items.EMERALD,1,12,4))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,2) { factories -> factories.add(TradeFactory(Items.EMERALD,24,Items.BOOK,1,RegisterItem.BOOK_OF_LORE,1,2,4))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,3) { factories -> factories.add(TradeFactory(Items.EMERALD,16,RegisterItem.OPAL,1,9,6))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,3) { factories -> factories.add(TradeFactory(Items.EMERALD,12,4,Items.COPPER_INGOT,1,RegisterItem.COPPER_HEADBAND,1,2,8))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,3) { factories -> factories.add(TradeFactory(Items.EMERALD,1,RegisterItem.MANA_POTION,1,12,5))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,3) { factories -> factories.add(TradeFactory(Items.EMERALD,12,4,Items.COPPER_INGOT,1,RegisterItem.COPPER_AMULET,1,2,8))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,3) { factories -> factories.add(TradeFactory(Items.EMERALD,12,4,Items.COPPER_INGOT,1,RegisterItem.COPPER_RING,1,2,8))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,4) { factories -> factories.add(TradeFactory(Items.DIAMOND,1,Items.EMERALD,2,8,6))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,4) { factories -> factories.add(TradeFactory(Items.EMERALD,32,8,RegisterItem.OPAL,1,RegisterItem.IRIDESCENT_ORB,1,1,12))}
        TradeOfferHelper.registerVillagerOffers(CRYSTAL_WITCH,5) { factories -> factories.add(TradeFactory(Items.DIAMOND,24,RegisterItem.BOOK_OF_LORE,1,RegisterItem.BOOK_OF_MYTHOS,1,1,20))}

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.ARMORER,3) { factories -> factories.add(TradeFactory(Items.EMERALD,16,4,RegisterArmor.STEEL_BOOTS,1,6,4))}
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.ARMORER,3) { factories -> factories.add(TradeFactory(Items.EMERALD,16,4,RegisterArmor.STEEL_HELMET,1,6,4))}

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FLETCHER,5) { factories -> factories.add(TradeFactory(Items.EMERALD,24,6,Items.CROSSBOW,1,RegisterItem.SNIPER_BOW,1,6,12))}

        val plainsId = Identifier(AI.MOD_ID + ":village/plains_crystal_workshop")
        FabricStructurePoolRegistry.register(Identifier("minecraft:village/plains/houses"),plainsId,2, StructureProcessorLists.MOSSIFY_10_PERCENT)

        val plainsZombieId = Identifier(AI.MOD_ID + ":village/plains_crystal_workshop_zombie")
        FabricStructurePoolRegistry.register(Identifier("minecraft:village/plains/zombie/houses"),plainsZombieId,1, StructureProcessorLists.ZOMBIE_PLAINS)

        val taigaId = Identifier(AI.MOD_ID + ":village/taiga_crystal_workshop")
        FabricStructurePoolRegistry.register(Identifier("minecraft:village/taiga/houses"),taigaId,2, StructureProcessorLists.MOSSIFY_20_PERCENT)

        val taigaZombieId = Identifier(AI.MOD_ID + ":village/taiga_crystal_workshop_zombie")
        FabricStructurePoolRegistry.register(Identifier("minecraft:village/taiga/zombie/houses"),taigaZombieId,1, StructureProcessorLists.ZOMBIE_TAIGA)

        val snowyId = Identifier(AI.MOD_ID + ":village/snowy_crystal_workshop")
        FabricStructurePoolRegistry.register(Identifier("minecraft:village/snowy/houses"),snowyId,1, StructureProcessorLists.EMPTY)

        val snowyZombieId = Identifier(AI.MOD_ID + ":village/snowy_crystal_workshop_zombie")
        FabricStructurePoolRegistry.register(Identifier("minecraft:village/snowy/zombie/houses"),snowyZombieId,1, StructureProcessorLists.ZOMBIE_SNOWY)

        val savannaId = Identifier(AI.MOD_ID + ":village/savanna_crystal_workshop")
        FabricStructurePoolRegistry.register(Identifier("minecraft:village/savanna/houses"),savannaId,2, StructureProcessorLists.EMPTY)

        val savannaZombieId = Identifier(AI.MOD_ID + ":village/savanna_crystal_workshop_zombie")
        FabricStructurePoolRegistry.register(Identifier("minecraft:village/savanna/zombie/houses"),savannaZombieId,1, StructureProcessorLists.ZOMBIE_SAVANNA)

        val desertId = Identifier(AI.MOD_ID + ":village/desert_crystal_workshop")
        FabricStructurePoolRegistry.register(Identifier("minecraft:village/desert/houses"),desertId,1, StructureProcessorLists.EMPTY)

        val desertZombieId = Identifier(AI.MOD_ID + ":village/desert_crystal_workshop_zombie")
        FabricStructurePoolRegistry.register(Identifier("minecraft:village/desert/zombie/houses"),desertZombieId,1, StructureProcessorLists.ZOMBIE_DESERT)


        /*StructurePoolAddCallback.EVENT.register { structurePool ->
            when (structurePool.underlyingPool.id.toString()) {
                "minecraft:village/plains/houses" -> {
                    val id = AI.MOD_ID + ":village/plains_crystal_workshop"
                    val processor = StructureProcessorLists.MOSSIFY_10_PERCENT
                    val processorId = BuiltinRegistries.STRUCTURE_PROCESSOR_LIST.getId(processor.value()).toString()
                    val poolElement : StructurePoolElement = StructurePoolElement.ofProcessedLegacySingle(id,processor).apply(StructurePool.Projection.RIGID)
                    val type = Registry.STRUCTURE_POOL_ELEMENT.getId(poolElement.type).toString()
                    locationMap[id] = Triple(type,processorId,StructurePool.Projection.RIGID.id)
                    structurePool.addStructurePoolElement(poolElement, 2)

                    //structurePool.underlyingPool.getElementIndicesInRandomOrder(Random(124)).forEach { n -> println(n.toString())}
                }
                "minecraft:village/plains/zombie/houses" -> {
                    val id = AI.MOD_ID + ":village/plains_crystal_workshop_zombie"
                    val processor = StructureProcessorLists.ZOMBIE_PLAINS
                    val processorId = BuiltinRegistries.STRUCTURE_PROCESSOR_LIST.getId(processor.value()).toString()
                    val poolElement : StructurePoolElement = StructurePoolElement.ofProcessedLegacySingle(id,processor).apply(StructurePool.Projection.RIGID)
                    val type = Registry.STRUCTURE_POOL_ELEMENT.getId(poolElement.type).toString()
                    locationMap[id] = Triple(type,processorId,StructurePool.Projection.RIGID.id)
                    structurePool.addStructurePoolElement(poolElement, 1)
                }
                "minecraft:village/taiga/houses" -> {
                    val id = AI.MOD_ID + ":village/taiga_crystal_workshop"
                    val processor = StructureProcessorLists.MOSSIFY_20_PERCENT
                    val processorId = BuiltinRegistries.STRUCTURE_PROCESSOR_LIST.getId(processor.value()).toString()
                    val poolElement : StructurePoolElement = StructurePoolElement.ofProcessedLegacySingle(id,processor).apply(StructurePool.Projection.RIGID)
                    val type = Registry.STRUCTURE_POOL_ELEMENT.getId(poolElement.type).toString()
                    locationMap[id] = Triple(type,processorId,StructurePool.Projection.RIGID.id)
                    structurePool.addStructurePoolElement(poolElement, 2)
                }
                "minecraft:village/taiga/zombie/houses" -> {
                    val id = AI.MOD_ID + ":village/taiga_crystal_workshop_zombie"
                    val processor = StructureProcessorLists.ZOMBIE_TAIGA
                    val processorId = BuiltinRegistries.STRUCTURE_PROCESSOR_LIST.getId(processor.value()).toString()
                    val poolElement : StructurePoolElement = StructurePoolElement.ofProcessedLegacySingle(id,processor).apply(StructurePool.Projection.RIGID)
                    val type = Registry.STRUCTURE_POOL_ELEMENT.getId(poolElement.type).toString()
                    locationMap[id] = Triple(type,processorId,StructurePool.Projection.RIGID.id)
                    structurePool.addStructurePoolElement(poolElement, 1)
                }
                "minecraft:village/snowy/houses" -> {
                    val id = AI.MOD_ID + ":village/snowy_crystal_workshop"
                    val processor = StructureProcessorLists.EMPTY
                    val processorId = BuiltinRegistries.STRUCTURE_PROCESSOR_LIST.getId(processor.value()).toString()
                    val poolElement : StructurePoolElement = StructurePoolElement.ofProcessedLegacySingle(id,processor).apply(StructurePool.Projection.RIGID)
                    val type = Registry.STRUCTURE_POOL_ELEMENT.getId(poolElement.type).toString()
                    locationMap[id] = Triple(type,processorId,StructurePool.Projection.RIGID.id)
                    structurePool.addStructurePoolElement(poolElement, 1)
                }
                "minecraft:village/snowy/zombie/houses" -> {
                    val id = AI.MOD_ID + ":village/snowy_crystal_workshop_zombie"
                    val processor = StructureProcessorLists.ZOMBIE_SNOWY
                    val processorId = BuiltinRegistries.STRUCTURE_PROCESSOR_LIST.getId(processor.value()).toString()
                    val poolElement : StructurePoolElement = StructurePoolElement.ofProcessedLegacySingle(id,processor).apply(StructurePool.Projection.RIGID)
                    val type = Registry.STRUCTURE_POOL_ELEMENT.getId(poolElement.type).toString()
                    locationMap[id] = Triple(type,processorId,StructurePool.Projection.RIGID.id)
                    structurePool.addStructurePoolElement(poolElement, 1)
                }
                "minecraft:village/savanna/houses" -> {
                    val id = AI.MOD_ID + ":village/savanna_crystal_workshop"
                    val processor = StructureProcessorLists.EMPTY
                    val processorId = BuiltinRegistries.STRUCTURE_PROCESSOR_LIST.getId(processor.value()).toString()
                    val poolElement : StructurePoolElement = StructurePoolElement.ofProcessedLegacySingle(id,processor).apply(StructurePool.Projection.RIGID)
                    val type = Registry.STRUCTURE_POOL_ELEMENT.getId(poolElement.type).toString()
                    locationMap[id] = Triple(type,processorId,StructurePool.Projection.RIGID.id)
                    structurePool.addStructurePoolElement(poolElement, 2)
                }
                "minecraft:village/savanna/zombie/houses" -> {
                    val id = AI.MOD_ID + ":village/savanna_crystal_workshop_zombie"
                    val processor = StructureProcessorLists.ZOMBIE_SAVANNA
                    val processorId = BuiltinRegistries.STRUCTURE_PROCESSOR_LIST.getId(processor.value()).toString()
                    val poolElement : StructurePoolElement = StructurePoolElement.ofProcessedLegacySingle(id,processor).apply(StructurePool.Projection.RIGID)
                    val type = Registry.STRUCTURE_POOL_ELEMENT.getId(poolElement.type).toString()
                    locationMap[id] = Triple(type,processorId,StructurePool.Projection.RIGID.id)
                    structurePool.addStructurePoolElement(poolElement, 1)
                }
                "minecraft:village/desert/houses" -> {
                    val id = AI.MOD_ID + ":village/desert_crystal_workshop"
                    val processor = StructureProcessorLists.EMPTY
                    val processorId = BuiltinRegistries.STRUCTURE_PROCESSOR_LIST.getId(processor.value()).toString()
                    val poolElement : StructurePoolElement = StructurePoolElement.ofProcessedLegacySingle(id,processor).apply(StructurePool.Projection.RIGID)
                    val type = Registry.STRUCTURE_POOL_ELEMENT.getId(poolElement.type).toString()
                    locationMap[id] = Triple(type,processorId,StructurePool.Projection.RIGID.id)
                    structurePool.addStructurePoolElement(poolElement, 1)
                }
                "minecraft:village/desert/zombie/houses" -> {
                    val id = AI.MOD_ID + ":village/desert_crystal_workshop_zombie"
                    val processor = StructureProcessorLists.ZOMBIE_DESERT
                    val processorId = BuiltinRegistries.STRUCTURE_PROCESSOR_LIST.getId(processor.value()).toString()
                    val poolElement : StructurePoolElement = StructurePoolElement.ofProcessedLegacySingle(id,processor).apply(StructurePool.Projection.RIGID)
                    val type = Registry.STRUCTURE_POOL_ELEMENT.getId(poolElement.type).toString()
                    locationMap[id] = Triple(type,processorId,StructurePool.Projection.RIGID.id)
                    structurePool.addStructurePoolElement(poolElement, 1)
                }
            }
        }*/

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