package me.fzzyhmstrs.amethyst_imbuement.mixins;

import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BrewingRecipeRegistry.class)
public interface BrewingRecipeRegistryInvoker {

    @Invoker(value = "registerPotionRecipe")
    public static void amethyst_imbuement_registerPotionRecipe(Potion input, Item item, Potion output){
        throw new AssertionError();
    };

}
