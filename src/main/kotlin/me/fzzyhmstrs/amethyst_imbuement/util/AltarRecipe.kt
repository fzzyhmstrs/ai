package me.fzzyhmstrs.amethyst_imbuement.util

import me.fzzyhmstrs.amethyst_core.modifier_util.ModifierHelper
import me.fzzyhmstrs.amethyst_imbuement.item.Reactant
import me.fzzyhmstrs.amethyst_imbuement.item.Reagent
import me.fzzyhmstrs.fzzy_core.nbt_util.Nbt
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import net.minecraft.world.World

class AltarRecipe(
    private val id: Identifier,
    val dust: Ingredient,
    val base: Ingredient,
    val addition: Ingredient,
    val result: ItemStack,
    val reactMessage: String = ""
) :
    Recipe<SimpleInventory> {

    val react = reactMessage != ""
        
    override fun matches(inventory: SimpleInventory, world: World): Boolean {
        var bl = dust.test(inventory.getStack(0)) && base.test(inventory.getStack(1)) && addition.test(inventory.getStack(2))
        if (!bl) return false
        val item = inventory.getStack(1).item
        if (item is Reactant){
            bl = bl && item.canReact(inventory.getStack(1), Reagent.getReagents(inventory), null,Type)
        }
        return bl
    }

    override fun craft(inventory: SimpleInventory): ItemStack {
        val itemStack = if(!react) result.copy() else Nbt.createItemStackWithNbt( inventory.getStack(1).item,inventory.getStack(1).count, inventory.getStack(1).orCreateNbt)
        if (!react) {
            val nbtCompound = inventory.getStack(1).nbt
            if (nbtCompound != null) {
                itemStack.nbt = nbtCompound.copy()
            }
        }
        val item = itemStack.item
        if (item is Reactant){
            item.react(itemStack, Reagent.getReagents(inventory), null, Type)
        }
        return itemStack
    }

    override fun fits(width: Int, height: Int): Boolean {
        return width * height >= 3
    }

    override fun getOutput(): ItemStack {
        return result.copy()
    }

    fun testDust(stack: ItemStack): Boolean {
        return dust.test(stack)
    }
    
    fun testAddition(stack: ItemStack): Boolean {
        return addition.test(stack)
    }

    override fun getId(): Identifier {
        return id
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return AltarRecipeSerializer
    }


    object Type : RecipeType<AltarRecipe> {
        // This will be needed in step 4
        const val ID = "altar_recipe"
    }

    override fun getType(): RecipeType<*> {
        return Type
    }
}
