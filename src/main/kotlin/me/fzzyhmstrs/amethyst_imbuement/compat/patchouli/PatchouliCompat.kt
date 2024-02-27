package me.fzzyhmstrs.amethyst_imbuement.compat.patchouli

import com.google.common.collect.ArrayListMultimap
import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.toast.Toast
import net.minecraft.client.toast.ToastManager
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import vazkii.patchouli.api.PatchouliAPI
import vazkii.patchouli.client.book.BookIcon
import vazkii.patchouli.client.book.ClientBookRegistry
import vazkii.patchouli.client.book.gui.GuiBook
import vazkii.patchouli.client.book.gui.GuiBookEntry
import vazkii.patchouli.common.book.Book
import kotlin.math.max

object PatchouliCompat {

    val CHECKLIST_BOX = AI.identity("textures/gui/patchouli/patchouli_checklist_box.png")

    val ARMOR_STAND_BACKGROUND = AI.identity("textures/gui/patchouli/patchouli_armor_stand_background.png")

    val SPELL_TEXT_DIVIDER = AI.identity("textures/gui/patchouli/patchouli_spell_divider.png")

    val ENHANCING_BACKGROUND = AI.identity("textures/gui/patchouli/patchouli_enhancing_background.png")

    val IMBUING_BACKGROUND = AI.identity("textures/gui/patchouli/patchouli_imbuing_background.png")
    val IMBUING_BACKGROUND_AUGMENT = AI.identity("textures/gui/patchouli/patchouli_imbuing_background_augment.png")



    val xOffsets = intArrayOf(8, 92, 31, 50, 69, 31, 50, 69, 31, 50, 69, 8, 92)
    val yOffsets = intArrayOf(17, 17, 40, 40, 40, 59, 59, 59, 78, 78, 78, 101, 101)

    val spellTypeDecorators = arrayOf("□ ", "△ ", "◇ ", "")
    val spellTypeColorCodes = arrayOf("$(c)", "$(2)", "$(9)", "")
    val spellTypeColorClears = arrayOf("$()", "$()", "$()", "")

    val defaultUnknownUnlockText = "You need to progress to something before you can read this entry!"
    val defaultEnhancingUnlockText = "Unlock this recipe by progressing to the Crystal Altar!"
    val defaultImbuingUnlockText = "Unlock this recipe by progressing to the Imbuing Table!"

    private val bookId = AI.identity("glistering_tome")
    private val wasUpdated: ArrayListMultimap<Book,BookIcon> = ArrayListMultimap.create()

    fun markUpdated(book: Book, icon: BookIcon){
        wasUpdated.put(book,icon)
    }

    private fun popUpdated(book: Book): List<BookIcon> {
        return wasUpdated.removeAll(book)
    }

    fun addPageToast(book: Book){
        val updates = popUpdated(book).toMutableList()
        if (updates.isEmpty()) return
        val toast = MinecraftClient.getInstance().toastManager.getToast(LexiconPageToast::class.java, book)
        if (toast == null) {
            MinecraftClient.getInstance().toastManager.add(LexiconPageToast(book, updates))
        } else {
            toast.addIcons(updates)
        }
    }

    fun openBookEntry(entry: Identifier, page: Int){
        PatchouliAPI.get().openBookEntry(bookId, entry, page)
    }

    fun openBookGui(playerEntity: ServerPlayerEntity, identifier: Identifier){
        PatchouliAPI.get().openBookGUI(playerEntity,identifier)
    }

    fun registerPage() {
        ClientBookRegistry.INSTANCE.pageTypes[AI.identity("imbuing")] = ImbuingRecipePage::class.java
        ClientBookRegistry.INSTANCE.pageTypes[AI.identity("enhancing")] = EnhancingRecipePage::class.java
        ClientBookRegistry.INSTANCE.pageTypes[AI.identity("enhancing_multi")] = EnhancingRecipeMultiPage::class.java
        ClientBookRegistry.INSTANCE.pageTypes[AI.identity("crafting")] = CraftingLockablePage::class.java
        ClientBookRegistry.INSTANCE.pageTypes[AI.identity("spell_text")] = SpellTextPage::class.java
        ClientBookRegistry.INSTANCE.pageTypes[AI.identity("hidden_text")] = HiddenTextPage::class.java
        ClientBookRegistry.INSTANCE.pageTypes[AI.identity("armor_stand")] = ArmorStandPage::class.java
        ClientBookRegistry.INSTANCE.pageTypes[AI.identity("progression")] = ProgressionPage::class.java
    }

