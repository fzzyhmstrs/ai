package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.PassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.DraconicBoxEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTag
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.block.Blocks
import net.minecraft.block.ExperienceDroppingBlock
import net.minecraft.block.RedstoneOreBlock
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class DraconicVisionAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): PassiveAugment(weight,mxLvl, *slot) {

    override fun tickEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        val world = user.world
        val pos = user.blockPos
        val posI = pos.x
        val posJ = pos.y
        val posK = pos.z
        val range = rangeOfEffect()
        for (i in -range..range){
            for (j in -range..range){
                for (k in -range..range){
                    val ii = posI + i
                    val jj = posJ + j
                    val kk = posK + k
                    val bp = pos.add(i,j,k)
                    if (world.isAir(bp)) continue
                    val bs = world.getBlockState(bp)
                    if (bs.block is ExperienceDroppingBlock || bs.isOf(Blocks.ANCIENT_DEBRIS) || bs.block is RedstoneOreBlock){
                        if (!extendBoxLife(bp, world)) {
                            val dbe = DraconicBoxEntity(RegisterEntity.DRACONIC_BOX_ENTITY, world, bs.block, 40, bp)
                            dbe.setPosition(ii + 0.5, jj + 0.1, kk + 0.5)
                            addBoxToMap(bp,dbe.id)
                            world.spawnEntity(dbe)
                        }
                    }
                }
            }
        }
        EffectQueue.addStatusToQueue(user, RegisterStatus.DRACONIC_VISION,260,0)
        world.playSound(null,pos, SoundEvents.BLOCK_CONDUIT_AMBIENT_SHORT, SoundCategory.NEUTRAL,0.3f,0.8f)
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return stack.isIn(RegisterTag.HEADBANDS_TAG)
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        return Registries.ITEM.iterateEntries(RegisterTag.HEADBANDS_TAG).map { it.value().defaultStack }.toMutableList()
    }

    private fun rangeOfEffect(): Int{
        return AiConfig.trinkets.draconicVisionRange.get()
    }

    companion object {
        private val boxPositions: MutableMap<BlockPos,Int> = mutableMapOf()

        private fun extendBoxLife(pos: BlockPos, world: World): Boolean{
            if (boxPositions.containsKey(pos)){
                val id = boxPositions[pos] ?: return false
                val box = world.getEntityById(id) ?: return false
                if (box !is DraconicBoxEntity) return false
                box.extendBoxLife(40)
                return true
            } else {
                return false
            }
        }

        private fun addBoxToMap(pos: BlockPos, id: Int){
            boxPositions[pos] = id
        }

        fun removeBoxFromMap(pos: BlockPos){
            if (boxPositions.containsKey(pos)){
                boxPositions.remove(pos)
            }
        }
    }
}