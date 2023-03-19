package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.item_util.AugmentScepterItem
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.world.World

class DebugAugment: MiscAugment(ScepterTier.ONE,1) {

    override fun effect(
        world: World,
        user: LivingEntity,
        entityList: MutableList<Entity>,
        level: Int,
        effect: AugmentEffect
    ): Boolean {
        val stack1 = user.getStackInHand(Hand.MAIN_HAND)
        val stack2 = user.getStackInHand(Hand.OFF_HAND)
        if (stack1.item is AugmentScepterItem) {
            val nbt = stack1.orCreateNbt
            writeNbt(SpellType.FURY.name+"_lvl", 25, nbt)
            writeNbt(SpellType.WIT.name+"_lvl", 25, nbt)
            writeNbt(SpellType.GRACE.name+"_lvl", 25, nbt)
        } else if (stack2.item is AugmentScepterItem) {
            val nbt = stack2.orCreateNbt
            writeNbt(SpellType.FURY.name+"_lvl", 25, nbt)
            writeNbt(SpellType.WIT.name+"_lvl", 25, nbt)
            writeNbt(SpellType.GRACE.name+"_lvl", 25, nbt)
        }
        return true
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE,1,1,
            1,imbueLevel,1, LoreTier.NO_TIER, Items.DEBUG_STICK)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH
    }

    private fun writeNbt(key: String, input: Int, nbt: NbtCompound){
        nbt.putInt(key,input)
    }
}