    private fun renderHint(context: DrawContext, mouseX: Int, mouseY: Int, hintIngredient: Ingredient?, unlockText: List<OrderedText>, mc: MinecraftClient, book: Book, parent: GuiBookEntry): Int{
        val w = 66
        val h = 26
        val y = if (hintIngredient != null) {
            RenderSystem.enableBlend()
            context.drawTexture(
                book.craftingTexture,
                GuiBook.PAGE_WIDTH / 2 - w / 2,
                GuiBook.PAGE_HEIGHT / 2 - 58,
                0f,
                (128 - h).toFloat(),
                w,
                h,
                128,
                256
            )
            RenderSystem.enableBlend()
            context.drawTexture(
                IMBUING_BACKGROUND,
                GuiBook.PAGE_WIDTH / 2 - 8,
                GuiBook.PAGE_HEIGHT / 2 - 34,
                45f,
                83f,
                16,
                16,
                128,
                128
            )
            parent.renderIngredient(
                context,
                GuiBook.PAGE_WIDTH / 2 - 8,
                GuiBook.PAGE_HEIGHT / 2 -53,
                mouseX,
                mouseY,
                hintIngredient
            )
            20
        } else {
            40
        }
        RenderSystem.enableBlend()
        context.drawTexture(
            book.craftingTexture,
            GuiBook.PAGE_WIDTH / 2 - w / 2,
            GuiBook.PAGE_HEIGHT / 2 - y,
            0f,
            (128 - h).toFloat(),
            w,
            h,
            128,
            256
        )
        var textY = GuiBook.PAGE_HEIGHT / 2 + (36 - y)
        for (text in unlockText) {
            context.drawText(mc.textRenderer,text,(GuiBook.PAGE_WIDTH / 2) - (mc.textRenderer.getWidth(text) / 2), textY, book.headerColor, false)
            textY += (GuiBook.TEXT_LINE_HEIGHT + 1)
        }
        return y
    }

    fun renderHint(context: DrawContext, mouseX: Int, mouseY: Int, hintIngredient: Ingredient?, output: ItemStack?, unlockText: List<OrderedText>, mc: MinecraftClient, book: Book, parent: GuiBookEntry){
        val y = renderHint(context, mouseX, mouseY, hintIngredient, unlockText, mc, book, parent)
        parent.renderItemStack(context, GuiBook.PAGE_WIDTH / 2 - 8, GuiBook.PAGE_HEIGHT / 2 - (y - 5), mouseX, mouseY, output)
    }

    fun renderHint(context: DrawContext, mouseX: Int, mouseY: Int, hintIngredient: Ingredient?, output: Ingredient?, unlockText: List<OrderedText>, mc: MinecraftClient, book: Book, parent: GuiBookEntry){
        val y = renderHint(context, mouseX, mouseY, hintIngredient, unlockText, mc, book, parent)
        parent.renderIngredient(context, GuiBook.PAGE_WIDTH / 2 - 8, GuiBook.PAGE_HEIGHT / 2 - (y - 5), mouseX, mouseY, output)
    }


    class LexiconPageToast(private val book: Book, private var icons: MutableList<BookIcon>) : Toast {

        private var startTime: Long = 0
        private var justUpdated = true

        override fun getType(): Book {
            return book
        }

        fun addIcons(newIcons: List<BookIcon>){
            icons.addAll(newIcons)
            justUpdated = true
        }

        override fun draw(context: DrawContext, toastGui: ToastManager, delta: Long): Toast.Visibility {
            if (this.justUpdated) {
                this.startTime = delta
                this.justUpdated = false
            }
            RenderSystem.setShaderTexture(0, Toast.TEXTURE)
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            context.drawTexture(Toast.TEXTURE, 0, 0, 0, 32, 160, 32)
            val font = toastGui.client.textRenderer
            context.drawText(font, Text.translatable(book.name), 30, 7, -11534256, false)
            context.drawText(font, Text.translatable("patchouli.page_toast_info"), 30, 17, -16777216, false)
            val bookIndex = (delta.toDouble() / max(1.0, 5000.0  / this.icons.size.toDouble()) % this.icons.size.toDouble()).toInt()
            icons[bookIndex].render(context, 8, 8)
            //graphics.drawItem(book.bookItem, 8, 8)
            context.drawItemInSlot(font, book.bookItem, 8, 8)
            return if ((delta - startTime) >= 5000L) Toast.Visibility.HIDE else Toast.Visibility.SHOW
        }
    }


}