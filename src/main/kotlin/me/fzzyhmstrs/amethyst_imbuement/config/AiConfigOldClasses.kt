package me.fzzyhmstrs.amethyst_imbuement.config

import me.fzzyhmstrs.amethyst_imbuement.tool.*
import me.fzzyhmstrs.fzzy_config.interfaces.OldClass
import net.minecraft.util.Identifier

object AiConfigOldClasses {

    class ItemsV1: OldClass<NewAiConfig.Items> {
        var giveGlisteringTome: Boolean = true
        var totemOfAmethystDurability: Int = 360
        var opalineDurability: Int = ScepterLvl1ToolMaterial.defaultDurability()
        var iridescentDurability: Int = ScepterLvl2ToolMaterial.defaultDurability()
        var lustrousDurability: Int = ScepterLvl3ToolMaterial.defaultDurability()
        var baseRegenRateTicks: Long = ScepterLvl1ToolMaterial.baseCooldown()
        var bladesDurability: Int = ScepterOfBladesToolMaterial.defaultDurability()
        var bladesDamage: Float = ScepterOfBladesToolMaterial.defaultAttackDamage()
        var lethalityDurability: Int = LethalityToolMaterial.defaultDurability()
        var lethalityDamage: Float = LethalityToolMaterial.defaultAttackDamage()
        override fun generateNewClass(): NewAiConfig.Items {
            val items = NewAiConfig.Items()
            items.giveGlisteringTome.validateAndSet(giveGlisteringTome)
            items.totemOfAmethystDurability.validateAndSet(totemOfAmethystDurability)
            items.scepters.opalineDurability.validateAndSet(opalineDurability)
            items.scepters.iridescentDurability.validateAndSet(iridescentDurability)
            items.scepters.lustrousDurability.validateAndSet(lustrousDurability)
            items.scepters.opalineCooldown.validateAndSet(baseRegenRateTicks)
            items.scepters.bladesDurability.validateAndSet(bladesDurability)
            items.scepters.bladesDamage.validateAndSet(bladesDamage)
            items.scepters.lethalityDurability.validateAndSet(lethalityDurability)
            items.scepters.lethalityDamage.validateAndSet(lethalityDamage)
            return items
        }
    }

    class AltarsV3: OldClass<NewAiConfig.Altars>{

        var experienceBushBonemealChance: Float = 0.4f
        var experienceBushGrowChance: Float = 0.15f
        var disenchantLevelCosts: List<Int> = listOf(11, 17, 24, 33, 44)
        var disenchantBaseDisenchantsAllowed: Int = 1
        var imbuingTableEnchantingEnabled: Boolean = true
        var imbuingTableReplaceEnchantingTable: Boolean = false
        var imbuingTableDifficultyModifier: Float = 1.0F
        var imbuingTableMatchEasyMagicBehavior: Boolean = true
        var imbuingTableEasyMagicRerollEnabled: Boolean = true
        var imbuingTableEasyMagicLevelCost: Int = 5
        var imbuingTableEasyMagicLapisCost: Int = 1
        var imbuingTableEasyMagicShowTooltip: Boolean = true
        var imbuingTableEasyMagicSingleEnchantTooltip: Boolean = true
        var imbuingTableMatchRerollBehavior: Boolean = true
        var imbuingTableRerollLevelCost: Int = 1
        var imbuingTableRerollLapisCost: Int = 0
        var altarOfExperienceBaseLevels: Int = 35
        var altarOfExperienceCandleLevelsPer: Int = 5
        var altarOfExperienceCustomXpMethod: Boolean = true

        override fun generateNewClass(): NewAiConfig.Altars {
            val altars = NewAiConfig.Altars()
            altars.xpBush.bonemealChance.validateAndSet(experienceBushBonemealChance)
            altars.xpBush.growChance.validateAndSet(experienceBushGrowChance)
            
            altars.disenchanter.levelCosts.validateAndSet(disenchantLevelCosts)
            altars.disenchanter.baseDisenchantsAllowed.validateAndSet(disenchantBaseDisenchantsAllowed)
            
            altars.imbuing.enchantingEnabled.validateAndSet(imbuingTableEnchantingEnabled)
            altars.imbuing.replaceEnchantingTable.validateAndSet(imbuingTableReplaceEnchantingTable)
            altars.imbuing.difficultyModifier.validateAndSet(imbuingTableDifficultyModifier)
            
            altars.imbuing.easyMagic.matchEasyMagicBehavior.validateAndSet(imbuingTableMatchEasyMagicBehavior)
            altars.imbuing.easyMagic.rerollEnabled.validateAndSet(imbuingTableEasyMagicRerollEnabled)
            altars.imbuing.easyMagic.levelCost.validateAndSet(imbuingTableEasyMagicLevelCost)
            altars.imbuing.easyMagic.lapisCost.validateAndSet(imbuingTableEasyMagicLapisCost)
            altars.imbuing.easyMagic.showTooltip.validateAndSet(imbuingTableEasyMagicShowTooltip)
            altars.imbuing.easyMagic.singleEnchantTooltip.validateAndSet(imbuingTableEasyMagicSingleEnchantTooltip)
            
            altars.imbuing.reroll.matchRerollBehavior.validateAndSet(imbuingTableMatchRerollBehavior)
            altars.imbuing.reroll.levelCost.validateAndSet(imbuingTableRerollLevelCost)
            altars.imbuing.reroll.lapisCost.validateAndSet(imbuingTableRerollLapisCost)
            
            altars.altar.baseLevels.validateAndSet(altarOfExperienceBaseLevels)
            altars.altar.candleLevelsPer.validateAndSet(altarOfExperienceCandleLevelsPer)
            altars.altar.customXpMethod.validateAndSet(altarOfExperienceCustomXpMethod)
            return altars
        }
    }
    
