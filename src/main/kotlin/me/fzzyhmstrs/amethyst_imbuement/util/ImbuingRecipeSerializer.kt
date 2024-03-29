package me.fzzyhmstrs.amethyst_imbuement.util

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.util.Identifier
import kotlin.math.max

@Suppress("PropertyName","CanBeVal")
object ImbuingRecipeSerializer: RecipeSerializer<ImbuingRecipe> {

    val ID = Identifier(AI.MOD_ID,"imbuing")
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
            val augId = Identifier(augmentA).toString()
            if (AugmentHelper.checkAugmentStat(augId)){
                val type = AugmentHelper.getAugmentType(augId)
                val cooldown = AugmentHelper.getAugmentCooldown(augId)
                val manaCost = AugmentHelper.getAugmentManaCost(augId)
                val minLevel = AugmentHelper.getAugmentMinLvl(augId)
                val castXp = AugmentHelper.getAugmentCastXp(augId)
                val bookOfLoreTier = AugmentHelper.getAugmentTier(augId)
                val keyItem = AugmentHelper.getAugmentItem(augId)
                val enabled = AugmentHelper.getAugmentEnabled(augId)
                val pvpMode = AugmentHelper.getAugmentPvpMode(augId)
                val datapoint = AugmentDatapoint(type,cooldown,manaCost,minLevel,costA,castXp,bookOfLoreTier,keyItem,enabled,pvpMode)
                AugmentHelper.registerAugmentStat(augId,datapoint,true)
            }
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