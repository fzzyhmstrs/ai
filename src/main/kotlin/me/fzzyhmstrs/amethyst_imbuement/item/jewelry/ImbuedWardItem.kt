package me.fzzyhmstrs.amethyst_imbuement.item.jewelry

import me.fzzyhmstrs.amethyst_core.registry.RegisterAttribute
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.Reactant
import me.fzzyhmstrs.amethyst_imbuement.item.SpellcastersReagent
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTag
import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable
import me.fzzyhmstrs.fzzy_core.item_util.FlavorHelper
import me.fzzyhmstrs.fzzy_core.item_util.FlavorHelper.addFlavorText
import me.fzzyhmstrs.fzzy_core.mana_util.ManaItem
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.ShieldItem
import net.minecraft.nbt.NbtList
import net.minecraft.recipe.RecipeType
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.world.World
import java.util.*

class ImbuedWardItem(settings: Settings): ShieldItem(settings), Modifiable, Reactant, ManaItem, SpellcastersReagent {

    private val flavorText: MutableText by lazy{
        FlavorHelper.makeFlavorText(this)
    }

    private val flavorTextDesc: MutableText by lazy{
        FlavorHelper.makeFlavorTextDesc(this)
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, context)
        addFlavorText(tooltip, context, flavorText, flavorTextDesc)
    }

    override fun canReact(stack: ItemStack, reagents: List<ItemStack>, player: PlayerEntity?, type: RecipeType<*>?): Boolean {
        return true
    }

    override fun react(stack: ItemStack, reagents: List<ItemStack>, player: PlayerEntity?, type: RecipeType<*>?) {

        for (reagent in reagents){
            val item = reagent.item
            if (item is SpellcastersReagent && !reagent.isIn(RegisterTag.ALL_WARDS_TAG)){
                if (stack.nbt?.contains("AttributeModifiers") == true) return
                val attribute = item.getAttributeModifier()
                val list = NbtList()
                val nbt = SpellcastersReagent.toNbt(attribute)
                nbt.putString("Slot","offhand")
                list.add(nbt)
                stack.orCreateNbt.put("AttributeModifiers",list)
                return
            }
        }
        for (reagent in reagents){
            val item = reagent.item
            if (item is SpellcastersReagent && reagent.isIn(RegisterTag.ALL_WARDS_TAG)){
                if (stack.nbt?.contains("AttributeModifiers") == true) return
                val attribute = item.getAttributeModifier()
                val list = NbtList()
                val nbt = SpellcastersReagent.toNbt(attribute)
                nbt.putString("Slot","offhand")
                list.add(nbt)
                stack.orCreateNbt.put("AttributeModifiers",list)
                return
            }
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
}