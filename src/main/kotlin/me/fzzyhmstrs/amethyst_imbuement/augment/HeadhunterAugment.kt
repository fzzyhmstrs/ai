package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_core.trinket_util.BaseAugment
import me.fzzyhmstrs.amethyst_imbuement.item.SniperBowItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ItemStack
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.math.tan

class HeadhunterAugment(weight: Rarity,mxLvl: Int = 1, vararg slot: EquipmentSlot): BaseAugment(weight, mxLvl,EnchantmentTarget.CROSSBOW, *slot) {

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.item is SniperBowItem)
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        val list = mutableListOf<ItemStack>()
        list.add(ItemStack(RegisterItem.SNIPER_BOW,1))
        return list
    }

    @Suppress("SpellCheckingInspection")
    companion object {
        fun checkHeadhunterHit(target: Entity, arrow: PersistentProjectileEntity, amount: Float): Float {
            val y1: Double = arrow.y
            val y2: Double = target.eyeY

            val xdiff: Double = arrow.x - target.x
            val zdiff: Double = arrow.z - target.z
            var sqrd = xdiff * xdiff + zdiff * zdiff
            if (sqrd < 0.0) sqrd = 0.0
            val d = sqrt(sqrd)
            val y1Fix: Double = if (abs(arrow.pitch) != 90f) {
                y1 + d * tan(arrow.pitch * 0.01745329)
            } else if (arrow.pitch.toDouble() == 90.0) {
                return amount
            } else if (arrow.pitch.toDouble() == -90.0) {
                val rndMultiplier: Float = target.world.random.nextFloat() * 0.7f + 0.6f
                return amount * (1.0f + rndMultiplier)
            } else {
                y1
            }

            if (abs(y1Fix - y2) < 0.03) {
                return amount * 4.2f
            } else if (abs(y1Fix - y2) < 0.14) {
                val rndMultiplier: Float = target.world.random.nextFloat() * 0.7f + 0.6f
                return amount * (1.0f + rndMultiplier)
            }
            return amount
        }
    }

}