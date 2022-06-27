package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.item_util.ManaItem
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.base_augments.MiscAugment
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World
import kotlin.math.max
import kotlin.math.min

@Suppress("SpellCheckingInspection")
class MendEquipmentAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier, maxLvl, *slot),
    ManaItem {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(0,15,0)

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        if (user !is PlayerEntity) return false
        val stacks: MutableList<ItemStack> = mutableListOf()
        for (stack2 in user.inventory.main){
            if (stack2.item !is ManaItem && stack2.isDamaged){
                stacks.add(stack2)
            }
        }
        for (stack2 in user.armorItems){
            if (stack2.item !is ManaItem && stack2.isDamaged){
                stacks.add(stack2)
            }
        }
        if (user.offHandStack.item !is ManaItem && user.offHandStack.isDamaged){
            stacks.add(user.offHandStack)
        }
        if (stacks.isEmpty()) return false
        val healLeft = effect.duration(level)
        val leftOverHeal = healItems(stacks,world,healLeft)
        world.playSound(null,user.blockPos,soundEvent(),SoundCategory.NEUTRAL,0.5f,1.0f)
        return (leftOverHeal < healLeft)
    }

    private fun healItems(list: MutableList<ItemStack>,world: World, healLeft: Int): Int{
        var hl = healLeft
        if (hl <= 0 || list.isEmpty()) return max(0,hl)
        val rnd = world.random.nextInt(list.size)
        val stack = list[rnd]
        val healAmount = min(5,hl)
        val healedAmount = healDamage(healAmount,stack)
        hl -= min(healAmount,healedAmount)
        if (!stack.isDamaged){
            list.remove(stack)
        }
        return healItems(list,world,hl)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_ANVIL_USE
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE,14,3,1,imbueLevel,LoreTier.NO_TIER, Items.IRON_INGOT)
    }

}