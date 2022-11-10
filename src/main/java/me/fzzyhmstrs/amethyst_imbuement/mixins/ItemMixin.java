package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.item.AiItemSettings;
import me.fzzyhmstrs.amethyst_imbuement.item.ItemBookCategory;
import net.fabricmc.fabric.impl.item.FabricItemInternals;
import net.minecraft.item.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin implements ItemBookCategory {

    @Unique private AiItemSettings.AiItemGroup aiItemGroup;

    @Nullable
    @Override
    public AiItemSettings.AiItemGroup amethyst_imbuement_getAiItemGroup() {
        return aiItemGroup;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void amethyst_imbuement_onConstructAiSettings(Item.Settings settings, CallbackInfo info) {
        if (settings instanceof AiItemSettings){
            aiItemGroup = ((AiItemSettings) settings).getAiGroup();
        }
    }

    @Inject(method = "getEnchantability", at = @At(value = "HEAD"), cancellable = true)
    private void amethyst_imbuement_checkForShield(CallbackInfoReturnable<Integer> cir){
        Object check = this;
        if (check instanceof ShieldItem){
            cir.setReturnValue(1);
        }
        if (check instanceof ElytraItem){
            cir.setReturnValue(1);
        }
        if (check instanceof FlintAndSteelItem){
            cir.setReturnValue(1);
        }
        if (check instanceof ShearsItem){
            cir.setReturnValue(1);
        }
        if (check instanceof BucketItem){
            cir.setReturnValue(1);
        }
        if (check instanceof HorseArmorItem){
            cir.setReturnValue(1);
        }
        if (check instanceof MilkBucketItem){
            cir.setReturnValue(1);
        }
        if (check instanceof OnAStickItem){
            cir.setReturnValue(1);
        }
        if (check instanceof EntityBucketItem){
            cir.setReturnValue(1);
        }
        if (check instanceof PowderSnowBucketItem){
            cir.setReturnValue(1);
        }
    }

    @Inject(method = "isEnchantable", at = @At(value = "HEAD"), cancellable = true)
    private void amethyst_imbuement_bucketIsEnchantable(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        if ((Object)this instanceof HorseArmorItem){
            cir.setReturnValue(true);
        }
    }
}
