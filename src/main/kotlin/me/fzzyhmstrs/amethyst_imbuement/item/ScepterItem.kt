package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.item_util.DefaultScepterItem
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterToolMaterial
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.util.Identifier


open class ScepterItem(material: ScepterToolMaterial, settings: Settings): DefaultScepterItem(material, settings) {
    override val fallbackId: Identifier = Identifier(AI.MOD_ID, "magic_missile")
}
