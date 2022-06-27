package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.item_util.CustomFlavorItem
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.util.Identifier

class CustomAiFlavorItem(settings: Settings, flavor: String, glint: Boolean, nameSpace: String = AI.MOD_ID) : CustomFlavorItem(settings, glint, Identifier(nameSpace, flavor)) {
    //passing the AI namespace
}