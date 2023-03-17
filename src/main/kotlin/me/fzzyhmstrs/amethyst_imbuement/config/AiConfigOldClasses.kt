package me.fzzyhmstrs.amethyst_imbuement.config

import me.fzzyhmstrs.amethyst_imbuement.tool.*
import me.fzzyhmstrs.fzzy_config.interfaces.OldClass

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
        var imbuingTableEasyMagicLenientBookshelves: Boolean = false
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
            return altars
        }
    }
}