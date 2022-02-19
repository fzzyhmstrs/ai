package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_imbuement.util.NbtKeys
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.screen.PropertyDelegate
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Nameable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import kotlin.math.atan2
import kotlin.math.roundToInt


@Suppress("UNUSED_PARAMETER")
class AltarOfExperienceBlockEntity(pos: BlockPos, state: BlockState): BlockEntity(RegisterEntity.ALTAR_OF_EXPERIENCE_BLOCK_ENTITY,pos, state),Nameable {

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

                /*blockEntity.pageTurningSpeed = blockEntity.nextPageTurningSpeed
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
            blockEntity.nextPageAngle += blockEntity.field_11967*/
            }
        }


    override fun getName(): Text? {
        return if (customName != null) {
            customName
        } else TranslatableText("container.altar_of_experience")
    }

    fun setCustomName(value: Text?) {
        customName = value
    }

    override fun getCustomName(): Text? {
        return customName
    }
}