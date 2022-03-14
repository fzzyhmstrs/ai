package me.fzzyhmstrs.amethyst_imbuement.mixins;

import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {

    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "net/minecraft/screen/Property.get ()I"))
    private int cheeseAnvilLevels(Property instance){
        return 0;
    }

}
