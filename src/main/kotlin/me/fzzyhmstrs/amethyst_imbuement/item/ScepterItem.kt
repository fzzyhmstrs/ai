package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.item_util.AbstractAiScepterItem
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterToolMaterial
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.util.*

open class ScepterItem(material: ScepterToolMaterial, settings: Settings, vararg defaultModifier: Identifier): AbstractAiScepterItem(material, settings, *defaultModifier) {
    override val fallbackId: Identifier = Identifier(AI.MOD_ID, "magic_missile")
}
