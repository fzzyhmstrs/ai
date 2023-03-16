package me.fzzyhmstrs.amethyst_imbuement.screen

import me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys
import me.fzzyhmstrs.amethyst_core.modifier_util.ModifierHelper
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.*
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
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.Registries
import net.minecraft.screen.Property
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import java.util.*
import kotlin.math.min

@Suppress("SENSELESS_COMPARISON", "unused", "UnnecessaryVariable")
class SpellcastersFocusScreenHandler(
    syncID: Int,
    playerInventory: PlayerInventory,
    private val stack: ItemStack,
    private val context: ScreenHandlerContext
):  ScreenHandler(RegisterHandler.SPELLCASTERS_FOCUS_SCREEN_HANDLER, syncID) {

    constructor(syncID: Int, playerInventory: PlayerInventory, buf: PacketByteBuf) : this(
        syncID,
        playerInventory,
        playerInventory.getStack(buf.readByte().toInt()),
        ScreenHandlerContext.EMPTY,
    )
    
    private val player = playerInventory.player
    internal var option1: List<Identifier> = listOf()
    internal var option2: List<Identifier> = listOf()
    internal var option3: List<Identifier> = listOf()
    private var failed = false

    override fun canUse(player: PlayerEntity): Boolean {
        return !failed
    }

    override fun onButtonClick(player: PlayerEntity, id: Int): Boolean {
        when (id){
            0 ->{
                ModifierHelper.addRolledModifiers(stack,option1)
                context.run{world: World, pos: BlockPos ->
                    world.playSound(
                            null,
                            pos,
                            SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                            SoundCategory.BLOCKS,
                            1.0f,
                            world.random.nextFloat() * 0.1f + 0.9f
                        )
                }
                stack.nbt?.putBoolean(RegisterItem.SPELLCASTERS_FOCUS.LEVEL_UP_READY,false)
                stack.nbt?.remove(RegisterItem.SPELLCASTERS_FOCUS.LEVEL_UP)
                this.close(player)
                return true
            }
            1 ->{
                ModifierHelper.addRolledModifiers(stack,option2)
                context.run{world: World, pos: BlockPos ->
                    world.playSound(
                            null,
                            pos,
                            SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                            SoundCategory.BLOCKS,
                            1.0f,
                            world.random.nextFloat() * 0.1f + 0.9f
                        )
                }
                stack.nbt?.putBoolean(RegisterItem.SPELLCASTERS_FOCUS.LEVEL_UP_READY,false)
                stack.nbt?.remove(RegisterItem.SPELLCASTERS_FOCUS.LEVEL_UP)
                this.close(player)
                return true
            }
            2 ->{
                ModifierHelper.addRolledModifiers(stack,option3)
                context.run{world: World, pos: BlockPos ->
                    world.playSound(
                            null,
                            pos,
                            SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                            SoundCategory.BLOCKS,
                            1.0f,
                            world.random.nextFloat() * 0.1f + 0.9f
                        )
                }
                stack.nbt?.putBoolean(RegisterItem.SPELLCASTERS_FOCUS.LEVEL_UP_READY,false)
                stack.nbt?.remove(RegisterItem.SPELLCASTERS_FOCUS.LEVEL_UP)
                this.close(player)
                return true
            }
            else -> {}
        }
        return false
    }

    override fun quickMove(player: PlayerEntity?, slot: Int): ItemStack {
        return ItemStack.EMPTY
    }

    init{
        val nbt = stack.nbt
        if (nbt == null){
            failed = true
        }
        val lvlUpNbt = nbt?.get(RegisterItem.SPELLCASTERS_FOCUS.LEVEL_UP)
        if (lvlUpNbt == null){
            failed = true
        } else {
            val list1 = nbt.getList(RegisterItem.SPELLCASTERS_FOCUS.OPTION_1,8)
            val list2 = nbt.getList(RegisterItem.SPELLCASTERS_FOCUS.OPTION_2,8)
            val list3 = nbt.getList(RegisterItem.SPELLCASTERS_FOCUS.OPTION_3,8)
            option1 = list1.stream().map { Identifier(it.asString()) }.toList()
            option2 = list2.stream().map { Identifier(it.asString()) }.toList()
            option3 = list3.stream().map { Identifier(it.asString()) }.toList()
        }
    }


}
