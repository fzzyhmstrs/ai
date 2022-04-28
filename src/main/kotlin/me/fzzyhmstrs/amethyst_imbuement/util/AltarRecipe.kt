package me.fzzyhmstrs.amethyst_imbuement.util

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.*
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World
import java.util.stream.Stream

class AltarRecipe(
    private val id: Identifier,
    val base: Ingredient,
    val addition: Ingredient,
    val result: ItemStack
) :
    Recipe<SimpleInventory> {

    object Type : RecipeType<AltarRecipe> {
        // This will be needed in step 4
        const val ID = "altar_recipe"
    }

    override fun matches(inventory: SimpleInventory, world: World): Boolean {
        return base.test(inventory.getStack(0)) && addition.test(inventory.getStack(1))
    }

    override fun craft(inventory: SimpleInventory): ItemStack {
        val itemStack = result.copy()
        val nbtCompound = inventory.getStack(0).nbt
        if (nbtCompound != null) {
            itemStack.nbt = nbtCompound.copy()
        }
        return itemStack
    }

    override fun fits(width: Int, height: Int): Boolean {
        return width * height >= 2
    }

    override fun getOutput(): ItemStack {
        return result
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

    override fun getType(): RecipeType<*> {
        return Type
    }
}