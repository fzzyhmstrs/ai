package me.fzzyhmstrs.amethyst_imbuement.mixins;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fzzyhmstrs.amethyst_imbuement.item.weapon.SniperBowItem;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTag;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow @Final
    private MinecraftClient client;

    @WrapOperation(method = "renderSpyglassOverlay", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/DrawContext.drawTexture (Lnet/minecraft/util/Identifier;IIIFFIIII)V"))
    private void amethyst_imbuement_setSniperBowTextureAfterSpyglass(DrawContext instance, Identifier texture, int x, int y, int z, float u, float v, int width, int height, int textureWidth, int textureHeight, Operation<Void> operation){
        if (this.client.player != null){
            if (this.client.player.getActiveItem().isIn(RegisterTag.INSTANCE.getCROSSBOWS_TAG())) {
                instance.drawTexture(SniperBowItem.Companion.getSNIPER_BOW_SCOPE(),x,y,z,u,v,width,height,textureWidth,textureHeight);
            } else {
                operation.call(instance,texture,x,y,z,u,v,width,height,textureWidth,textureHeight);
            }
        } else {
            operation.call(instance,texture,x,y,z,u,v,width,height,textureWidth,textureHeight);
        }
    }
}
