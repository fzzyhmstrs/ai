package me.fzzyhmstrs.amethyst_imbuement.util

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.IntList
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentModifier
import me.fzzyhmstrs.amethyst_core.modifier_util.ModifierHelper
import me.fzzyhmstrs.amethyst_core.scepter_util.addIfDistinct
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.item.Reactant
import me.fzzyhmstrs.amethyst_imbuement.item.Reagent
import me.fzzyhmstrs.amethyst_imbuement.item.SpellScrollItem
import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfLoreItem
import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfMythosItem
import me.fzzyhmstrs.fzzy_core.registry.ModifierRegistry
import me.fzzyhmstrs.fzzy_core.trinket_util.base_augments.BaseAugment
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.world.World


@Suppress("PropertyName")
class ImbuingRecipe(private val inputs: Array<Ingredient>,
                    private val result: String,
                    private val count: Int,
                    private val augment: String,
                    private val transferEnchant: Boolean,
                    private val title: String,
                    private val cost: Int,
                    private val id: Identifier): Recipe<SimpleInventory> {
    private val resultId: Identifier = Identifier(result)
    private val augId: Identifier = Identifier(augment)
    private val outputItem: ItemStack by lazy{
        outputGenerator()
    }
    private val centerSlot: Ingredient by lazy{
        val ingredient = centerSlotGenerator()
        if(ingredient.isEmpty){
            inputs[6]
        } else {
            ingredient
        }
    }
    private val imbueA: Ingredient
    private val imbueB: Ingredient
    private val imbueC: Ingredient
    private val imbueD: Ingredient
    private val crafts: Array<Ingredient>
    private val bomList: MutableList<Int> = mutableListOf()
    private val bomStructure: Int2ObjectOpenHashMap<IntList> by lazy {
        generateStructure()
    }


    init{
        if (inputs.size != 13) throw UnsupportedOperationException("Recipe input not the correct size")
        //inputs array is 4 imbuing slots left>right, top>bottom, then the 9 crafting slots left>right,top>bottom
        imbueA = inputs[0]
        imbueB = inputs[1]
        imbueC = inputs[11]
        imbueD = inputs[12]
        crafts = arrayOf(
            inputs[2],
            inputs[3],
            inputs[4],
            inputs[5],
            inputs[6],
            inputs[7],
            inputs[8],
            inputs[9],
            inputs[10]
        )
    }

    private fun generateStructure():Int2ObjectOpenHashMap<IntList>{
        val tempStruct = Int2ObjectOpenHashMap<IntList>(16,0.8f)
        inputs.forEachIndexed { index, ingredient ->
            if (index == 6 && augment != ""){
                val centerIngredient = getCenterIngredient()
                if (!centerIngredient.isEmpty) {
                    val ids = centerIngredient.matchingItemIds
                    tempStruct[index] = ids
                    ids.forEach {
                        bomList.addIfDistinct(it)
                    }
                }
            } else if (!ingredient.isEmpty) {
                val ids = ingredient.matchingItemIds
                tempStruct[index] = ids
                ids.forEach {
                    bomList.addIfDistinct(it)
                }
            }
        }
        return tempStruct
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
                            if (!nbt.contains(me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys.LORE_KEY.str())){
                                return false
                            } else {
                                val idTest = Identifier(augment)
                                val pathTest = idTest.toString()
                                val loreString = nbt.getString(me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys.LORE_KEY.str())
                                val nbtTest = if (Identifier(loreString).namespace == "minecraft"){
                                    Identifier(AI.MOD_ID,Identifier(loreString).path).toString()
                                } else {
                                    loreString
                                }
                                bl1 = bl1 && pathTest == nbtTest
                            }
                        }
                    }
                }
            }
        }
        val test = if(augment != "") inventory.getStack(6) else outputItem
        val item = test.item
        if (item is Reactant){
            bl1 = bl1 && item.canReact(test, Reagent.getReagents(inventory),null,Type)
        }
        return bl1
    }

    override fun craft(inventory: SimpleInventory, registryManager: DynamicRegistryManager): ItemStack {
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
    fun getInputs(): Array<Ingredient>{
        return inputs
    }
    override fun getOutput(registryManager: DynamicRegistryManager): ItemStack {
        return outputItem.copy()
    }
    fun getOutput(): ItemStack{
        return outputItem.copy()
    }
    fun setOutput(stack: ItemStack){}
    private fun outputGenerator(): ItemStack{
        if (augment == "") return ItemStack(Registries.ITEM.getOrEmpty(resultId).orElse(Items.AIR),count)
        val identifier = Identifier(augment)
        val enchant = Registries.ENCHANTMENT.get(identifier)
        val modifier = ModifierRegistry.getByType<AugmentModifier>(identifier)
        return if (enchant != null){
            if (enchant is ScepterAugment){
                SpellScrollItem.createSpellScroll(enchant)
            } else {
                val stack = ItemStack(Items.ENCHANTED_BOOK, 1)
                EnchantedBookItem.addEnchantment(stack, EnchantmentLevelEntry(enchant, 1))
                stack
            }
        } else if (modifier != null){
            val stack = modifier.acceptableItemStacks().first()
            val moddedStack = stack.copy()
            ModifierHelper.addModifierForREI(modifier.modifierId, moddedStack)
            stack
        } else {
            ItemStack(Items.BOOK, 1)
        }
    }

    fun getCenterIngredient(): Ingredient{
        return centerSlot
    }
    private fun centerSlotGenerator(): Ingredient{
        if (augId.path == "") return Ingredient.EMPTY
        val enchant = Registries.ENCHANTMENT.get(augId)
        val modifier = ModifierRegistry.getByType<AugmentModifier>(augId)
        if (enchant != null){
            when (enchant) {
                is BaseAugment -> {
                    val stacks = enchant.acceptableItemStacks().toTypedArray()
                    return Ingredient.ofStacks(*stacks)
                }
                is ScepterAugment -> {
                    val opt = Registries.ITEM.getEntryList(enchant.getTag())
                    val stacks = if(opt.isPresent){
                        opt.get().stream().map { entry -> ItemStack(entry.value()) }.toList()
                    } else {
                        listOf()
                    }
                    return Ingredient.ofStacks(*stacks.toTypedArray())
                }
                else -> {
                    val enchantItemList: MutableList<ItemStack> = mutableListOf()
                    for (item in Registries.ITEM.iterator()){
                        val stack = ItemStack(item)
                        if (enchant.isAcceptableItem(stack)){
                            enchantItemList.add(stack)
                        }
                    }
                    return Ingredient.ofStacks(*enchantItemList.toTypedArray())
                }
            }
        } else if(modifier != null) {
            val stacks = modifier.acceptableItemStacks().toTypedArray()
            return Ingredient.ofStacks(*stacks)
        } else {
            return Ingredient.EMPTY
        }
    }

    fun getAugment(): String {
        return augment
    }
    fun getAugmentId(): Identifier{
        return augId
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
    override fun getSerializer(): RecipeSerializer<*> {
        return ImbuingRecipeSerializer
    }
    fun getBom(): Int2ObjectOpenHashMap<IntList>{
        return bomStructure
    }
    fun getBomList(): List<Int>{
        return bomList
    }

    companion object{

        fun blankRecipe(): ImbuingRecipe{
            return ImbuingRecipe(arrayOf(Ingredient.EMPTY,Ingredient.EMPTY,Ingredient.EMPTY,
                Ingredient.EMPTY,Ingredient.EMPTY,Ingredient.EMPTY,
                Ingredient.EMPTY,Ingredient.EMPTY,Ingredient.EMPTY,
                Ingredient.EMPTY,Ingredient.EMPTY,Ingredient.EMPTY,
                Ingredient.EMPTY),"",1,"",false,"",1, Identifier(AI.MOD_ID,"blank_recipe")
            )
        }

    }
}
