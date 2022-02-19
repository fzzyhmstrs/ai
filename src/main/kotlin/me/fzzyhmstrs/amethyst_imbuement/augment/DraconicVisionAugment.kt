package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.PassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.DraconicBoxEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import net.minecraft.block.OreBlock
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents

class DraconicVisionAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): PassiveAugment(weight,mxLvl, *slot) {

    override fun tickEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        val world = user.world
        if (user.hasStatusEffect(RegisterStatus.DRACONIC_VISION)) return
        val pos = user.blockPos
        val posI = pos.x
        val posJ = pos.y
        val posK = pos.z
        val range = RegisterEnchantment.DRACONIC_VISION.rangeOfEffect()
        for (i in -range..range){
            for (j in -range..range){
                for (k in -range..range){
                    val ii = posI + i
                    val jj = posJ + j
                    val kk = posK + k
                    if (world.isAir(pos.add(i,j,k))) continue
                    if (world.getBlockState(pos.add(i,j,k)).block is OreBlock){
                        val dbe = DraconicBoxEntity(RegisterEntity.DRACONIC_BOX_ENTITY,world)
                        dbe.setPosition(ii + 0.5,jj.toDouble(),kk + 0.5)
                        world.spawnEntity(dbe)
                    }
                }
            }
        }
        addStatusToQueue(user.uuid, RegisterStatus.DRACONIC_VISION,300,0)
        world.playSound(null,pos, SoundEvents.BLOCK_CONDUIT_AMBIENT_SHORT, SoundCategory.NEUTRAL,1.0f,0.8f)
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.isOf(RegisterItem.IMBUED_HEADBAND))
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        val list = mutableListOf<ItemStack>()
        list.add(ItemStack(RegisterItem.IMBUED_HEADBAND,1))
        return list
    }

    fun rangeOfEffect(): Int{
        return 8
    }
}