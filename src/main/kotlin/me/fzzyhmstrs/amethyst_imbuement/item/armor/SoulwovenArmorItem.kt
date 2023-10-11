package me.fzzyhmstrs.amethyst_imbuement.item.armor

import me.fzzyhmstrs.amethyst_core.modifier_util.ModifierHelper
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterModifier
import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable
import me.fzzyhmstrs.fzzy_core.item_util.FlavorHelper
import me.fzzyhmstrs.fzzy_core.modifier_util.ModifierHelperType
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ArmorItem
import net.minecraft.item.ArmorMaterial
import net.minecraft.item.ItemStack
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.world.World

class SoulwovenArmorItem(material: ArmorMaterial, type: Type, settings: Settings) : ArmorItem(material, type, settings), Modifiable {

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
        FlavorHelper.addFlavorText(tooltip, context, flavorText, flavorTextDesc)
    }

    override fun defaultModifiers(type: ModifierHelperType<*>?): MutableList<Identifier> {
        if (type == ModifierHelper.getType())
            return mutableListOf(RegisterModifier.ensouledModifiers.random().modifierId)
        return super.defaultModifiers(type)
    }

    companion object{
        init{
            /*ModifyModifiersEvent.EVENT.register{ _, user, scepterStack, modifiers ->
                val activeEnchant = Identifier(scepterStack.nbt?.getString(NbtKeys.ACTIVE_ENCHANT.str()) ?: "blank_spell")
                var mods = modifiers
                for (stack in user.armorItems) {
                    if (stack.item is SoulwovenArmorItem) {
                        ModifierHelper.gatherActiveModifiers(stack, activeEnchant)
                        val focusMods = ModifierHelper.getActiveModifiers(stack)
                        mods = mods.combineWith(focusMods, AugmentModifier())
                    }
                }
                mods
            }*/
        }
    }
}