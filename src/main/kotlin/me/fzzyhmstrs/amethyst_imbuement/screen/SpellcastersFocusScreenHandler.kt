package me.fzzyhmstrs.amethyst_imbuement.screen

import me.fzzyhmstrs.amethyst_core.modifier_util.ModifierHelper
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterHandler
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

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
    internal val options: Array<List<Identifier>> = arrayOf(listOf(),listOf(),listOf())
    private var failed = false

    override fun canUse(player: PlayerEntity): Boolean {
        return !failed
    }

    override fun onButtonClick(player: PlayerEntity, id: Int): Boolean {
        when (id){
            0 ->{
                ModifierHelper.addRolledModifiers(stack,options[0])
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
                val nbt = stack.orCreateNbt
                nbt.putBoolean(RegisterItem.SPELLCASTERS_FOCUS.LEVEL_UP_READY,false)
                val lvlUpNbt = nbt.getCompound(RegisterItem.SPELLCASTERS_FOCUS.LEVEL_UP)
                if (!lvlUpNbt.isEmpty){
                    nbt.put(RegisterItem.SPELLCASTERS_FOCUS.CHOSEN_OPTION,lvlUpNbt.getList(RegisterItem.SPELLCASTERS_FOCUS.OPTION_1,8))
                }
                player.closeHandledScreen()
                return true
            }
            1 ->{
                ModifierHelper.addRolledModifiers(stack,options[1])
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
                val nbt = stack.orCreateNbt
                nbt.putBoolean(RegisterItem.SPELLCASTERS_FOCUS.LEVEL_UP_READY,false)
                val lvlUpNbt = nbt.getCompound(RegisterItem.SPELLCASTERS_FOCUS.LEVEL_UP)
                if (!lvlUpNbt.isEmpty){
                    nbt.put(RegisterItem.SPELLCASTERS_FOCUS.CHOSEN_OPTION,lvlUpNbt.getList(RegisterItem.SPELLCASTERS_FOCUS.OPTION_2,8))
                }
                player.closeHandledScreen()
                return true
            }
            2 ->{
                ModifierHelper.addRolledModifiers(stack,options[2])
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
                val nbt = stack.orCreateNbt
                nbt.putBoolean(RegisterItem.SPELLCASTERS_FOCUS.LEVEL_UP_READY,false)
                val lvlUpNbt = nbt.getCompound(RegisterItem.SPELLCASTERS_FOCUS.LEVEL_UP)
                if (!lvlUpNbt.isEmpty){
                    nbt.put(RegisterItem.SPELLCASTERS_FOCUS.CHOSEN_OPTION,lvlUpNbt.getList(RegisterItem.SPELLCASTERS_FOCUS.OPTION_3,8))
                }
                player.closeHandledScreen()
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
        val lvlUpNbt = nbt?.getCompound(RegisterItem.SPELLCASTERS_FOCUS.LEVEL_UP)
        if (lvlUpNbt == null){
            failed = true
        } else {
            val list1 = lvlUpNbt.getList(RegisterItem.SPELLCASTERS_FOCUS.OPTION_1,8)
            val list2 = lvlUpNbt.getList(RegisterItem.SPELLCASTERS_FOCUS.OPTION_2,8)
            val list3 = lvlUpNbt.getList(RegisterItem.SPELLCASTERS_FOCUS.OPTION_3,8)
            options[0] = list1.stream().map { Identifier(it.asString()) }.toList()
            options[1] = list2.stream().map { Identifier(it.asString()) }.toList()
            options[2] = list3.stream().map { Identifier(it.asString()) }.toList()
        }
    }


}
