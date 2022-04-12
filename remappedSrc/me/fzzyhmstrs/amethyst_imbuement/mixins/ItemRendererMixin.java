package me.fzzyhmstrs.amethyst_imbuement.mixins;


import me.fzzyhmstrs.amethyst_imbuement.AI;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow @Final
    private ItemModels models;

    private boolean glister;
    private boolean bl;
    private ItemStack stackChk;


    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At(value = "HEAD"))
    private void renderItemInitBl(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        bl = renderMode == ModelTransformation.Mode.GUI || renderMode == ModelTransformation.Mode.GROUND || renderMode == ModelTransformation.Mode.FIXED;
        stackChk = stack;
    }

    @Redirect(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.isOf (Lnet/minecraft/item/Item;)Z"))
    private boolean betterTridentCheck(ItemStack instance, Item item){
        glister = instance.isOf(RegisterItem.INSTANCE.getGLISTERING_TRIDENT());
        return instance.isOf(Items.TRIDENT) || instance.isOf(RegisterItem.INSTANCE.getGLISTERING_TRIDENT());
    }

    @Redirect(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "INVOKE", target = "net/minecraft/client/render/model/BakedModelManager.getModel (Lnet/minecraft/client/util/ModelIdentifier;)Lnet/minecraft/client/render/model/BakedModel;"))
    private BakedModel betterTridentModelLoader(BakedModelManager instance, ModelIdentifier id){
        if (glister){
            return this.models.getModelManager().getModel(new ModelIdentifier(AI.MOD_ID + ":glistering_trident#inventory"));
        } else {
            return this.models.getModelManager().getModel(new ModelIdentifier("minecraft:trident#inventory"));
        }
    }

    @Redirect(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "INVOKE", target = "net/minecraft/client/render/model/BakedModel.isBuiltin ()Z"))
    private boolean betterTridentCheck2(BakedModel instance){
        return (instance.isBuiltin() || (stackChk.isOf(RegisterItem.INSTANCE.getGLISTERING_TRIDENT()) && !bl));
    }


    @Inject(method = "getModel", at = @At(value = "HEAD"), cancellable = true)
    private void getModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        if(stack.getItem() == RegisterItem.INSTANCE.getGLISTERING_TRIDENT()){
            BakedModel bakedModel = this.models.getModelManager().getModel(new ModelIdentifier(AI.MOD_ID + ":glistering_trident_in_hand#inventory"));
            ClientWorld clientWorld = world instanceof ClientWorld ? (ClientWorld)world : null;
            BakedModel bakedModel2 = bakedModel.getOverrides().apply(bakedModel, stack, clientWorld, entity, seed);
            if(bakedModel2 == null) {
                cir.setReturnValue(this.models.getModelManager().getMissingModel());
            }else{
                cir.setReturnValue(bakedModel2);
            }
        }
    }


}
