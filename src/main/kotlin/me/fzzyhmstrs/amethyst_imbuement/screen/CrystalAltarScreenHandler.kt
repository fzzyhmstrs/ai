package me.fzzyhmstrs.amethyst_imbuement.screen

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterHandler
import me.fzzyhmstrs.amethyst_imbuement.util.AltarRecipe
import me.shedaniel.rei.api.common.transfer.RecipeFinder
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ForgingScreenHandler
import net.minecraft.screen.Property
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.Slot
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*
import java.util.function.BiFunction

@Suppress("SENSELESS_COMPARISON", "USELESS_CAST")
class CrystalAltarScreenHandler(
    syncID: Int,
    playerInventory: PlayerInventory,
    handlerContext: ScreenHandlerContext
): ForgingScreenHandler(RegisterHandler.CRYSTAL_ALTAR_SCREEN_HANDLER,syncID,playerInventory,handlerContext) {

    constructor(syncID: Int, playerInventory: PlayerInventory) : this(
        syncID,
        playerInventory,
        ScreenHandlerContext.EMPTY,
    )

    val flowerSlot: Property = Property.create()
    private var world: World
    private var currentRecipe: AltarRecipe? = null
    private var recipes: List<AltarRecipe>

    init{
        addProperty(flowerSlot).set(0)
        world = playerInventory.player.world
        recipes = playerInventory.player.world.recipeManager.listAllOfType(AltarRecipe.Type)
    }

    override fun onContentChanged(inventory: Inventory) {
        super.onContentChanged(inventory)
        if (inventory === this.input){
            val flowerStack = inventory.getStack(1)
            val flowerStackFull = if(flowerStack.isEmpty){ 0 }else{ 1 }
            flowerSlot.set(flowerStackFull)
            sendContentUpdates()
        }
    }

    override fun canUse(state: BlockState): Boolean {
        return state.isOf(RegisterBlock.CRYSTAL_ALTAR)
    }

    override fun canUse(player: PlayerEntity): Boolean {
        return this.context.get(BiFunction { world: World, pos: BlockPos ->
            if (!this.canUse(world.getBlockState(pos))) {
                return@BiFunction false
            }
            player.squaredDistanceTo(
                pos.x.toDouble() + 0.5,
                pos.y.toDouble() + 0.5,
                pos.z.toDouble() + 0.5
            ) <= 64.0
        }, true)
    }

    override fun canTakeOutput(player: PlayerEntity, present: Boolean): Boolean {
        val recipe = this.currentRecipe
        return recipe?.matches(input as SimpleInventory, this.world) ?: false
    }

    override fun onTakeOutput(player: PlayerEntity, stack: ItemStack) {
        stack.onCraft(player.world, player, stack.count)
        output.unlockLastRecipe(player)
        decrementStack(0)
        decrementStack(1)
        this.context.run { world: World, pos: BlockPos ->
            world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.6f, world.random.nextFloat() * 0.1f + 0.9f)
        }
    }

    private fun decrementStack(slot: Int) {
        val itemStack = input.getStack(slot)
        itemStack.decrement(1)
        input.setStack(slot, itemStack)
    }

    override fun updateResult() {
        val match: Optional<AltarRecipe> = this.world.recipeManager.getFirstMatch(
            AltarRecipe.Type,
            input as SimpleInventory, this.world
        )
        if (match.isEmpty) {
            output.setStack(0, ItemStack.EMPTY)
        } else {
            this.currentRecipe = match.get()
            val recipe = match.get()
            val itemStack: ItemStack = recipe.craft(input as SimpleInventory)
            output.lastRecipe = this.currentRecipe
            output.setStack(0, itemStack)
        }
    }

    override fun isUsableAsAddition(stack: ItemStack): Boolean {
        return this.recipes.stream().anyMatch { recipe: AltarRecipe ->
            recipe.testAddition(
                stack
            )
        }
    }

    override fun canInsertIntoSlot(stack: ItemStack?, slot: Slot): Boolean {
        return slot.inventory !== output && super.canInsertIntoSlot(stack, slot)
    }

    override fun close(player: PlayerEntity?) {
        super.close(player)
        context.run { _: World, _: BlockPos ->
            dropInventory(
                player,
                input
            )
        }
    }

    fun populateRecipeFinder(finder: RecipeFinder) {
        for (i in 0..1){
            val stack = input.getStack(i)
            finder.addNormalItem(stack)
        }
    }
}