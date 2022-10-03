package me.fzzyhmstrs.amethyst_imbuement.screen.widget

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuedFamiliarInventoryScreen
import net.minecraft.client.gui.widget.ToggleButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.passive.CatVariant
import java.util.function.Consumer

class CatHeadWidget(x:Int, y: Int, toggled: Boolean, private val variant: CatVariant,private val index: Int, private val listener: Consumer<Pair<Int,CatVariant>>): ToggleButtonWidget(x,y,7,11,toggled) {

    init{
        setTextureUV(176,0,7,11, ImbuedFamiliarInventoryScreen.texture)
    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        super.onClick(mouseX, mouseY)
        listener.accept(Pair(index,variant))
    }

    override fun renderButton(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.renderButton(matrices, mouseX, mouseY, delta)
        RenderSystem.setShaderTexture(0,variant.texture)
        drawTexture(matrices, this.x+1, this.y+1, 5f, 4f, 5, 5,64,32)
    }


}