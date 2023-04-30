package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.mixins.BrewingRecipeRegistryInvoker
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.item.Items
import net.minecraft.potion.Potion
import net.minecraft.potion.Potions
import net.minecraft.recipe.BrewingRecipeRegistry
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object RegisterPotion {

    val CURSE_POTION = Potion(StatusEffectInstance(RegisterStatus.CURSED,900,1))
    val STRONG_CURSE_POTION = Potion("curse",StatusEffectInstance(RegisterStatus.CURSED,450,3))
    val LONG_CURSE_POTION = Potion("curse",StatusEffectInstance(RegisterStatus.CURSED,2400,1))
    val IMMUNITY_POTION = Potion(StatusEffectInstance(RegisterStatus.IMMUNITY,600,0))
    val LONG_IMMUNITY_POTION = Potion("immunity",StatusEffectInstance(RegisterStatus.IMMUNITY,1200,0))
    val INSIGHT_POTION = Potion(StatusEffectInstance(RegisterStatus.INSIGHTFUL,3600,1))
    val STRONG_INSIGHT_POTION = Potion("insight",StatusEffectInstance(RegisterStatus.INSIGHTFUL,1800,3))
    val LONG_INSIGHT_POTION = Potion("insight",StatusEffectInstance(RegisterStatus.INSIGHTFUL,9600,1))


    fun registerAll(){
        Registry.register(Registry.POTION, Identifier(AI.MOD_ID,"curse"), CURSE_POTION)
        Registry.register(Registry.POTION, Identifier(AI.MOD_ID,"strong_curse"), STRONG_CURSE_POTION)
        Registry.register(Registry.POTION, Identifier(AI.MOD_ID,"long_curse"), LONG_CURSE_POTION)
        Registry.register(Registry.POTION, Identifier(AI.MOD_ID,"immunity"), IMMUNITY_POTION)
        Registry.register(Registry.POTION, Identifier(AI.MOD_ID,"long_immunity"), LONG_IMMUNITY_POTION)
        Registry.register(Registry.POTION, Identifier(AI.MOD_ID,"insight"), INSIGHT_POTION)
        Registry.register(Registry.POTION, Identifier(AI.MOD_ID,"strong_insight"), STRONG_INSIGHT_POTION)
        Registry.register(Registry.POTION, Identifier(AI.MOD_ID,"long_insight"), LONG_INSIGHT_POTION)

        BrewingRecipeRegistryInvoker.amethyst_imbuement_registerPotionRecipe(Potions.POISON,RegisterItem.ACCURSED_FIGURINE, CURSE_POTION)
        BrewingRecipeRegistryInvoker.amethyst_imbuement_registerPotionRecipe(CURSE_POTION, Items.GLOWSTONE_DUST, STRONG_CURSE_POTION)
        BrewingRecipeRegistryInvoker.amethyst_imbuement_registerPotionRecipe(CURSE_POTION,Items.REDSTONE, LONG_CURSE_POTION)
        BrewingRecipeRegistryInvoker.amethyst_imbuement_registerPotionRecipe(Potions.REGENERATION,Items.MILK_BUCKET, IMMUNITY_POTION)
        BrewingRecipeRegistryInvoker.amethyst_imbuement_registerPotionRecipe(IMMUNITY_POTION,Items.REDSTONE, LONG_IMMUNITY_POTION)
        BrewingRecipeRegistryInvoker.amethyst_imbuement_registerPotionRecipe(Potions.NIGHT_VISION,RegisterItem.KNOWLEDGE_POWDER, INSIGHT_POTION)
        BrewingRecipeRegistryInvoker.amethyst_imbuement_registerPotionRecipe(INSIGHT_POTION, Items.GLOWSTONE_DUST, STRONG_INSIGHT_POTION)
        BrewingRecipeRegistryInvoker.amethyst_imbuement_registerPotionRecipe(INSIGHT_POTION,Items.REDSTONE, LONG_INSIGHT_POTION)
    }
}