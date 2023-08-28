@file:Suppress("unused")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.registry.RegisterAttribute
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.item.*
import me.fzzyhmstrs.amethyst_imbuement.item.AiItemSettings.AiItemGroup
import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfLoreItem
import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfMythosItem
import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfTalesItem
import me.fzzyhmstrs.amethyst_imbuement.item.book.GlisteringTomeItem
import me.fzzyhmstrs.amethyst_imbuement.item.promise.*
import me.fzzyhmstrs.amethyst_imbuement.spells.DebugAugment
import me.fzzyhmstrs.fzzy_core.item_util.CustomFlavorItem
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.item.*
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text
import net.minecraft.util.Rarity
import java.util.*

// don't know if this is better as a class or object. as an object it allows me to call it without needing to initialize an instance of it.
object RegisterItem {

    private val regItem: MutableList<Item> = mutableListOf()

    private fun <T: Item> register(item: T, name: String): T{
        if (item is IgnitedGemItem){
            GemOfPromiseItem.register(item)
        }
        regItem.add(item)
        return Registry.register(Registries.ITEM,AI.identity(name), item)
    }


    //basic ingredients
    val CITRINE = register(Item(AiItemSettings().aiGroup(AiItemGroup.GEM)),"citrine")
    val SMOKY_QUARTZ = register(Item(AiItemSettings().aiGroup(AiItemGroup.GEM)),"smoky_quartz")
    val IMBUED_QUARTZ = register(Item(AiItemSettings().aiGroup(AiItemGroup.GEM)),"imbued_quartz")
    val IMBUED_LAPIS = register(Item(AiItemSettings().aiGroup(AiItemGroup.GEM)),"imbued_lapis")
    val DANBURITE = register(Item(AiItemSettings().aiGroup(AiItemGroup.GEM)),"danburite")
    val MOONSTONE = register(Item(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)),"moonstone")
    val OPAL = register(Item(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)),"opal")
    val GARNET = register(Item(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)),"garnet")
    val PYRITE = register(Item(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)),"pyrite")
    val TIGERS_EYE = register(SpellcastersReagentFlavorItem(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
        EntityAttributeModifier(UUID.fromString("64399f14-d25b-11ed-afa1-0242ac120002"),"tigers_eye_modifier",0.15,EntityAttributeModifier.Operation.ADDITION),
        AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.RARE)),"tigers_eye")
    val CHARGED_MOONSTONE = register(Item(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.RARE)),"charged_moonstone")
    val ENERGETIC_OPAL = register(SpellcastersReagentFlavorItem(EntityAttributes.GENERIC_MOVEMENT_SPEED,
        EntityAttributeModifier(UUID.fromString("1ac772d4-d25b-11ed-afa1-0242ac120002"),"energetic_modifier",0.03,EntityAttributeModifier.Operation.MULTIPLY_TOTAL),
        AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.RARE)).withGlint(),"energetic_opal")
    val AMETRINE = register(CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.RARE)),"ametrine") // item is custom for flavor text
    val CELESTINE = register(CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.EPIC)).withGlint(),"celestine") // item is custom for flavor text. need texture
    val STEEL_INGOT = register(Item(AiItemSettings().aiGroup(AiItemGroup.GEM)),"steel_ingot")
    val BERYL_COPPER_INGOT = register(Item(AiItemSettings().aiGroup(AiItemGroup.GEM)),"beryl_copper_ingot")
    val SHIMMERING_FABRIC = register(Item(AiItemSettings()),"shimmering_fabric")

    //scepter update gem and found/crafted items
    val GEM_OF_PROMISE = register(GemOfPromiseItem(AiItemSettings().aiGroup(AiItemGroup.GEM).maxCount(1))
        .withFlavorDefaultPath(AI.identity("gem_of_promise"))
        .withFlavorDescDefaultPath(AI.identity("gem_of_promise")),"gem_of_promise")
    val GEM_DUST = register(Item(AiItemSettings().aiGroup(AiItemGroup.GEM)),"gem_dust")
    val SPARKING_GEM = register(SparkingGemItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)),"sparking_gem")
    val BLAZING_GEM = register(BlazingGemItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)),"blazing_gem")
    val INQUISITIVE_GEM = register(InquisitiveGemItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)),"inquisitive_gem")
    val LETHAL_GEM = register(LethalGemItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)),"lethal_gem")
    val HEALERS_GEM = register(HealersGemItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)),"healers_gem")
    val BRUTAL_GEM = register(BrutalGemItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)),"brutal_gem")
    val MYSTICAL_GEM = register(MysticalGemItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)),"mystical_gem")
    val GLOWING_FRAGMENT = register(SpellcastersReagentFlavorItem(RegisterAttribute.SPELL_MANA_COST,
        EntityAttributeModifier(UUID.fromString("38ea2c82-ce89-11ed-afa1-0242ac120002"),"glowing_modifier",0.03,EntityAttributeModifier.Operation.MULTIPLY_TOTAL),
        AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.RARE)),"glowing_fragment")
    val BRILLIANT_DIAMOND = register(SpellcastersReagentFlavorItem(RegisterAttribute.SPELL_MANA_COST,
        EntityAttributeModifier(UUID.fromString("402ea570-c404-11ed-afa1-0242ac120002"),"brilliant_modifier",0.075,EntityAttributeModifier.Operation.MULTIPLY_TOTAL),
        AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.EPIC)).withGlint(),"brilliant_diamond")
    val ACCURSED_FIGURINE = register(SpellcastersReagentFlavorItem(RegisterAttribute.DAMAGE_MULTIPLICATION,
        EntityAttributeModifier(UUID.fromString("57ac057e-c505-11ed-afa1-0242ac120002"),"accursed_modifier",0.1,EntityAttributeModifier.Operation.MULTIPLY_TOTAL),
        AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.RARE)),"accursed_figurine")
    val MALACHITE_FIGURINE = register(SpellcastersReagentFlavorItem(RegisterAttribute.SPELL_DURATION,
        EntityAttributeModifier(UUID.fromString("402ebf88-c404-11ed-afa1-0242ac120002"),"malachite_modifier",0.075,EntityAttributeModifier.Operation.MULTIPLY_TOTAL),
        AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.RARE)),"malachite_figurine")
    val RESONANT_ROD = register(SpellcastersReagentFlavorItem(RegisterAttribute.SPELL_DAMAGE,
        EntityAttributeModifier(UUID.fromString("402ec2da-c404-11ed-afa1-0242ac120002"),"resonant_modifier",0.05,EntityAttributeModifier.Operation.MULTIPLY_TOTAL),
        AiItemSettings().aiGroup(AiItemGroup.GEM)),"resonant_rod")
    //val SURVEY_MAP = SurveyMapItem(FabricItemSettings()),"survey_map")
    val HEARTSTONE = register(SpellcastersReagentFlavorItem(RegisterAttribute.SPELL_AMPLIFIER,
        EntityAttributeModifier(UUID.fromString("402ec528-c404-11ed-afa1-0242ac120002"),"heartstone_modifier",1.0,EntityAttributeModifier.Operation.ADDITION),
        AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)).withGlint(),"heartstone")
    val IRIDESCENT_ORB = register(CustomFlavorItem(FabricItemSettings().rarity(Rarity.UNCOMMON)),"iridescent_orb")
    val LUSTROUS_SPHERE = register(SpellcastersReagentFlavorItem(RegisterAttribute.SPELL_LEVEL,
        EntityAttributeModifier(UUID.fromString("402ec79e-c404-11ed-afa1-0242ac120002"),"lustrous_modifier",0.075,EntityAttributeModifier.Operation.MULTIPLY_TOTAL),
        FabricItemSettings().rarity(Rarity.RARE)).withGlint(),"lustrous_sphere")
    val KNOWLEDGE_POWDER = register(SpellcastersReagentFlavorItem(RegisterAttribute.PLAYER_EXPERIENCE,
        EntityAttributeModifier(UUID.fromString("72321934-ccc0-11ed-afa1-0242ac120002"),"knowledge_modifier",0.05,EntityAttributeModifier.Operation.ADDITION),
        AiItemSettings().aiGroup(AiItemGroup.GEM)),"knowledge_powder")
    val XP_BUSH_SEED = register(AliasedBlockItem(RegisterBlock.EXPERIENCE_BUSH,FabricItemSettings()),"xp_bush_seed")
    val GOLDEN_HEART = register(SpellcastersReagentFlavorItem(RegisterAttribute.SPELL_RANGE,
        EntityAttributeModifier(UUID.fromString("f62a18b6-c407-11ed-afa1-0242ac120002"),"golden_modifier",0.125,EntityAttributeModifier.Operation.MULTIPLY_TOTAL),
        AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)),"golden_heart")
    val CRYSTALLINE_HEART = register(CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.RARE)).withGlint(),"crystalline_heart") //item is custom for flavor text

    // book and other used items
    val BOOK_OF_LORE = register(BookOfLoreItem(FabricItemSettings().maxCount(8).rarity(Rarity.RARE)),"book_of_lore") as CustomFlavorItem
    val BOOK_OF_MYTHOS = register(BookOfMythosItem(FabricItemSettings().maxCount(8).rarity(Rarity.RARE)).withGlint(),"book_of_mythos") as CustomFlavorItem
    val BOOK_OF_TALES = register(BookOfTalesItem(FabricItemSettings().maxCount(8).rarity(Rarity.RARE)).withGlint(),"book_of_tales") as CustomFlavorItem
    val GLISTERING_TOME = register(GlisteringTomeItem(FabricItemSettings()),"glistering_tome")
    val GLISTERING_KEY = register(GlisteringKeyItem(FabricItemSettings()),"glistering_key")
    val MYSTERIOUS_MAGNIFYING_GLASS = register(CustomFlavorItem(FabricItemSettings()),"mysterious_magnifying_glass")
    val MANA_POTION = register(ManaPotionItem(FabricItemSettings().maxCount(16)),"mana_potion")
    val DAZZLING_MELON_SLICE = register(Item(FabricItemSettings().rarity(Rarity.UNCOMMON).food(FoodComponent.Builder().hunger(4).saturationModifier(0.75f).statusEffect(
        StatusEffectInstance(RegisterStatus.BLESSED, 300),1f).build())),"dazzling_melon_slice")

    ///////////////////////////

    val AI_GROUP: ItemGroup by lazy{
        registerItemGroup()
    }

    fun registerItemGroup(): ItemGroup{
        return Registry.register(Registries.ITEM_GROUP,AI.identity("ai_group"), FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.amethyst_imbuement.ai_group"))
            .icon { ItemStack(RegisterBlock.IMBUING_TABLE.asItem()) }
            .entries { _, entries ->
                entries.addAll(regItem.stream()
                    .map { item -> ItemStack(item) }.toList())
                entries.addAll(RegisterTool.regTool.stream()
                    .map { item -> ItemStack(item) }
                    .toList())
                entries.addAll(RegisterScepter.regScepter.stream()
                    .map { item -> ItemStack(item) }
                    .toList())
                entries.addAll(RegisterArmor.regArmor.stream()
                    .map { item -> ItemStack(item) }
                    .toList())
                entries.addAll(RegisterBlock.regBlock.values.stream()
                    .filter { block -> block !== RegisterBlock.EXPERIENCE_BUSH }
                    .map { block -> ItemStack(block.asItem()) }
                    .toList())
                entries.addAll(Registries.ENCHANTMENT.stream()
                    .filter { enchant -> enchant is ScepterAugment && enchant !is DebugAugment }
                    .map { enchant -> SpellScrollItem.createSpellScroll(enchant as ScepterAugment) }
                    .toList()
                )
            }.build())
    }

    fun registerAll() {
        val group = AI_GROUP
    }
}