package me.fzzyhmstrs.amethyst_imbuement.util

import me.fzzyhmstrs.amethyst_imbuement.item.BookOfLoreItem
import me.fzzyhmstrs.amethyst_imbuement.item.BookOfMythosItem
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.World


@Suppress("PropertyName")
class ImbuingRecipe(_inputs: Array<Ingredient>, _result: String, _count: Int,_augment: String, _transferEnchant: Boolean,_title: String,_cost: Int, _id: Identifier): Recipe<SimpleInventory> {
    private val result: String
    private val count: Int
    private val augment: String
    private val transferEnchant: Boolean
    private val title: String
    private val cost: Int
    private val id: Identifier
    private var inputs: Array<Ingredient> = arrayOf(Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY,Ingredient.EMPTY,
        Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY,
        Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY,
        Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY)
    private val imbueA: Ingredient
    private val imbueB: Ingredient
    private val imbueC: Ingredient
    private val imbueD: Ingredient
    private var crafts: Array<Ingredient> = arrayOf(Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY,
        Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY,
        Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY)

    init{
        if (inputs.size != 13) throw UnsupportedOperationException("Recipe input not the correct size")
        //inputs array is 4 imbuing slots left>right, top>bottom, then the 9 crafting slots left>right,top>bottom
        imbueA = _inputs[0]
        imbueB = _inputs[1]
        imbueC = _inputs[11]
        imbueD = _inputs[12]
        for (i in 0..8) {
            crafts[i] = _inputs[2+i]
        }
        for (i in 0..12){
            inputs[i] = _inputs[i]
        }
        result = _result
        count = _count
        augment = _augment
        transferEnchant = _transferEnchant
        title = _title
        cost = _cost
        id = _id
    }


    override fun matches(inventory: SimpleInventory, world: World): Boolean {
        if (inventory.size() < 13) return false
        var bl1 = imbueA.test(inventory.getStack(0))
        bl1 = bl1 && imbueB.test(inventory.getStack(1))
        bl1 = bl1 && imbueC.test(inventory.getStack(11))
        bl1 = bl1 && imbueD.test(inventory.getStack(12))
        for (i in 0..8){
            if (i==4 && augment != "") continue
            bl1 = bl1 && crafts[i].test(inventory.getStack(2+i))
        }
        if (!bl1) return false
        if (augment != "") {
            for (i in 0..12) {
                val stacks = inputs[i].matchingStacks
                for (stack in stacks){
                    if (stack.item is BookOfLoreItem || stack.item is BookOfMythosItem){
                        val stackTest = inventory.getStack(i)
                        if (!stackTest.hasNbt()){
                            return false
                        } else {
                            val nbt = stackTest.orCreateNbt
                            if (!nbt.contains(NbtKeys.LORE_KEY.str())){
                                return false
                            } else {
                                val idTest = Identifier(augment)
                                val pathTest = idTest.path
                                val nbtTest = readStringNbt(NbtKeys.LORE_KEY.str(),nbt)
                                bl1 = bl1 && pathTest == nbtTest
                            }
                        }
                    }
                }
            }
        }
        return bl1
    }

    override fun craft(inventory: SimpleInventory): ItemStack {
        return ItemStack.EMPTY
    }
    override fun fits(var1: Int, var2: Int): Boolean {
        return false
    }

    object Type : RecipeType<ImbuingRecipe> {
        // This will be needed in step 4
        const val ID = "imbuing_recipe"
    }

    override fun getType(): RecipeType<*> {
        return Type
    }
    fun getImbueA(): Ingredient{
        return imbueA
    }
    fun getImbueB(): Ingredient{
        return imbueB
    }
    fun getImbueC(): Ingredient{
        return imbueC
    }
    fun getImbueD(): Ingredient{
        return imbueD
    }
    fun getCrafts(index: Int): Ingredient{
        return if (index in 0..8) {
            crafts[index]
        } else{
            Ingredient.EMPTY
        }
    }
    fun getInputs(): Array<Ingredient>{
        return inputs
    }
    override fun getOutput(): ItemStack {
        return ItemStack(Registry.ITEM.getOrEmpty(Identifier(result)).get(),count)
    }
    fun getAugment(): String {
        return augment
    }
    fun getTransferEnchant(): Boolean {
        return transferEnchant
    }
    fun getTitle(): String {
        return title
    }
    fun getCost(): Int {
        return cost
    }
    fun getResult(): String {
        return result
    }
    fun getCount(): Int {
        return count
    }
    override fun getId(): Identifier {
        return id
    }
    override fun getSerializer(): RecipeSerializer<*>? {
        return ImbuingRecipeSerializer
    }

    private fun readStringNbt(key: String, nbt: NbtCompound): String {
        return nbt.getString(key)
    }
}