package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.item_util.CustomFlavorItem
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.world.World

class CustomAiFlavorItem(settings: Settings, flavor: String, glint: Boolean, nameSpace: String = AI.MOD_ID) : CustomFlavorItem(settings, flavor, glint, nameSpace) {
    //passing the AI namespace
}