    class VillagesV1: OldClass<NewAiConfig.Villages>{
    
        var enableDesertWorkshops: Boolean = true
        var desertWorkshopWeight: Int = 2
        var enablePlainsWorkshops: Boolean = true
        var plainsWorkshopWeight: Int = 3
        var enableSavannaWorkshops: Boolean = true
        var savannaWorkshopWeight: Int = 3
        var enableSnowyWorkshops: Boolean = true
        var snowyWorkshopWeight: Int = 2
        var enableTaigaWorkshops: Boolean = true
        var taigaWorkshopWeight: Int = 3
        var enableCtovWorkshops: Boolean = true
        var ctovBeachWorkshopWeight: Int = 4
        var ctovDarkForestWorkshopWeight: Int = 4
        var ctovJungleWorkshopWeight: Int = 4
        var ctovJungleTreeWorkshopWeight: Int = 4
        var ctovMesaWorkshopWeight: Int = 4
        var ctovMesaFortifiedWorkshopWeight: Int = 4
        var ctovMountainWorkshopWeight: Int = 4
        var ctovMountainAlpineWorkshopWeight: Int = 4
        var ctovMushroomWorkshopWeight: Int = 4
        var ctovSwampWorkshopWeight: Int = 4
        var ctovSwampFortifiedWorkshopWeight: Int = 4
        var enableRsWorkshops: Boolean = true
        var rsBadlandsWorkshopWeight: Int = 2
        var rsBirchWorkshopWeight: Int = 2
        var rsDarkForestWorkshopWeight: Int = 2
        var rsGiantTaigaWorkshopWeight: Int = 1
        var rsJungleWorkshopWeight: Int = 2
        var rsMountainsWorkshopWeight: Int = 2
        var rsMushroomsWorkshopWeight: Int = 2
        var rsOakWorkshopWeight: Int = 2
        var rsSwampWorkshopWeight: Int = 2
        var rsCrimsonWorkshopWeight: Int = 2
        var rsWarpedWorkshopWeight: Int = 2
        
