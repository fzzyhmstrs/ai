package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.registry.RegisterAttribute
import me.fzzyhmstrs.amethyst_imbuement.augment.ShieldingAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTag
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable
import me.fzzyhmstrs.fzzy_core.item_util.interfaces.Flavorful
import me.fzzyhmstrs.fzzy_core.mana_util.ManaItem
import me.fzzyhmstrs.fzzy_core.registry.EventRegistry
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.ShieldItem
import net.minecraft.nbt.NbtList
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import java.util.*

class ImbuedWardItem(settings: Settings): ShieldItem(settings), Modifiable, Reactant, ManaItem, SpellcastersReagent, Flavorful<ImbuedWardItem> {

    override var flavor: String = ""
    override var glint: Boolean = false
    override var flavorDesc: String = ""

    private val flavorText: MutableText by lazy{
        makeFlavorText()
    }

    private val flavorTextDesc: MutableText by lazy{
        makeFlavorTextDesc()
    }

    private fun makeFlavorText(): MutableText {
        val id = Registry.ITEM.getId(this)
        val key = "item.${id.namespace}.${id.path}.flavor"
        val text = AcText.translatable(key).formatted(Formatting.WHITE, Formatting.ITALIC)
        if (text.string == key) return AcText.empty()
        return text
    }

    private fun makeFlavorTextDesc(): MutableText {
        val id = Registry.ITEM.getId(this)
        val key = "item.${id.namespace}.${id.path}.flavor.desc"
        val text = AcText.translatable(key).formatted(Formatting.WHITE)
        if (text.string == key) return AcText.empty()
        return text
    }

    override fun flavorText(): MutableText {
        return flavorText
    }
    override fun flavorDescText(): MutableText {
        return flavorTextDesc
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, context)
        addFlavorText(tooltip, context)
    }

    override fun canReact(stack: ItemStack, reagents: List<ItemStack>): Boolean {
        return true
    }

    override fun react(stack: ItemStack, reagents: List<ItemStack>) {

        for (reagent in reagents){
            val item = reagent.item
            if (item is SpellcastersReagent && !reagent.isIn(RegisterTag.BASIC_WARDS_TAG)){
                if (stack.nbt?.contains("AttributeModifiers") == true) return
                val attribute = item.getAttributeModifier()
                val list = NbtList()
                val nbt = attribute.second.toNbt()
                nbt.putString("AttributeName", Registry.ATTRIBUTE.getId(attribute.first).toString())
                nbt.putString("Slot","offhand")
                list.add(nbt)
                stack.orCreateNbt.put("AttributeModifiers",list)
                return
            }
        }
        for (reagent in reagents){
            val item = reagent.item
            if (item is SpellcastersReagent && reagent.isIn(RegisterTag.BASIC_WARDS_TAG)){
                if (stack.nbt?.contains("AttributeModifiers") == true) return
                val attribute = item.getAttributeModifier()
                val list = NbtList()
                val nbt = attribute.second.toNbt()
                nbt.putString("AttributeName", Registry.ATTRIBUTE.getId(attribute.first).toString())
                nbt.putString("Slot","offhand")
                list.add(nbt)
                stack.orCreateNbt.put("AttributeModifiers",list)
                return
            }
        }
    }

    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        if (EventRegistry.ticker_30.isReady() && entity is LivingEntity){
            ShieldingAugment.refreshTrinkets(entity)
        }
    }

    override fun getItemBarColor(stack: ItemStack): Int {
        return AiConfig.items.manaItems.getItemBarColor(stack)
    }

    override fun canRepair(stack: ItemStack, ingredient: ItemStack): Boolean {
        return ingredient.isOf(Items.AMETHYST_SHARD) && stack.item is ImbuedJewelryItem
    }

    override fun getRepairTime(): Int {
        return 0
    }

    override fun getAttributeModifier(): Pair<EntityAttribute, EntityAttributeModifier> {
        return Pair(RegisterAttribute.SHIELDING,
            EntityAttributeModifier(UUID.fromString("31e9c8aa-ce72-11ed-afa1-0242ac120002"),"imbued_ward_modifier",0.05,EntityAttributeModifier.Operation.ADDITION))
    }

    override fun getFlavorItem(): ImbuedWardItem {
        return this
    }
}