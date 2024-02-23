package me.fzzyhmstrs.amethyst_imbuement.compat.patchouli

import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import vazkii.patchouli.api.PatchouliAPI
import vazkii.patchouli.client.book.ClientBookRegistry

object PatchouliCompat {

    val SPELL_TEXT_DIVIDER = AI.identity("textures/gui/patchouli/patchouli_spell_divider.png")

    val ENHANCING_BACKGROUND = AI.identity("textures/gui/patchouli/patchouli_enhancing_background.png")

    val IMBUING_BACKGROUND = AI.identity("textures/gui/patchouli/patchouli_imbuing_background.png")
    val IMBUING_BACKGROUND_AUGMENT = AI.identity("textures/gui/patchouli/patchouli_imbuing_background_augment.png")

    val xOffsets = intArrayOf(8, 92, 31, 50, 69, 31, 50, 69, 31, 50, 69, 8, 92)
    val yOffsets = intArrayOf(17, 17, 40, 40, 40, 59, 59, 59, 78, 78, 78, 101, 101)

    val spellTypeDecorators = arrayOf("□ ", "△ ", "◇ ", "")
    val spellTypeColorCodes = arrayOf("$(c)", "$(2)", "$(9)", "")
    val spellTypeColorClears = arrayOf("$()", "$()", "$()", "")

    fun openBookGui(playerEntity: ServerPlayerEntity, identifier: Identifier){
        PatchouliAPI.get().openBookGUI(playerEntity,identifier)
    }

    fun registerPage() {
        ClientBookRegistry.INSTANCE.pageTypes[AI.identity("imbuing")] = ImbuingRecipePage::class.java
        ClientBookRegistry.INSTANCE.pageTypes[AI.identity("enhancing")] = EnhancingRecipePage::class.java
        ClientBookRegistry.INSTANCE.pageTypes[AI.identity("enhancing_multi")] = EnhancingRecipeMultiPage::class.java
        ClientBookRegistry.INSTANCE.pageTypes[AI.identity("spell_text")] = SpellTextPage::class.java
        ClientBookRegistry.INSTANCE.pageTypes[AI.identity("hidden_text")] = HiddenTextPage::class.java
    }

}