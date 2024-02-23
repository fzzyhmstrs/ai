package me.fzzyhmstrs.amethyst_imbuement.compat.patchouli

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.amethyst_core.registry.RegisterTag
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.resource.language.I18n
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.world.World
import vazkii.patchouli.api.IVariable
import vazkii.patchouli.client.book.BookContentsBuilder
import vazkii.patchouli.client.book.BookEntry
import vazkii.patchouli.client.book.gui.GuiBook
import vazkii.patchouli.client.book.gui.GuiBookEntry
import vazkii.patchouli.client.book.page.abstr.PageWithText
import kotlin.math.abs

class SpellTextPage: PageWithText() {

    private var spell: Identifier? = null

    @Transient
    private var spellEnchant: ScepterAugment? = null
    @Transient
    private var spellTex: Identifier = AI.identity("textures/spell/missing_no.png")
    @Transient
    private var spellText = ""

    override fun getTextHeight(): Int {
        return 46
    }

    override fun build(level: World?, entry: BookEntry?, builder: BookContentsBuilder?, pageNum: Int) {
        super.build(level, entry, builder, pageNum)
        spellEnchant = FzzyPort.ENCHANTMENT.get(spell) as? ScepterAugment
    }

    override fun onDisplayed(parent: GuiBookEntry?, left: Int, top: Int) {
        if (spellEnchant == null) return
        if (spellTex == AI.identity("textures/spell/missing_no.png"))
            spellEnchant?.let {
                val textureIdTemp = Identifier(it.id.namespace,"textures/spell/${it.id.path}.png")
                spellTex = if (MinecraftClient.getInstance().resourceManager.getResource(textureIdTemp).isPresent) textureIdTemp else AI.identity("textures/spell/missing_no.png")
            }
        if (spellText.isEmpty()){
            spellEnchant?.let {
                val desc = AcText.translatable(it.translationKey + ".desc").string
                val type = AugmentHelper.getAugmentType(it)
                val typeString = PatchouliCompat.spellTypeColorCodes[type.ordinal] + PatchouliCompat.spellTypeDecorators[type.ordinal] + type.str().replaceFirstChar { chr -> chr.uppercaseChar() } + PatchouliCompat.spellTypeColorClears[type.ordinal]
                val lvl = AugmentHelper.getAugmentMinLvl(it)
                val minLevel = AcText.translatable("patchouli.min_level_text",typeString, lvl).string
                val cooldown = AugmentHelper.getAugmentCooldown(it)
                val cooldownBase = cooldown.base() / 20f
                val cooldownPerLvl = cooldown.perLevel() / 20f
                val cooldownKey = if(cooldownPerLvl < 0){
                    "lore_book.cooldown.minus"
                } else if (cooldownPerLvl == 0f){
                    "lore_book.cooldown.basic"
                } else {
                    "lore_book.cooldown.plus"
                }
                val cd = AcText.translatable(cooldownKey,cooldownBase.toString(), abs(cooldownPerLvl).toString()).string
                val manaCost = AcText.translatable("lore_book.mana_cost",AugmentHelper.getAugmentManaCost(it)).string
                val tier = AcText.translatable("lore_book.tier",it.getTier()).string
                val tagStyles = RegisterTag.getStylesFromSpell(it)
                val tagStylesMaxIndex = tagStyles.size - 1
                var tagStyleNameString = ""
                tagStyles.forEachIndexed { index, tagStyle ->
                    tagStyleNameString += "$(${(tagStyle.color.takeIf { clr -> clr != Formatting.YELLOW }?:Formatting.GOLD).code})${tagStyle.name.lowercase().replaceFirstChar { chr -> chr.uppercaseChar()}}$()"
                    if (index < tagStylesMaxIndex)
                        tagStyleNameString += ", "
                }
                val style = AcText.translatable("patchouli.style_text",tagStyleNameString).string
                val stitchedText = "$desc$(br2)$minLevel$(br)$cd$(br)$manaCost$(br)$tier$(br)$style"
                spellText = stitchedText
            }
            this.text = IVariable.wrap(spellText)
        }
        super.onDisplayed(parent, left, top)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        if (pageNum == 0) {
            var renderedSmol = false
            var smolText = ""
            if (mc.options.advancedItemTooltips) {
                val res = parent.entry.id
                smolText = res.toString()
            } else if (entry.addedBy != null) {
                smolText = I18n.translate("patchouli.gui.lexicon.added_by", entry.addedBy)
            }
            //smol added by info text
            if (!smolText.isEmpty()) {
                context.matrices.scale(0.5f, 0.5f, 1f)
                parent.drawCenteredStringNoShadow(context, smolText, GuiBook.PAGE_WIDTH, 12, book.headerColor)
                context.matrices.scale(2f, 2f, 1f)
                renderedSmol = true
            }
            //title
            parent.drawCenteredStringNoShadow(
                context,
                parent.entry.name.asOrderedText(),
                GuiBook.PAGE_WIDTH / 2,
                if (renderedSmol) -3 else 0,
                book.headerColor
            )
            //divider
            RenderSystem.enableBlend()
            context.drawTexture(PatchouliCompat.SPELL_TEXT_DIVIDER,3,12,0f,0f,110,27,128,32)
            //spell texture
            RenderSystem.enableBlend()
            context.drawTexture(spellTex,43,10,0f,0f,32,32,32,32)
            //GuiBook.drawSeparator(context, book, 0, 12)
        }
    }


}