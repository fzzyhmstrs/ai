package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterToolMaterial
import me.fzzyhmstrs.amethyst_imbuement.LOGGER
import net.minecraft.block.Blocks
import net.minecraft.item.CompassItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtHelper
import net.minecraft.nbt.NbtOps
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World

class SojournScepterItem(material: ScepterToolMaterial, settings: Settings): CustomScepterItem(material, settings) {


    override fun useOnBlock(context: ItemUsageContext): ActionResult? {
        val blockPos = context.blockPos
        val world = context.world
        if (world.getBlockState(blockPos).isOf(Blocks.LODESTONE)) {
            val bl: Boolean
            world.playSound(null, blockPos, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0f, 1.0f)
            val playerEntity = context.player
            val itemStack = context.stack
            bl = !playerEntity!!.abilities.creativeMode && itemStack.count == 1
            if (bl) {
                this.writeNbt(world.registryKey, blockPos, itemStack.orCreateNbt)
            } else {
                val itemStack2 = ItemStack(Items.COMPASS, 1)
                val nbtCompound = if (itemStack.hasNbt()) itemStack.nbt!!.copy() else NbtCompound()
                itemStack2.nbt = nbtCompound
                if (!playerEntity.abilities.creativeMode) {
                    itemStack.decrement(1)
                }
                this.writeNbt(world.registryKey, blockPos, nbtCompound)
                if (!playerEntity.inventory.insertStack(itemStack2)) {
                    playerEntity.dropItem(itemStack2, false)
                }
            }
            return ActionResult.success(world.isClient)
        }
        return super.useOnBlock(context)
    }

    private fun writeNbt(worldKey: RegistryKey<World>, pos: BlockPos, nbt: NbtCompound) {
        nbt.put(CompassItem.LODESTONE_POS_KEY, NbtHelper.fromBlockPos(pos))
        World.CODEC.encodeStart(NbtOps.INSTANCE, worldKey).resultOrPartial { s: String? ->
            LOGGER.warning(
                s
            )
        }.ifPresent { nbtElement: NbtElement? ->
            nbt.put(
                CompassItem.LODESTONE_DIMENSION_KEY,
                nbtElement
            )
        }
        nbt.putBoolean(CompassItem.LODESTONE_TRACKED_KEY, true)
    }
}