package me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments

import me.fzzyhmstrs.amethyst_imbuement.util.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.util.AcceptableItemStacks
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

abstract class ScepterAugment(weight: Rarity, _tier: Int, _maxLvl: Int, target: EnchantmentTarget, vararg slot: EquipmentSlot): Enchantment(weight, target,slot) {
    private val maxLvl = _maxLvl
    private val tier = _tier

    open fun needsClient(): Boolean{
        return false
    }

    open fun clientTask(world: World, target: Entity?, user: LivingEntity, level: Int, hit: HitResult?){

    }

    open fun entityTask(world: World, target: Entity, user: LivingEntity, level: Double, hit: HitResult?){

    }

    override fun getMinPower(level: Int): Int {
        return 150000
    }

    override fun getMaxPower(level: Int): Int {
        return 155000
    }

    override fun getMaxLevel(): Int {
        return 1
    }

    open fun getAugmentMaxLevel(): Int{
        return maxLvl
    }

    override fun isTreasure(): Boolean {
        return true
    }

    override fun isAvailableForEnchantedBookOffer(): Boolean {
        return false
    }

    override fun isAvailableForRandomSelection(): Boolean {
        return false
    }

    fun isAcceptableScepterItem(stack: ItemStack, player: PlayerEntity): Boolean {
        val nbt = stack.orCreateNbt
        return ScepterObject.checkScepterStat(
            nbt,
            Registry.ENCHANTMENT.getId(this)?.path ?: ""
        ) || player.abilities.creativeMode
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return when (tier) {
            1 -> (stack.isOf(RegisterItem.OPALINE_SCEPTER) || stack.isOf(RegisterItem.IRIDESCENT_SCEPTER) || stack.isOf(
                RegisterItem.LUSTROUS_SCEPTER))
            2 -> (stack.isOf(RegisterItem.IRIDESCENT_SCEPTER) || stack.isOf(RegisterItem.LUSTROUS_SCEPTER))
            3 -> stack.isOf(RegisterItem.LUSTROUS_SCEPTER)
            else -> {
                false
            }
        }
    }

    open fun acceptableItemStacks(): MutableList<ItemStack> {
        return AcceptableItemStacks.scepterAcceptableItemStacks(tier)
    }

    abstract fun applyTasks(world: World, user: LivingEntity,hand: Hand, level: Int): Boolean
}