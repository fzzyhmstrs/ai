package me.fzzyhmstrs.amethyst_imbuement.screen

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterCriteria
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterHandler
import me.fzzyhmstrs.amethyst_imbuement.util.AltarRecipe
import me.shedaniel.rei.api.common.transfer.RecipeFinder
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.Property
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
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
    private val handlerContext: ScreenHandlerContext
): ScreenHandler(RegisterHandler.CRYSTAL_ALTAR_SCREEN_HANDLER,syncID) {

    constructor(syncID: Int, playerInventory: PlayerInventory) : this(
        syncID,
        playerInventory,
        ScreenHandlerContext.EMPTY,
    )

    val flowerSlot: Property = Property.create()
    private var world: World
    private var currentRecipe: AltarRecipe? = null
    private var recipes: List<AltarRecipe>

    private val input: Inventory = object : SimpleInventory(3) {
        override fun markDirty() {
            super.markDirty()
            this@CrystalAltarScreenHandler.onContentChanged(this)
        }
    }

    private val output = CraftingResultInventory()

    init{
        world = playerInventory.player.world
        recipes = playerInventory.player.world.recipeManager.listAllOfType(AltarRecipe.Type)
        addSlot(Slot(input, 0, 33, 47))
        addSlot(Slot(input, 1, 51, 47))
        addSlot(Slot(input, 2, 69, 47))
        addSlot(object : Slot(output, 3, 127, 47) {
            override fun canInsert(stack: ItemStack): Boolean {
                return false
            }

            override fun canTakeItems(playerEntity: PlayerEntity): Boolean {
                return this@CrystalAltarScreenHandler.canTakeOutput(playerEntity, hasStack())
            }

            override fun onTakeItem(player: PlayerEntity, stack: ItemStack) {
                this@CrystalAltarScreenHandler.onTakeOutput(player, stack)
            }
        })
        for (i in 0..2) {
            for (j in 0..8) {
                addSlot(Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
            }
        }
        for (j in 0..8) {
            addSlot(Slot(playerInventory, j, 8 + j * 18, 142))
        }
    }

    override fun onContentChanged(inventory: Inventory) {
        super.onContentChanged(inventory)
        if (inventory === this.input){
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
    }

    private fun canUse(state: BlockState): Boolean {
        return state.isOf(RegisterBlock.CRYSTAL_ALTAR)
    }

    override fun canUse(player: PlayerEntity): Boolean {
        return handlerContext.get(BiFunction { world: World, pos: BlockPos ->
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

    private fun canTakeOutput(player: PlayerEntity, present: Boolean): Boolean {
        val recipe = this.currentRecipe
        return recipe?.matches(input as SimpleInventory, this.world) ?: false
    }

    private fun onTakeOutput(player: PlayerEntity, stack: ItemStack) {
        println(player.world.isClient)
        println(stack)
        stack.onCraft(player.world, player, stack.count)
        output.unlockLastRecipe(player)
        decrementStack(0)
        decrementStack(1)
        decrementStack(2)
        handlerContext.run { world: World, pos: BlockPos ->
            if (player is ServerPlayerEntity) {
                RegisterCriteria.ENHANCE.trigger(player)
            }
            world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.6f, world.random.nextFloat() * 0.1f + 0.9f)
        }
    }

    private fun decrementStack(slot: Int) {
        val itemStack = input.getStack(slot)
        itemStack.decrement(1)
        input.setStack(slot, itemStack)
    }

    private fun isUsableAsAddition(stack: ItemStack): Boolean {
        return this.recipes.stream().anyMatch { recipe: AltarRecipe ->
            recipe.testAddition(
                stack
            )
        }
    }

    private fun isUsableAsDust(stack: ItemStack): Boolean {
        return this.recipes.stream().anyMatch { recipe: AltarRecipe ->
            recipe.testDust(
                stack
            )
        }
    }

    override fun transferSlot(player: PlayerEntity, slot: Int): ItemStack {
        var itemStack = ItemStack.EMPTY
        val slot2 = slots[slot] as Slot
        if (slot2 != null && slot2.hasStack()) {
            val itemStack2 = slot2.stack
            itemStack = itemStack2.copy()
            println(itemStack)
            if (slot == 3) {
                itemStack2.onCraft(player.world,player,itemStack2.count)
                if (!insertItem(itemStack2, 4, 40, true)) {
                    return ItemStack.EMPTY
                }
                slot2.onQuickTransfer(itemStack2, itemStack)
            } else if (slot == 0 || slot == 1 || slot == 2) {
                if (!insertItem(itemStack2, 4, 40, false)) {
                    return ItemStack.EMPTY
                }
            } else if (slot in 4..39) {
                val i: Int = if (isUsableAsAddition(itemStack)) {
                    2
                } else if (isUsableAsDust(itemStack)){
                    0
                } else {
                    1
                }
                if (!insertItem(itemStack2, i, 3, false)) {
                    return ItemStack.EMPTY
                }
            }
            if (itemStack2.isEmpty) {
                slot2.stack = ItemStack.EMPTY
            } else {
                slot2.markDirty()
            }
            slot2.onTakeItem(player, itemStack)
            if (itemStack2.count == itemStack.count) {
                return ItemStack.EMPTY
            }
        }
        return itemStack
    }

    override fun canInsertIntoSlot(stack: ItemStack?, slot: Slot): Boolean {
        return slot.inventory !== output && super.canInsertIntoSlot(stack, slot)
    }

    override fun close(player: PlayerEntity?) {
        super.close(player)
        handlerContext.run { _: World, _: BlockPos ->
            dropInventory(
                player,
                input
            )
        }
    }

    fun populateRecipeFinder(finder: RecipeFinder) {
        for (i in 0..2){
            val stack = input.getStack(i)
            finder.addNormalItem(stack)
        }
    }
}