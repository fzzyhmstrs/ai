package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects

import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import net.minecraft.entity.Entity
import net.minecraft.item.BoneMealItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.math.Direction

object AbundanceEffect {

    fun grow(entity: Entity, owner: Entity?, processContext: ProcessContext){
        val stack = ItemStack(Items.BONE_MEAL)
        BoneMealItem.useOnGround(stack,entity.world,entity.blockPos.down(), Direction.UP)
    }

}