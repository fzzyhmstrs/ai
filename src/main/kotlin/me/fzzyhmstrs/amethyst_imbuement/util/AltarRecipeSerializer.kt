@file:Suppress("SENSELESS_COMPARISON")

package me.fzzyhmstrs.amethyst_imbuement.util

import com.google.gson.Gson
import com.google.gson.JsonObject
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.item.ItemStack
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
        val dust = RecipeUtil.ingredientFromJson(recipeJson.dust)
        val base = RecipeUtil.ingredientFromJson(recipeJson.base)
        val addition = RecipeUtil.ingredientFromJson(recipeJson.addition)
        val result = if (json.has("result")) ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result")) else ItemStack.EMPTY
        return AltarRecipe(id,dust,base,addition,result, recipeJson.react)
    }

    override fun write(buf: PacketByteBuf, recipe: AltarRecipe) {
        recipe.dust.write(buf)
        recipe.base.write(buf)
        recipe.addition.write(buf)
        buf.writeItemStack(recipe.result)
        buf.writeString(recipe.reactMessage)
    }

    override fun read(id: Identifier, buf: PacketByteBuf): AltarRecipe {
        val dust = Ingredient.fromPacket(buf)
        val base = Ingredient.fromPacket(buf)
        val addition = Ingredient.fromPacket(buf)
        val result = buf.readItemStack()
        val reactMessage = buf.readString()
        return AltarRecipe(id,dust,base,addition,result,reactMessage)
    }


}
