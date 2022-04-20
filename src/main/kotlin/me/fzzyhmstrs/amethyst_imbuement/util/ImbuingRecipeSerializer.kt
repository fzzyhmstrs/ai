package me.fzzyhmstrs.amethyst_imbuement.util

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.util.Identifier

@Suppress("PropertyName","CanBeVal")
object ImbuingRecipeSerializer: RecipeSerializer<ImbuingRecipe> {

    val ID = Identifier("amethyst_imbuement:imbuing")

    override fun read(id: Identifier, json: JsonObject): ImbuingRecipe {
        val recipeJson: ImbuingRecipeFormat = Gson().fromJson(json, ImbuingRecipeFormat::class.java)
        var inputsArray: Array<Ingredient> = arrayOf(Ingredient.EMPTY,Ingredient.EMPTY,Ingredient.EMPTY,
            Ingredient.EMPTY,Ingredient.EMPTY,Ingredient.EMPTY,
            Ingredient.EMPTY,Ingredient.EMPTY,Ingredient.EMPTY,
            Ingredient.EMPTY,Ingredient.EMPTY,Ingredient.EMPTY,
            Ingredient.EMPTY)
        if (recipeJson.imbueA != null) inputsArray[0] = Ingredient.fromJson(recipeJson.imbueA)
        if (recipeJson.imbueB != null) inputsArray[1] = Ingredient.fromJson(recipeJson.imbueB)
        if (recipeJson.imbueC != null) inputsArray[11] = Ingredient.fromJson(recipeJson.imbueC)
        if (recipeJson.imbueD != null) inputsArray[12] = Ingredient.fromJson(recipeJson.imbueD)
        if (recipeJson.craftA != null) inputsArray[2] = Ingredient.fromJson(recipeJson.craftA)
        if (recipeJson.craftB != null) inputsArray[3] = Ingredient.fromJson(recipeJson.craftB)
        if (recipeJson.craftC != null) inputsArray[4] = Ingredient.fromJson(recipeJson.craftC)
        if (recipeJson.craftD != null) inputsArray[5] = Ingredient.fromJson(recipeJson.craftD)
        if (recipeJson.craftE != null) inputsArray[6] = Ingredient.fromJson(recipeJson.craftE)
        if (recipeJson.craftF != null) inputsArray[7] = Ingredient.fromJson(recipeJson.craftF)
        if (recipeJson.craftG != null) inputsArray[8] = Ingredient.fromJson(recipeJson.craftG)
        if (recipeJson.craftH != null) inputsArray[9] = Ingredient.fromJson(recipeJson.craftH)
        if (recipeJson.craftI != null) inputsArray[10] = Ingredient.fromJson(recipeJson.craftI)
        val titleA = recipeJson.title?:""
        val costA = recipeJson.cost?:1
        val augmentA = recipeJson.augment?:""
        val resultA = recipeJson.resultA?:""
        if(augmentA == "" && resultA == ""){
            throw JsonSyntaxException("Need either an augment or item output!: $id")
        } else if(augmentA != "" && resultA != ""){
            throw JsonSyntaxException("Can't have both outputs and augments!: $id")
        }
        if (augmentA != ""){
            val augId = Identifier(augmentA).path
            if (ScepterObject.checkAugmentStat(augId)){
                val type = ScepterObject.getAugmentType(augId)
                val cooldown = ScepterObject.getAugmentCooldown(augId)
                val manaCost = ScepterObject.getAugmentManaCost(augId)
                val minLevel = ScepterObject.getAugmentMinLvl(augId)
                val bookOfLoreTier = ScepterObject.getAugmentTier(augId)
                val keyItem = ScepterObject.getAugmentItem(augId)
                val datapoint = ScepterObject.AugmentDatapoint(type,cooldown,manaCost,minLevel,costA,bookOfLoreTier,keyItem)
                ScepterObject.registerAugmentStat(augId,datapoint,true)
            }
        }
        val countA: Int = if (recipeJson.countA == 0){ 1 } else{ recipeJson.countA?:1 }
        val transferEnchant = recipeJson.transferEnchant?:false

        return ImbuingRecipe(inputsArray,resultA,countA,augmentA,transferEnchant,titleA,costA,id)
    }

    override fun write(buf: PacketByteBuf, recipe: ImbuingRecipe) {
        recipe.getImbueA().write(buf)
        recipe.getImbueB().write(buf)
        recipe.getImbueC().write(buf)
        recipe.getImbueD().write(buf)
        for (i in 0..8){
            recipe.getCrafts(i).write(buf)
        }

        buf.writeString(recipe.getTitle())
        buf.writeInt(recipe.getCost())
        buf.writeString(recipe.getAugment())
        buf.writeString(recipe.getResult())
        buf.writeInt(recipe.getCount())
        buf.writeBoolean(recipe.getTransferEnchant())
    }

    override fun read(id: Identifier, buf: PacketByteBuf): ImbuingRecipe {
        var inputsArray: Array<Ingredient> = arrayOf(Ingredient.EMPTY,Ingredient.EMPTY,Ingredient.EMPTY,
            Ingredient.EMPTY,Ingredient.EMPTY,Ingredient.EMPTY,
            Ingredient.EMPTY,Ingredient.EMPTY,Ingredient.EMPTY,
            Ingredient.EMPTY,Ingredient.EMPTY,Ingredient.EMPTY,
            Ingredient.EMPTY)
        for (i in 0..12) {
            inputsArray[i] = Ingredient.fromPacket(buf)
        }
        val titleA = buf.readString()
        val costA = buf.readInt()
        val augmentA = buf.readString()
        val resultA = buf.readString()
        val countA = buf.readInt()
        val transferEnchant = buf.readBoolean()
        return ImbuingRecipe(inputsArray,resultA,countA,augmentA,transferEnchant,titleA,costA,id)
    }


}