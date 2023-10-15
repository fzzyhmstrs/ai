package me.fzzyhmstrs.amethyst_imbuement.entity.block



import me.fzzyhmstrs.amethyst_imbuement.item.GlisteringKeyItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.block.entity.ChestLidAnimator
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class GildedLockboxBlockEntity(blockEntityType: BlockEntityType<*>, blockPos: BlockPos, blockState: BlockState) :
    ChestBlockEntity(blockEntityType, blockPos, blockState), GlisteringKeyItem.GlisteringKeyUnlockable {

    constructor(blockPos: BlockPos, blockState: BlockState): this(RegisterEntity.GILDED_LOCKBOX_BLOCK_ENTITY, blockPos, blockState)

    internal var unlocked = false
    private val lidAnimator = ChestLidAnimator()

    override fun onSyncedBlockEvent(type: Int, data: Int): Boolean {
        if (type == 1) {
            this.lidAnimator.setOpen(data > 0)
            return true
        }
        return super.onSyncedBlockEvent(type, data)
    }

    override fun getAnimationProgress(tickDelta: Float): Float {
        return this.lidAnimator.getProgress(tickDelta)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        unlocked = nbt.getBoolean("glistering_unlocked")
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        nbt.putBoolean("glistering_unlocked", unlocked)
    }

    override fun unlock(world: World, blockPos: BlockPos, stack: ItemStack?) {
        unlocked = true
    }

    override fun consumeItem(): Boolean {
        return true
    }

    companion object{
        fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: GildedLockboxBlockEntity){
            blockEntity.lidAnimator.step()
        }
    }


}