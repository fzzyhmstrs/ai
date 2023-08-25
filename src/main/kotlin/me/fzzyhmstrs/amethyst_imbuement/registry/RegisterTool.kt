@file:Suppress("unused")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.registry.RegisterAttribute
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.*
import me.fzzyhmstrs.amethyst_imbuement.item.AiItemSettings.AiItemGroup
import me.fzzyhmstrs.amethyst_imbuement.item.custom.*
import me.fzzyhmstrs.amethyst_imbuement.item.promise.GemOfPromiseItem
import me.fzzyhmstrs.amethyst_imbuement.item.promise.IgnitedGemItem
import me.fzzyhmstrs.amethyst_imbuement.item.scepter.FzzyhammerItem
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.*
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Rarity
import java.util.*

// don't know if this is better as a class or object. as an object it allows me to call it without needing to initialize an instance of it.
object RegisterTool {

    internal val regTool: MutableList<Item> = mutableListOf()

    private fun <T: Item> register(item: T, name: String): T{
        if (item is IgnitedGemItem){
            GemOfPromiseItem.register(item)
        }
        regTool.add(item)
        return Registry.register(Registries.ITEM,AI.identity(name), item)
    }

    //tool and weapon items
    val FZZYHAMMER = register(FzzyhammerItem(FabricItemSettings().rarity(Rarity.EPIC)),"fzzyhammer")
    val GLISTERING_TRIDENT = register(GlisteringTridentItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(AiConfig.items.glisteringTridentDurability.get()).rarity(Rarity.RARE)),"glistering_trident")
    val SNIPER_BOW = register(SniperBowItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(AiConfig.items.sniperBowDurability.get()).rarity(Rarity.RARE)),"sniper_bow")
    val GARNET_SWORD = register(SwordItem(AiConfig.materials.tools.garnet,3 ,-2.4f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"garnet_sword")
    val GARNET_SHOVEL = register(ShovelItem(AiConfig.materials.tools.garnet,1.5f,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"garnet_shovel")
    val GARNET_PICKAXE = register(PickaxeItem(AiConfig.materials.tools.garnet,1,-2.8f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"garnet_pickaxe")
    val GARNET_AXE = register(AxeItem(AiConfig.materials.tools.garnet,5.0f,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"garnet_axe")
    val GARNET_HOE = register(HoeItem(AiConfig.materials.tools.garnet,-3,0.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"garnet_hoe")
    val GARNET_HORSE_ARMOR = register(HorseArmorItem(12,"garnet",AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"garnet_horse_armor")
    val GLOWING_BLADE = register(CustomSwordItem(AiConfig.materials.tools.glowing,3 ,-2.4f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"glowing_blade")
    val GLOWING_SPADE = register(CustomShovelItem(AiConfig.materials.tools.glowing,1.5f ,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"glowing_spade")
    val GLOWING_PICK = register(CustomPickaxeItem(AiConfig.materials.tools.glowing,1 ,-2.8f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"glowing_pick")
    val GLOWING_AXE = register(CustomAxeItem(AiConfig.materials.tools.glowing,5.0f ,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"glowing_axe")
    val GLOWING_HOE = register(CustomHoeItem(AiConfig.materials.tools.glowing,-3 ,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"glowing_hoe")
    val GLOWING_HORSE_ARMOR = register(FlavorHorseArmorItem(14,"glowing",AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"glowing_horse_armor")
    val STEEL_AXE = register(AxeItem(AiConfig.materials.tools.steel,5.0f,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"steel_axe")
    val STEEL_HOE = register(CustomHoeItem(AiConfig.materials.tools.steel,-3,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"steel_hoe")
    val STEEL_PICKAXE = register(PickaxeItem(AiConfig.materials.tools.steel,1,-2.8f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"steel_pickaxe")
    val STEEL_SHOVEL = register(ShovelItem(AiConfig.materials.tools.steel,1.5f,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"steel_shovel")
    val STEEL_SWORD = register(SwordItem(AiConfig.materials.tools.steel,3,-2.4f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"steel_sword")
    val STEEL_HORSE_ARMOR = register(HorseArmorItem(9,"steel",AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"steel_horse_armor")
    val AMETRINE_AXE = register(AxeItem(AiConfig.materials.tools.ametrine,5.0f,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"ametrine_axe")
    val AMETRINE_HOE = register(CustomHoeItem(AiConfig.materials.tools.ametrine,-3,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"ametrine_hoe")
    val AMETRINE_PICKAXE = register(PickaxeItem(AiConfig.materials.tools.ametrine,1,-2.8f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"ametrine_pickaxe")
    val AMETRINE_SHOVEL = register(ShovelItem(AiConfig.materials.tools.ametrine,1.5f,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"ametrine_shovel")
    val AMETRINE_SWORD = register(SwordItem(AiConfig.materials.tools.ametrine,3,-2.4f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"ametrine_sword")
    val AMETRINE_HORSE_ARMOR = register(HorseArmorItem(15,"ametrine",AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"ametrine_horse_armor")

    //trinket and books
    val COPPER_RING = register(CopperJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxCount(1)),"copper_ring")
    val COPPER_HEADBAND = register(CopperJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxCount(1)),"copper_headband")
    val COPPER_AMULET = register(CopperJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxCount(1)),"copper_amulet")
    val COPPER_WARD = register(CopperWardItem(RegisterAttribute.SHIELDING,
        EntityAttributeModifier(UUID.fromString("c66fd31a-ce6e-11ed-afa1-0242ac120002"),"ward_modifier",0.025,EntityAttributeModifier.Operation.ADDITION),
        AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(336)),"copper_ward")
    val STEEL_AMULET = register(SteelJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxCount(1)),"steel_amulet")
    val STEEL_HEADBAND = register(SteelJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxCount(1)),"steel_headband")
    val STEEL_RING = register(SteelJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxCount(1)),"steel_ring")
    val STEEL_WARD = register(SteelWardItem(EntityAttributes.GENERIC_ARMOR,
        EntityAttributeModifier(UUID.fromString("1f6875e4-d167-11ed-afa1-0242ac120002"),"steel_ward_modifier",1.25, EntityAttributeModifier.Operation.ADDITION),
        AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(424)),"steel_ward")
    val IMBUED_RING = register(ImbuedJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(AiConfig.items.manaItems.imbuedJewelryDurability.get())),"imbued_ring")
    val IMBUED_HEADBAND = register(ImbuedJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(AiConfig.items.manaItems.imbuedJewelryDurability.get())),"imbued_headband")
    val IMBUED_AMULET = register(ImbuedJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(AiConfig.items.manaItems.imbuedJewelryDurability.get())),"imbued_amulet")
    val IMBUED_WARD = register(ImbuedWardItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(AiConfig.items.manaItems.totemOfAmethystDurability.get())),"imbued_ward")
    val TOTEM_OF_AMETHYST = register(TotemItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(AiConfig.items.manaItems.totemOfAmethystDurability.get()).rarity(Rarity.UNCOMMON)),"totem_of_amethyst")
    val SPELLCASTERS_FOCUS = register(SpellcastersFocusItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).rarity(Rarity.UNCOMMON)),"spellcasters_focus")
    val WITCHES_ORB = register(WitchesOrbItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxCount(1).rarity(Rarity.RARE)).withGlint(),"witches_orb")
    ///////////////////////////

    fun registerAll() {
    }
}