package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.item.Items
import net.minecraft.potion.Potion
import net.minecraft.potion.Potions
import net.minecraft.recipe.BrewingRecipeRegistry
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

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
        Registry.register(Registries.POTION, Identifier(AI.MOD_ID,"curse"), CURSE_POTION)
        Registry.register(Registries.POTION, Identifier(AI.MOD_ID,"strong_curse"), STRONG_CURSE_POTION)
        Registry.register(Registries.POTION, Identifier(AI.MOD_ID,"long_curse"), LONG_CURSE_POTION)
        Registry.register(Registries.POTION, Identifier(AI.MOD_ID,"immunity"), IMMUNITY_POTION)
        Registry.register(Registries.POTION, Identifier(AI.MOD_ID,"long_immunity"), LONG_IMMUNITY_POTION)
        Registry.register(Registries.POTION, Identifier(AI.MOD_ID,"insight"), INSIGHT_POTION)
        Registry.register(Registries.POTION, Identifier(AI.MOD_ID,"strong_insight"), STRONG_INSIGHT_POTION)
        Registry.register(Registries.POTION, Identifier(AI.MOD_ID,"long_insight"), LONG_INSIGHT_POTION)

        BrewingRecipeRegistry.registerPotionRecipe(Potions.POISON,RegisterItem.ACCURSED_FIGURINE, CURSE_POTION)
        BrewingRecipeRegistry.registerPotionRecipe(CURSE_POTION, Items.GLOWSTONE_DUST, STRONG_CURSE_POTION)
        BrewingRecipeRegistry.registerPotionRecipe(CURSE_POTION,Items.REDSTONE, LONG_CURSE_POTION)
        BrewingRecipeRegistry.registerPotionRecipe(Potions.REGENERATION,Items.MILK_BUCKET, IMMUNITY_POTION)
        BrewingRecipeRegistry.registerPotionRecipe(IMMUNITY_POTION,Items.REDSTONE, LONG_IMMUNITY_POTION)
    }
}