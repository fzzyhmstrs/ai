package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.model.GlisteringTridentEntityModel;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltinModelItemRenderer.class)
public abstract class GlisteringTridentModelMixin {
    @Shadow @Final private EntityModelLoader entityModelLoader;

    private GlisteringTridentEntityModel modelGlisteringTrident; // = new GlisteringTridentEntityModel(entityModelLoader.getModelPart(RegisterRenderer.INSTANCE.getGLISTERING_TRIDENT()));

    @Inject(method = "reload", at = @At(value = "TAIL"))
    private void reloadMixin(ResourceManager manager, CallbackInfo ci) {
        this.modelGlisteringTrident = new GlisteringTridentEntityModel(entityModelLoader.getModelPart(RegisterRenderer.INSTANCE.getGLISTERING_TRIDENT()));
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    private void renderMixin(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        if (stack.isOf(RegisterItem.INSTANCE.getGLISTERING_TRIDENT())) {
            matrices.push();
            matrices.scale(1.0f, -1.0f, -1.0f);
            VertexConsumer block = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, modelGlisteringTrident.getLayer(GlisteringTridentEntityModel.Companion.getTEXTURE()), false, stack.hasGlint());
            modelGlisteringTrident.render(matrices, block, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);
            matrices.pop();
        }
    }
}
