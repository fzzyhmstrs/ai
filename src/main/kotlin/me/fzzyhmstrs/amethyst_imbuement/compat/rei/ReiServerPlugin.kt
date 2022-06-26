@file:Suppress("LocalVariableName")

package me.fzzyhmstrs.amethyst_imbuement.compat.rei

import me.fzzyhmstrs.amethyst_imbuement.screen.CrystalAltarScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreenHandler
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry
import me.shedaniel.rei.api.common.plugins.REIServerPlugin
import me.shedaniel.rei.api.common.transfer.info.MenuInfoRegistry
import me.shedaniel.rei.api.common.transfer.info.simple.SimpleMenuInfoProvider

object ReiServerPlugin: REIServerPlugin {

    override fun registerMenuInfo(registry: MenuInfoRegistry) {
        registry.register(ReiCategoryIds.IMBUING_TABLE_CATEGORY_ID,ImbuingTableScreenHandler::class.java,
            SimpleMenuInfoProvider.of { display: ImbuingTableDisplay -> ImbuingTableMenuInfo(display) }
        )
        registry.register(ReiCategoryIds.CRYSTAL_ALTAR_CATEGORY_ID,CrystalAltarScreenHandler::class.java,
            SimpleMenuInfoProvider.of { display: CrystalAltarDisplay -> CrystalAltarMenuInfo(display) }
        )
    }

    override fun registerDisplaySerializer(registry: DisplaySerializerRegistry) {
        registry.register(ReiCategoryIds.IMBUING_TABLE_CATEGORY_ID,ImbuingTableDisplay.serializer())
        registry.register(ReiCategoryIds.CRYSTAL_ALTAR_CATEGORY_ID,CrystalAltarDisplay.serializer())
    }

}