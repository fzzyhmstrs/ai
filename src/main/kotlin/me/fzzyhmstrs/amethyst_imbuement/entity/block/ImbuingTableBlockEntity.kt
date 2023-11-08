package me.fzzyhmstrs.amethyst_imbuement.entity.block

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.nbt_util.Nbt
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.text.Text
import net.minecraft.util.Nameable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import java.util.*

@Suppress("UNUSED_PARAMETER", "PropertyName")
class ImbuingTableBlockEntity(pos: BlockPos, state: BlockState): BlockEntity(RegisterEntity.IMBUING_TABLE_BLOCK_ENTITY,pos, state),Nameable {

    var ticks = 0
    var nextPageAngle = 0f
    var pageAngle = 0f
    var field_11969 = 0f
    var field_11967 = 0f
    var nextPageTurningSpeed = 0f
    var pageTurningSpeed = 0f
    var field_11964 = 0f
    var field_11963 = 0f
    var field_11962 = 0f
    private var customName: Text? = null
    val inventory = ImbuingInventory(13, this)
    var inUse: Boolean = false
    var inUseUuid: UUID = UUID(0L,0L)

    fun setInUse(uuid: UUID){
        if (!inUse){
            inUseUuid = uuid
            inUse = true
            //logger.info("block set as in-use by $uuid")
        }
    }

    fun clearInUse(uuid: UUID){
        if (uuid == inUseUuid){
            inUse = false
            inUseUuid = UUID(0L,0L)
            //logger.info("block cleared for future use by $uuid")
        }
    }

    fun checkCanUse(uuid: UUID): Boolean{
        return uuid == inUseUuid
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        if (hasCustomName()) {
            nbt.putString("CustomName", Text.Serializer.toJson(customName))
        }
        val list = inventory.toNbtList()
        nbt.put("inventory",list)
        nbt.putBoolean("inUse",inUse)
        nbt.putUuid("inUseUuid", inUseUuid)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        if (nbt.contains("CustomName", 8)) {
            customName = Text.Serializer.fromJson(nbt.getString("CustomName"))
        }
        val list = Nbt.readNbtList(nbt,"inventory")
        inventory.readNbtList(list)
        if (nbt.contains("inUse")) {
            inUse = nbt.getBoolean("inUse")
        }
        if (nbt.contains("inUseUuid")) {
            inUseUuid = nbt.getUuid("inUseUuid")
        }
    }

    companion object {
        private val RANDOM = Random()
        fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: ImbuingTableBlockEntity) {
            blockEntity.pageTurningSpeed = blockEntity.nextPageTurningSpeed
            blockEntity.field_11963 = blockEntity.field_11964
            val playerEntity =
                world.getClosestPlayer(pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, 3.0, false)
            if (playerEntity != null) {
                val d2 = playerEntity.x - (pos.x.toDouble() + 0.5)
                val e = playerEntity.z - (pos.z.toDouble() + 0.5)
                blockEntity.field_11962 = MathHelper.atan2(e, d2).toFloat()
                blockEntity.nextPageTurningSpeed += 0.1f
                if (blockEntity.nextPageTurningSpeed < 0.5f || RANDOM.nextInt(40) == 0) {
                    val f = blockEntity.field_11969
                    do {
                        blockEntity.field_11969 += (RANDOM.nextInt(4) - RANDOM.nextInt(4)).toFloat()
                    } while (f == blockEntity.field_11969)
                }
            } else {
                blockEntity.field_11962 += 0.02f
                blockEntity.nextPageTurningSpeed -= 0.1f
            }
            while (blockEntity.field_11964 >= Math.PI.toFloat()) {
                blockEntity.field_11964 -= Math.PI.toFloat() * 2
            }
            while (blockEntity.field_11964 < (-Math.PI).toFloat()) {
                blockEntity.field_11964 += Math.PI.toFloat() * 2
            }
            while (blockEntity.field_11962 >= Math.PI.toFloat()) {
                blockEntity.field_11962 -= Math.PI.toFloat() * 2
            }
            while (blockEntity.field_11962 < (-Math.PI).toFloat()) {
                blockEntity.field_11962 += Math.PI.toFloat() * 2
            }
            var d: Float = blockEntity.field_11962 - blockEntity.field_11964
            while (d >= Math.PI.toFloat()) {
                d -= Math.PI.toFloat() * 2
            }
            while (d < (-Math.PI).toFloat()) {
                d += Math.PI.toFloat() * 2
            }
            blockEntity.field_11964 += d * 0.4f
            blockEntity.nextPageTurningSpeed = MathHelper.clamp(blockEntity.nextPageTurningSpeed, 0.0f, 1.0f)
            ++blockEntity.ticks
            blockEntity.pageAngle = blockEntity.nextPageAngle
            var g = (blockEntity.field_11969 - blockEntity.nextPageAngle) * 0.4f
            g = MathHelper.clamp(g, -0.2f, 0.2f)
            blockEntity.field_11967 += (g - blockEntity.field_11967) * 0.9f
            blockEntity.nextPageAngle += blockEntity.field_11967
        }

    }

    class ImbuingInventory(size: Int, private val blockEntity: ImbuingTableBlockEntity?): SimpleInventory(size){
        override fun readNbtList(nbtList: NbtList) {
            for (i in 0 until nbtList.size){
                val compound = nbtList.getCompound(i)
                val stack = ItemStack.fromNbt(compound)
                if (stack.isEmpty) continue
                val slot = compound.getInt("slot")
                this.setStack(slot,stack)
            }
        }

        override fun toNbtList(): NbtList {
            val list = NbtList()
            for (i in 0 until this.size()){
                val compound = NbtCompound()
                compound.putInt("slot",i)
                val stack = this.getStack(i)
                stack.writeNbt(compound)
                list.add(compound)
            }
            return list
        }

        private var dirtyChecked: Boolean = false
        override fun markDirty() {
            if (dirtyChecked) {
                dirtyChecked = false
                return
            }
            //Exception().printStackTrace()
            dirtyChecked = true
            super.markDirty()
            blockEntity?.markDirty()
        }
    }

    override fun getName(): Text? {
        return if (customName != null) {
            customName
        } else AcText.translatable("container.imbuing_table")
    }

    fun setCustomName(value: Text?) {
        customName = value
    }

    override fun getCustomName(): Text? {
        return customName
    }
}