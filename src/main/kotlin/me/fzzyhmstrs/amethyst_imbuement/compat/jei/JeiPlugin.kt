package me.fzzyhmstrs.amethyst_imbuement.compat.jei

import me.fzzyhmstrs.amethyst_imbuement.AI
import mezz.jei.api.IModPlugin
import mezz.jei.api.registration.IRecipeCategoryRegistration
import net.minecraft.util.Identifier

object JeiPlugin: IModPlugin {

    override fun getPluginUid(): Identifier {
        return Identifier(AI.MOD_ID,"jei_plugin")
    }

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        registration.addRecipeCategories()
    }

}