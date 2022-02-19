package me.fzzyhmstrs.amethyst_imbuement.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import me.fzzyhmstrs.amethyst_imbuement.AI;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class HandledScreenMixin<T extends ScreenHandler> extends Screen {

    @Shadow protected int x;
    @Shadow protected int y;
    @Shadow protected int backgroundHeight;
    @Shadow protected int backgroundWidth;
    protected T handler;

    protected HandledScreenMixin(Text title) {
        super(title);
    }
   //@Inject(method="<init>",at = @At(value = "TAIL"))
    //private void HandledScreen(ScreenHandler handler, PlayerInventory inventory, Text title, CallbackInfo ci){
       // hndlr = (T) handler;
    //}

    @Inject(method="init",at = @At(value="TAIL"))
    private void initMixin(CallbackInfo ci){
        if (handler.getType() == RegisterHandler.INSTANCE.getIMBUING_SCREEN_HANDLER()){
            this.x = (this.width - 234) / 2;
            this.y = (this.height - 174) / 2;
            this.backgroundWidth = 234;
            this.backgroundHeight = 174;

        }
    }

    @Inject(method="isClickOutsideBounds", at=@At(value="HEAD"), cancellable = true)
    private void isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> cir){
        if (handler.getType() == RegisterHandler.INSTANCE.getIMBUING_SCREEN_HANDLER()){
            cir.setReturnValue(mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight));
            //System.out.println(this.backgroundWidth);
            //System.out.println(this.backgroundHeight);
        }
    }
    /*
    @Inject(method="drawSlot",at = @At("HEAD"), cancellable = true)
    private void drawSlot(MatrixStack matrices, Slot slot, CallbackInfo ci) {
        if (!(handler.getType() == AI.INSTANCE.getIMBUING_SCREEN_HANDLER())){return;}
        Pair<Identifier, Identifier> k;
        int i = slot.x;
        int j = slot.y;
        ItemStack itemStack = slot.getStack();
        boolean bl = false;
        boolean bl2 = slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && !this.touchIsRightClickDrag;
        ItemStack itemStack2 = ((ScreenHandler)this.handler).getCursorStack();
        String string = null;
        if (slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && this.touchIsRightClickDrag && !itemStack.isEmpty()) {
            itemStack = itemStack.copy();
            itemStack.setCount(itemStack.getCount() / 2);
        } else if (this.cursorDragging && this.cursorDragSlots.contains(slot) && !itemStack2.isEmpty()) {
            if (this.cursorDragSlots.size() == 1) {
                return;
            }
            if (ScreenHandler.canInsertItemIntoSlot(slot, itemStack2, true) && ((ScreenHandler)this.handler).canInsertIntoSlot(slot)) {
                itemStack = itemStack2.copy();
                bl = true;
                ScreenHandler.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemStack, slot.getStack().isEmpty() ? 0 : slot.getStack().getCount());
                int k2 = Math.min(itemStack.getMaxCount(), slot.getMaxItemCount(itemStack));
                if (itemStack.getCount() > k2) {
                    string = Formatting.YELLOW.toString() + k2;
                    itemStack.setCount(k2);
                }
            } else {
                this.cursorDragSlots.remove(slot);
                this.calculateOffset();
            }
        }
        this.setZOffset(100);
        this.itemRenderer.zOffset = 100.0f;
        if (itemStack.isEmpty() && slot.isEnabled() && (k = slot.getBackgroundSprite()) != null) {
            Sprite sprite = this.client.getSpriteAtlas(k.getFirst()).apply(k.getSecond());
            RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
            HandledScreen.drawSprite(matrices, i, j, this.getZOffset(), 16, 16, sprite);
            bl2 = true;
        }
        if (!bl2) {
            if (bl) {
                HandledScreen.fill(matrices, i, j, i + 16, j + 16, -2130706433);
            }
            RenderSystem.enableDepthTest();
            this.itemRenderer.renderInGuiWithOverrides(this.client.player, itemStack, i, j, slot.x + slot.y * this.backgroundWidth);
            this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, i, j, string);
        }
        this.itemRenderer.zOffset = 0.0f;
        this.setZOffset(0);
    }
    */

}
