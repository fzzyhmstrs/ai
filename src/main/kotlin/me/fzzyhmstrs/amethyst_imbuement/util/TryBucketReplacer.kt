package me.fzzyhmstrs.amethyst_imbuement.util

import net.minecraft.advancement.criterion.Criteria
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Bucketable
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsage
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import java.util.*

object TryBucketReplacer {

    fun tryBucket(player: PlayerEntity, hand: Hand, entity: LivingEntity): Optional<ActionResult> {
        val itemStack = player.getStackInHand(hand)
        if (itemStack.item === Items.WATER_BUCKET && entity.isAlive) {
            entity.playSound((entity as Bucketable).bucketedSound, 1.0f, 1.0f)
            val itemStack2 = (entity as Bucketable).bucketItem
            (entity as Bucketable).copyDataToStack(itemStack2)
            val itemStack3 = exchangeInfinityStacks(itemStack, player, itemStack2)
            player.setStackInHand(hand, itemStack3)
            val world = entity.world
            if (!world.isClient) {
                Criteria.FILLED_BUCKET.trigger(player as ServerPlayerEntity, itemStack2)
            }
            entity.discard()
            return Optional.of(ActionResult.success(world.isClient))
        }
        return Optional.empty()
    }

    private fun exchangeInfinityStacks(
        inputStack: ItemStack,
        player: PlayerEntity,
        outputStack: ItemStack
    ): ItemStack {
        val tempStack = ItemUsage.exchangeStack(inputStack, player, outputStack, false)
        val map: MutableMap<Enchantment, Int> = HashMap()
        map[Enchantments.INFINITY] = 1
        EnchantmentHelper.set(map, tempStack)
        return tempStack
    }
}