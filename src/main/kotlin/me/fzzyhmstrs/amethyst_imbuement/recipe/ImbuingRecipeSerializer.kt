package me.fzzyhmstrs.amethyst_imbuement.recipe

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.util.Identifier
import kotlin.math.max

@Suppress("PropertyName","CanBeVal")
object ImbuingRecipeSerializer: RecipeSerializer<ImbuingRecipe> {

    val ID = AI.identity("imbuing")
    private val gson = Gson()

    override fun read(id: Identifier, json: JsonObject): ImbuingRecipe {
        val recipeJson: ImbuingRecipeFormat = gson.fromJson(json, ImbuingRecipeFormat::class.java)
        val inputsArray: Array<Ingredient> = arrayOf(
            RecipeUtil.ingredientFromJson(recipeJson.imbueA),
            RecipeUtil.ingredientFromJson(recipeJson.imbueB),
            RecipeUtil.ingredientFromJson(recipeJson.craftA),
            RecipeUtil.ingredientFromJson(recipeJson.craftB),
            RecipeUtil.ingredientFromJson(recipeJson.craftC),
            RecipeUtil.ingredientFromJson(recipeJson.craftD),
            RecipeUtil.ingredientFromJson(recipeJson.craftE),
            RecipeUtil.ingredientFromJson(recipeJson.craftF),
            RecipeUtil.ingredientFromJson(recipeJson.craftG),
            RecipeUtil.ingredientFromJson(recipeJson.craftH),
            RecipeUtil.ingredientFromJson(recipeJson.craftI),
            RecipeUtil.ingredientFromJson(recipeJson.imbueC),
            RecipeUtil.ingredientFromJson(recipeJson.imbueD)
        )
        val titleA = recipeJson.title
        val costA = recipeJson.cost
        val augmentA = recipeJson.augment
        val resultA = recipeJson.resultA
        if(augmentA == "" && resultA == ""){
            throw JsonSyntaxException("Need either an augment or item output!: $id")
        } else if(augmentA != "" && resultA != ""){
            throw JsonSyntaxException("Can't have both outputs and augments!: $id")
        }
        if (augmentA != ""){
            val augId = Identifier(augmentA)
            AugmentHelper.setAugmentImbueLevel(augId, costA)
        }
        val countA: Int = max(1,recipeJson.countA )
        val transferEnchant = recipeJson.transferEnchant

        return ImbuingRecipe(inputsArray,resultA,countA,augmentA,transferEnchant,titleA,costA,id)
    }

    override fun write(buf: PacketByteBuf, recipe: ImbuingRecipe) {
        val inputs = recipe.getInputs()
        for (i in 0..12){
            inputs[i].write(buf)
        }
        buf.writeString(recipe.getTitle())
        buf.writeInt(recipe.getCost())
        buf.writeString(recipe.getAugment())
        buf.writeString(recipe.getResult())
        buf.writeInt(recipe.getCount())
        buf.writeBoolean(recipe.getTransferEnchant())
    }

    override fun read(id: Identifier, buf: PacketByteBuf): ImbuingRecipe {
        val inputsArray: Array<Ingredient> = arrayOf(
            Ingredient.fromPacket(buf),
            Ingredient.fromPacket(buf),
            Ingredient.fromPacket(buf),
            Ingredient.fromPacket(buf),
            Ingredient.fromPacket(buf),
            Ingredient.fromPacket(buf),
            Ingredient.fromPacket(buf),
            Ingredient.fromPacket(buf),
            Ingredient.fromPacket(buf),
            Ingredient.fromPacket(buf),
            Ingredient.fromPacket(buf),
            Ingredient.fromPacket(buf),
            Ingredient.fromPacket(buf)
        )
        val titleA = buf.readString()
        val costA = buf.readInt()
        val augmentA = buf.readString()
        val resultA = buf.readString()
        val countA = buf.readInt()
        val transferEnchant = buf.readBoolean()
        return ImbuingRecipe(inputsArray,resultA,countA,augmentA,transferEnchant,titleA,costA,id)
    }

}