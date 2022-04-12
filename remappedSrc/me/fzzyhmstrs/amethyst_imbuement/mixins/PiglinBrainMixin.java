package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {

    @Inject(method = "wearsGoldArmor", at = @At(value = "HEAD"), cancellable = true)
    private static void wearsGoldArmor(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (RegisterEnchantment.INSTANCE.getFRIENDLY().specialEffect(entity,1,ItemStack.EMPTY)){
            cir.setReturnValue(true);
        }
    }
}
