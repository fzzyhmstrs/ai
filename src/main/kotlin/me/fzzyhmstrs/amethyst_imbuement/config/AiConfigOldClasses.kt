package me.fzzyhmstrs.amethyst_imbuement.config

import me.fzzyhmstrs.fzzy_config.interfaces.OldClass
import net.minecraft.util.Identifier

object AiConfigOldClasses {
    
    class VillagesV1: OldClass<AiConfig.Villages>{
    
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
        
        override fun generateNewClass(): AiConfig.Villages {
            val villages = AiConfig.Villages()
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
    
    class EnchantmentsV0: OldClass<AiConfig.Enchants>{
        var enabledEnchantments: Map<String,Boolean> = mapOf()
        
        override fun generateNewClass(): AiConfig.Enchants {
            val enchantments = AiConfig.Enchants()
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
    
    class TrinketsV0: OldClass<AiConfig.Trinkets>{
        var enabledAugments: Map<String,Boolean> = mapOf()
        
        override fun generateNewClass(): AiConfig.Trinkets {
            val trinkets = AiConfig.Trinkets()
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
    
    class EntitiesV0: OldClass<AiConfig.Entities>{
        var unhallowedBaseLifespan: Int = 2400
        var unhallowedBaseHealth: Double = 20.0
        var unhallowedBaseDamage: Float = 3.0f
        var crystalGolemSpellBaseLifespan: Int = 5500
        var crystalGolemSpellPerLvlLifespan: Int = 500
        var crystalGolemGuardianLifespan: Int = 900
        var crystalGolemBaseHealth: Double = 180.0
        var crystalGolemBaseDamage: Float = 20.0f
        
        override fun generateNewClass(): AiConfig.Entities {
            val entities = AiConfig.Entities()
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
