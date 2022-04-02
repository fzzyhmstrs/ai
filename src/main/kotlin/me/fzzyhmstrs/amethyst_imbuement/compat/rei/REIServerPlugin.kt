@file:Suppress("LocalVariableName")

package me.fzzyhmstrs.amethyst_imbuement.compat.rei

import me.fzzyhmstrs.amethyst_imbuement.screen.CrystalAltarScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreenHandler
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry
import me.shedaniel.rei.api.common.plugins.REIServerPlugin
import me.shedaniel.rei.api.common.transfer.info.MenuInfoRegistry
import me.shedaniel.rei.api.common.transfer.info.simple.SimpleMenuInfoProvider

object REIServerPlugin: REIServerPlugin {

    private val IMBUING_TABLE_CATEGORY = ImbuingTableCategory()
    private val CRYSTAL_ALTAR_CATEGORY = CrystalAltarCategory()


    override fun registerMenuInfo(registry: MenuInfoRegistry) {
        registry.register(IMBUING_TABLE_CATEGORY.categoryIdentifier,ImbuingTableScreenHandler::class.java,
            SimpleMenuInfoProvider.of { display: ImbuingTableDisplay -> ImbuingTableMenuInfo(display) }
        )
        registry.register(CRYSTAL_ALTAR_CATEGORY.categoryIdentifier,CrystalAltarScreenHandler::class.java,
            SimpleMenuInfoProvider.of { display: CrystalAltarDisplay -> CrystalAltarMenuInfo(display) }
        )
    }

    override fun registerDisplaySerializer(registry: DisplaySerializerRegistry) {
        registry.register(IMBUING_TABLE_CATEGORY.categoryIdentifier,ImbuingTableDisplay.serializer())
        registry.register(CRYSTAL_ALTAR_CATEGORY.categoryIdentifier,CrystalAltarDisplay.serializer())
    }

}