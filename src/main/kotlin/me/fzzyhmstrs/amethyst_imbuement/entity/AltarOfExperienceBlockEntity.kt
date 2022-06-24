package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.util.NbtKeys
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.screen.PropertyDelegate
import net.minecraft.text.Text
import net.minecraft.util.Nameable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import kotlin.math.atan2
import kotlin.math.roundToInt


@Suppress("UNUSED_PARAMETER")
class AltarOfExperienceBlockEntity(pos: BlockPos, state: BlockState): BlockEntity(RegisterEntity.ALTAR_OF_EXPERIENCE_BLOCK_ENTITY,pos, state),Nameable {

    var ticks = 0
    private var customName: Text? = null
    private var storedXp = 0

    val propertyDelegate: PropertyDelegate = object : PropertyDelegate {
        override fun get(index: Int): Int {
            return storedXp
        }

        override fun set(index: Int, value: Int) {
            storedXp = value
            markDirty()
        }

        //this is supposed to return the amount of integers you have in your delegate, in our example only one
        override fun size(): Int {
            return 1
        }
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        if (hasCustomName()) {
            nbt.putString("CustomName", Text.Serializer.toJson(customName))
        }
        nbt.putInt(NbtKeys.ALTAR_KEY.str(),storedXp)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        if (nbt.contains("CustomName", 8)) {
            customName = Text.Serializer.fromJson(nbt.getString("CustomName"))
        }
        if (nbt.contains(NbtKeys.ALTAR_KEY.str())) {
            storedXp = nbt.getInt(NbtKeys.ALTAR_KEY.str())
        }
    }


        //private val RANDOM = Random()
        var lookingRotR = 0f
        private var turningSpeedR = 2f

        private fun rotClamp(clampTo: Int, value: Float): Float {
            return if (value >= clampTo) {
                value - clampTo
            } else if (value < 0) {
                value + clampTo
            } else {
                value
            }
        }

        private fun checkBound(amount: Int, rotBase: Float): Boolean {
            val rot = rotBase.roundToInt().toFloat()
            val rot2 = rotClamp(360, rot + 180)
            return rot - amount <= lookingRotR && lookingRotR <= rot + amount || rot2 - amount <= lookingRotR && lookingRotR <= rot2 + amount
        }

        private fun moveOnTickR(rot: Float) {
            if (!checkBound(2, rot)) {
                val check = ((rotClamp(180, rot) - rotClamp(180, lookingRotR) + 180) % 180).toDouble()
                if (check < 90) {
                    lookingRotR += turningSpeedR
                } else {
                    lookingRotR -= turningSpeedR
                }
                lookingRotR = rotClamp(360, lookingRotR)
                if (checkBound(10, rot)) {
                    turningSpeedR = 2f
                } else {
                    turningSpeedR += 1f
                    turningSpeedR = MathHelper.clamp(turningSpeedR, 2f, 20f)
                }
            }
        }
        companion object {
            fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: AltarOfExperienceBlockEntity) {
                //println("ticking")
                val closestPlayer: PlayerEntity? = world.getClosestPlayer(
                    pos.x.toDouble() + 0.5,
                    pos.y.toDouble() + 0.5,
                    pos.z.toDouble() + 0.5,
                    3.0,
                    false
                )
                if (closestPlayer != null) {
                    val x: Double = closestPlayer.x - pos.x.toDouble() - 0.5
                    val z: Double = closestPlayer.z - pos.z.toDouble() - 0.5
                    val rotY = (atan2(z, x).toFloat() / Math.PI * 180 + 180).toFloat()
                    blockEntity.moveOnTickR(rotY)
                } else {
                    blockEntity.lookingRotR += 2
                }
                blockEntity.ticks++
                if (blockEntity.ticks >= 360){
                    blockEntity.ticks = 0
                }
                blockEntity.lookingRotR = blockEntity.rotClamp(360, blockEntity.lookingRotR)
            }
        }


    override fun getName(): Text? {
        return if (customName != null) {
            customName
        } else Text.translatable("container.altar_of_experience")
    }

    fun setCustomName(value: Text?) {
        customName = value
    }

    override fun getCustomName(): Text? {
        return customName
    }
}