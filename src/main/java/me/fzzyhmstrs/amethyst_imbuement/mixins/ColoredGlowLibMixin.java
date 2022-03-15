package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

@Mixin(ColoredGlowLib.class)
public class ColoredGlowLibMixin {

    @Shadow
    static
    private List<UUID> entity_rainbow_list;

    @Inject(method = "getEntityRainbowColor", at = @At(value = "HEAD"), cancellable = true)
    private static void fixListCheck(Entity entity, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(entity_rainbow_list.contains(entity.getUuid()));
    }

}
