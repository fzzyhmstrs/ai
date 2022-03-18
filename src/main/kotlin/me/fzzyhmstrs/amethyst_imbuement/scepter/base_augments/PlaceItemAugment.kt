package me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments

import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem
import me.fzzyhmstrs.amethyst_imbuement.util.ScepterObject
import net.minecraft.client.MinecraftClient
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

open class PlaceItemAugment(weight: Rarity, tier: Int, maxLvl: Int,item: Item, vararg slot: EquipmentSlot): ScepterAugment(weight,tier,maxLvl, EnchantmentTarget.WEAPON, *slot) {
    private val _item = item

    override fun applyTasks(world: World, user: LivingEntity, hand: Hand, level: Int): Boolean {
        if (user !is PlayerEntity) return false
        val hit = MinecraftClient.getInstance().crosshairTarget ?: return false
        if (hit.type != HitResult.Type.BLOCK) return false
        when (val testItem = itemToPlace(world,user).item) {
            is BlockItem -> {
                testItem.place(ItemPlacementContext(user, hand, ItemStack(testItem),hit as BlockHitResult))
                world.playSound(null,user.blockPos,soundEvent(), SoundCategory.NEUTRAL,1.0f,1.0f)
                if (needsClient()) ScepterObject.addClientTaskToQueue(this,ScepterItem.ClientTaskInstance(null, level, hit))
                return true
            }
            is BucketItem -> {
                return if (testItem.placeFluid(user,world,(hit as BlockHitResult).blockPos,hit)) {
                    world.playSound(null,user.blockPos,soundEvent(), SoundCategory.NEUTRAL,1.0f,1.0f)
                    if (needsClient()) ScepterObject.addClientTaskToQueue(this,ScepterItem.ClientTaskInstance(null, level, hit))
                    true
                } else {
                    false
                }
            }
            else -> {
                return false
            }
        }
    }

    open fun itemToPlace(world: World, user: LivingEntity): ItemStack {
        return ItemStack(_item)
    }

    open fun soundEvent(): SoundEvent{
        return SoundEvents.BLOCK_WOOD_PLACE
    }

}