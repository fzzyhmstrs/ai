@file:Suppress("SENSELESS_COMPARISON")

package me.fzzyhmstrs.amethyst_imbuement.util

import com.google.gson.Gson
import com.google.gson.JsonObject
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper

@Suppress("PropertyName")
object AltarRecipeSerializer: RecipeSerializer<AltarRecipe> {

    val ID = Identifier(AI.MOD_ID,"enhancing")
    private val gson = Gson()

    override fun read(id: Identifier, json: JsonObject): AltarRecipe {
        val recipeJson: AltarRecipeFormat = gson.fromJson(json, AltarRecipeFormat::class.java)
        val base = RecipeUtil.ingredientFromJson(recipeJson.base)
        val addition = RecipeUtil.ingredientFromJson(recipeJson.addition)
        val itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"))
        return AltarRecipe(id,base, addition,itemStack)
    }

    override fun write(buf: PacketByteBuf, recipe: AltarRecipe) {
        recipe.base.write(buf)
        recipe.addition.write(buf)
        buf.writeItemStack(recipe.result)
    }

    override fun read(id: Identifier, buf: PacketByteBuf): AltarRecipe {
        val base = Ingredient.fromPacket(buf)
        val addition = Ingredient.fromPacket(buf)
        val result = buf.readItemStack()
        return AltarRecipe(id,base,addition,result)
    }


}