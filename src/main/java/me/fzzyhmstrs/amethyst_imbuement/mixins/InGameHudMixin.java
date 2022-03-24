package me.fzzyhmstrs.amethyst_imbuement.mixins;


import com.mojang.blaze3d.systems.RenderSystem;
import me.fzzyhmstrs.amethyst_imbuement.item.SniperBowItem;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow @Final
    private MinecraftClient client;
    @Shadow @Final private static Identifier SPYGLASS_SCOPE;


    @Redirect(method = "renderSpyglassOverlay", at = @At(value = "INVOKE", target = "com/mojang/blaze3d/systems/RenderSystem.setShaderTexture (ILnet/minecraft/util/Identifier;)V"))
    private void setShaderTextureMixin(int i, Identifier identifier){
        assert this.client.player != null;
        if (this.client.player.getActiveItem().getItem() != RegisterItem.INSTANCE.getSNIPER_BOW()){
            RenderSystem.setShaderTexture(0, SPYGLASS_SCOPE);
        } else {
            RenderSystem.setShaderTexture(0, SniperBowItem.Companion.getSNIPER_BOW_SCOPE());
        }
    }
}
