package me.fzzyhmstrs.amethyst_imbuement.entity.block

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.block.BlockState
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.ScreenHandler
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Nameable
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos

class WitchesBookshelfBlockEntity(pos: BlockPos, state: BlockState): LootableContainerBlockEntity(RegisterEntity.IMBUING_TABLE_BLOCK_ENTITY,pos, state),Nameable {

    private var customName: Text? = null
    var inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);

    override fun readNbt(nbt: NbtCompound?) {
        super.readNbt(nbt)
        inventory = DefaultedList.ofSize(size(), ItemStack.EMPTY)
        if (!deserializeLootTable(nbt)) {
            Inventories.readNbt(nbt, inventory)
        }
    }

    override fun writeNbt(nbt: NbtCompound?) {
        super.writeNbt(nbt)
        if (!serializeLootTable(nbt)) {
            Inventories.writeNbt(nbt, inventory)
        }
    }

    override fun size(): Int {
        return 9
    }

    override fun getName(): Text? {
        return if (customName != null) {
            customName
        } else AcText.translatable("container.witches_bookshelf")
    }

    override fun getCustomName(): Text? {
        return customName
    }

    override fun setCustomName(value: Text?) {
        customName = value
    }

    override fun getContainerName(): Text {
        return name ?: AcText.translatable("container.witches_bookshelf")
    }

    override fun createScreenHandler(syncId: Int, playerInventory: PlayerInventory): ScreenHandler {
        return GenericContainerScreenHandler.createGeneric9x1(syncId, playerInventory)
    }

    override fun getInvStackList(): DefaultedList<ItemStack> {
        return inventory
    }

    override fun setInvStackList(list: DefaultedList<ItemStack>?) {
        this.inventory = list
    }

    override fun onOpen(player: PlayerEntity) {
        player.world.playSound(null,player.blockPos,SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS,1f,1f)
    }

    override fun onClose(player: PlayerEntity) {
        player.world.playSound(null,player.blockPos,SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS,1f,1f)
    }

}