        override fun generateNewClass(): NewAiConfig.Villages {
            val villages = NewAiConfig.Villages()
            villages.vanilla.enableDesertWorkshops.validateAndSet(enableDesertWorkshops)
            villages.vanilla.desertWorkshopWeight.validateAndSet(desertWorkshopWeight)
            villages.vanilla.enablePlainsWorkshops.validateAndSet(enablePlainsWorkshops)
            villages.vanilla.plainsWorkshopWeight.validateAndSet(plainsWorkshopWeight)
            villages.vanilla.enableSavannaWorkshops.validateAndSet(enableSavannaWorkshops)
            villages.vanilla.savannaWorkshopWeight.validateAndSet(savannaWorkshopWeight)
            villages.vanilla.enableSnowyWorkshops.validateAndSet(enableSnowyWorkshops)
            villages.vanilla.snowyWorkshopWeight.validateAndSet(snowyWorkshopWeight)
            villages.vanilla.enableTaigaWorkshops.validateAndSet(enableTaigaWorkshops)
            villages.vanilla.taigaWorkshopWeight.validateAndSet(taigaWorkshopWeight)
            
            villages.ctov.enableCtovWorkshops.validateAndSet(enableCtovWorkshops)
            villages.ctov.beachWorkshopWeight.validateAndSet(ctovBeachWorkshopWeight)
            villages.ctov.darkForestWorkshopWeight.validateAndSet(ctovDarkForestWorkshopWeight)
            villages.ctov.jungleWorkshopWeight.validateAndSet(ctovJungleWorkshopWeight)
            villages.ctov.jungleTreeWorkshopWeight.validateAndSet(ctovJungleTreeWorkshopWeight)
            villages.ctov.mesaWorkshopWeight.validateAndSet(ctovMesaWorkshopWeight)
            villages.ctov.mesaFortifiedWorkshopWeight.validateAndSet(ctovMesaFortifiedWorkshopWeight)
            villages.ctov.mountainWorkshopWeight.validateAndSet(ctovMountainWorkshopWeight)
            villages.ctov.mountainAlpineWorkshopWeight.validateAndSet(ctovMountainAlpineWorkshopWeight)
            villages.ctov.mushroomWorkshopWeight.validateAndSet(ctovMushroomWorkshopWeight)
            villages.ctov.swampWorkshopWeight.validateAndSet(ctovSwampWorkshopWeight)
            villages.ctov.swampFortifiedWorkshopWeight.validateAndSet(ctovSwampFortifiedWorkshopWeight)
            
            villages.rs.enableRsWorkshops.validateAndSet(enableRsWorkshops)
            villages.rs.badlandsWorkshopWeight.validateAndSet(rsBadlandsWorkshopWeight)
            villages.rs.birchWorkshopWeight.validateAndSet(rsBirchWorkshopWeight)
            villages.rs.darkForestWorkshopWeight.validateAndSet(rsDarkForestWorkshopWeight)
            villages.rs.giantTaigaWorkshopWeight.validateAndSet(rsGiantTaigaWorkshopWeight)
            villages.rs.jungleWorkshopWeight.validateAndSet(rsJungleWorkshopWeight)
            villages.rs.mountainsWorkshopWeight.validateAndSet(rsMountainsWorkshopWeight)
            villages.rs.mushroomsWorkshopWeight.validateAndSet(rsMushroomsWorkshopWeight)
            villages.rs.oakWorkshopWeight.validateAndSet(rsOakWorkshopWeight)
            villages.rs.swampWorkshopWeight.validateAndSet(rsSwampWorkshopWeight)
            villages.rs.crimsonWorkshopWeight.validateAndSet(rsCrimsonWorkshopWeight)
            villages.rs.warpedWorkshopWeight.validateAndSet(rsWarpedWorkshopWeight)
            
            return villages
        }
    }
    
    class EnchantmentsV0: OldClass<NewAiConfig.Enchants>{
        var enabledEnchantments: Map<String,Boolean> = mapOf()
        
        override fun generateNewClass(): NewAiConfig.Enchants {
            val enchantments = NewAiConfig.Enchants()
            val newMap = enchantments.enabledEnchants.get().toMutableMap()
            for (entry in enabledEnchantments.entries){
                val key = "amethyst_imbuement:${entry.key}"
                val id = Identifier.tryParse(key)?:continue
                if (!newMap.containsKey(key)) continue
                newMap[key] = entry.value
            }
            enchantments.enabledEnchants.validateAndSet(newMap)
            return enchantments
        }
    }
    
    class TrinketsV0: OldClass<NewAiConfig.Trinkets>{
        var enabledAugments: Map<String,Boolean> = mapOf()
        
        override fun generateNewClass(): NewAiConfig.Trinkets {
            val trinkets = NewAiConfig.Trinkets()
            val newMap = trinkets.enabledAugments.get().toMutableMap()
            for (entry in enabledAugments.entries){
                val key = "amethyst_imbuement:${entry.key}"
                val id = Identifier.tryParse(key)?:continue
                if (!newMap.containsKey(key)) continue
                newMap[key] = entry.value
            }
            trinkets.enabledAugments.validateAndSet(newMap)
            return trinkets
        }
    }
    
    class EntitiesV0: OldClass<NewAiConfig.Entities>{
        var unhallowedBaseLifespan: Int = 2400
        var unhallowedBaseHealth: Double = 20.0
        var unhallowedBaseDamage: Float = 3.0f
        var crystalGolemSpellBaseLifespan: Int = 5500
        var crystalGolemSpellPerLvlLifespan: Int = 500
        var crystalGolemGuardianLifespan: Int = 900
        var crystalGolemBaseHealth: Double = 180.0
        var crystalGolemBaseDamage: Float = 20.0f
        
        override fun generateNewClass(): NewAiConfig.Entities {
            val entities = NewAiConfig.Entities()
            entities.unhallowed.baseLifespan.validateAndSet(unhallowedBaseLifespan)
            entities.unhallowed.baseHealth.validateAndSet(unhallowedBaseHealth)
            entities.unhallowed.baseDamage.validateAndSet(unhallowedBaseDamage)
            
            entities.crystalGolem.spellBaseLifespan.validateAndSet(crystalGolemSpellBaseLifespan)
            entities.crystalGolem.spellPerLvlLifespan.validateAndSet(crystalGolemSpellPerLvlLifespan)
            entities.crystalGolem.guardianLifespan.validateAndSet(crystalGolemGuardianLifespan)
            entities.crystalGolem.baseHealth.validateAndSet(crystalGolemBaseHealth)
            entities.crystalGolem.baseDamage.validateAndSet(crystalGolemBaseDamage)
            return entities
        }
    }
}
