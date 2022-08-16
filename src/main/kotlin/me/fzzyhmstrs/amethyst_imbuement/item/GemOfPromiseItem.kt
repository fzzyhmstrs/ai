package me.fzzyhmstrs.amethyst_imbuement.item

import net.minecraft.entity.Entity
import net.minecraft.entity.damage.DamageRecord
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class GemOfPromiseItem(settings: Settings): Item(settings) {

    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        if (entity !is PlayerEntity) return
        if(!PlayerInventory.isValidHotbarIndex(slot)) return
        super.inventoryTick(stack, world, entity, slot, selected)
        val record = entity.damageTracker.mostRecentDamage
    }

    fun sparkingGemCheck(stack: ItemStack, world: World, playerEntity: PlayerEntity, record: DamageRecord){

    }

    fun blazingGemCheck(stack: ItemStack, world: World, playerEntity: PlayerEntity, record: DamageRecord){

    }


    companion object{
        fun incrementKillCount(stack: ItemStack, inventory: PlayerInventory){

        }
    }

}