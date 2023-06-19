package me.fzzyhmstrs.amethyst_imbuement.mixins;


import com.mojang.blaze3d.systems.RenderSystem;
import me.fzzyhmstrs.amethyst_imbuement.item.SniperBowItem;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow @Final
    private MinecraftClient client;
    @Shadow @Final private static Identifier SPYGLASS_SCOPE;

    //altering my method to inject just after the vanilla setTextureShader, so I can hopefully override any other spyglass changes without needing a redirect
    @Inject(method = "renderSpyglassOverlay", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/hud/InGameHud.drawTexture (Lnet/minecraft/client/util/math/MatrixStack;IIIFFIIII)V"))
    private void amethyst_imbuement_setSniperBowTextureAfterSpyglassImprovements(MatrixStack matrices, float scale, CallbackInfo ci){
        if (this.client.player != null){
            if (this.client.player.getActiveItem().getItem() == RegisterItem.INSTANCE.getSNIPER_BOW()) {
                RenderSystem.setShaderTexture(0, SniperBowItem.Companion.getSNIPER_BOW_SCOPE());
            }
        }
    }
}
