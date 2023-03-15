package me.fzzyhmstrs.amethyst_imbuement.screen

import me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterCriteria
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterHandler
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTag
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.registry.Registries
import net.minecraft.screen.Property
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import java.util.*
import kotlin.math.min

@Suppress("SENSELESS_COMPARISON", "unused", "UnnecessaryVariable")
class SpellcastersFocusScreenHandler(
    syncID: Int,
    private val playerInventory: PlayerInventory,
    private val context: ScreenHandlerContext
):  ScreenHandler(RegisterHandler.SPELLCASTERS_FOCUS_SCREEN_HANDLER, syncID) {

    constructor(syncID: Int, playerInventory: PlayerInventory) : this(
        syncID,
        playerInventory,
        ScreenHandlerContext.EMPTY,
    )
    
    private val random = Random()
    private val seed = Property.create()
    private var activeItem = Property.create()
    var enchantmentId = intArrayOf(-1, -1, -1)
    var enchantmentLevel = intArrayOf(-1, -1, -1)
    var disenchantCost = IntArray(1)
    private var removing = false
    private val player = playerInventory.player


    override fun canUse(player: PlayerEntity): Boolean {
        return canUse(this.context, player, RegisterBlock.DISENCHANTING_TABLE)
    }

    fun getSlotStack(index:Int): ItemStack{
        if (index < 0 || index > inventory.size()-1) return ItemStack.EMPTY
        return inventory.getStack(index)

    }

    fun getSeed(): Int {
        return this.seed.get()
    }

    override fun close(player: PlayerEntity) {
        super.close(player)
        context.run { _: World, _: BlockPos ->
            dropInventory(
                player,
                inventory
            )
        }
    }

    override fun onButtonClick(player: PlayerEntity, id: Int): Boolean {

    }

    init{
        //add the properties for the three enchantment bars
        addProperty(seed).set(playerInventory.player.enchantmentTableSeed)
        addProperty(activeItem).set(-1)
        addProperty(Property.create(this.enchantmentId, 0))
        addProperty(Property.create(this.enchantmentId, 1))
        addProperty(Property.create(this.enchantmentId, 2))
        addProperty(Property.create(this.enchantmentLevel, 0))
        addProperty(Property.create(this.enchantmentLevel, 1))
        addProperty(Property.create(this.enchantmentLevel, 2))
        addProperty(Property.create(this.disenchantCost, 0))
        if (context != ScreenHandlerContext.EMPTY) {
            context.run { world: World, pos: BlockPos ->
                val pillarPairs = checkPillars(world, pos) / 2
                if (pillarPairs == 4 && player is ServerPlayerEntity){
                    RegisterCriteria.DISENCHANTING_PILLARS.trigger(player)
                }
            }
        }
    }


}
