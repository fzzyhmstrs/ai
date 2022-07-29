package me.fzzyhmstrs.amethyst_imbuement.compat.emi

import com.mojang.blaze3d.systems.RenderSystem
import dev.emi.emi.api.render.EmiRenderable
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class AiSimplifiedRenderer(val u: Int, val v: Int): EmiRenderable {
    private val SPRITE_SHEET = Identifier(AI.MOD_ID,"textures/gui/emi_recipe_textures.png")

    override fun render(matrices: MatrixStack?, x: Int, y: Int, delta: Float) {
        RenderSystem.setShaderTexture(0, SPRITE_SHEET)
        DrawableHelper.drawTexture(matrices, x, y, u.toFloat(), v.toFloat(), 16, 16, 32, 16)
    }
}