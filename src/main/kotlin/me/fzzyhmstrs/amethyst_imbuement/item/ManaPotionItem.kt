package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.coding_util.AcText
import me.fzzyhmstrs.amethyst_core.mana_util.ManaItem
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.PotionItem
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.stat.Stats
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import kotlin.math.max
import kotlin.math.min

class ManaPotionItem(settings: Settings) : PotionItem(settings) {

    override fun getDefaultStack(): ItemStack {
        return ItemStack(this)
    }

    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        if (user !is PlayerEntity) return stack
        if (user is ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger(user, stack)
        }
        val stacks: MutableList<ItemStack> = mutableListOf()
        for (stack2 in user.inventory.main){
            if (stack2.item is ManaItem && stack2.isDamaged){
                stacks.add(stack2)
            }
        } // iterate over the inventory and look for items that are interfaced with "ManaItem"
        val healLeft = 80
        val leftOverMana = manaHealItems(stacks,world,healLeft)
        if (leftOverMana > 0) {
            val baseXp: Int = (3 + world.random.nextInt(5) + world.random.nextInt(5)) * 2
            val manaFraction = leftOverMana.toFloat()/healLeft.toFloat()
            val fractionXp = max((baseXp * manaFraction).toInt(), 1)
            user.addExperience(fractionXp)
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this))
        if (!user.abilities.creativeMode) {
            stack.decrement(1)
            if (stack.isEmpty) {
                return ItemStack(Items.GLASS_BOTTLE)
            }
            user.inventory.insertStack(ItemStack(Items.GLASS_BOTTLE))
        }
        world.emitGameEvent(user as Entity, GameEvent.DRINK, user.eyePos)
        return stack
    }

    private fun manaHealItems(list: MutableList<ItemStack>,world: World, healLeft: Int): Int{
        var hl = healLeft
        if (hl <= 0 || list.isEmpty()) return max(0,hl)
        val rnd = world.random.nextInt(list.size)
        val stack = list[rnd]
        val healAmount = min(5,hl)
        val healedAmount = (stack.item as ManaItem).healDamage(healAmount,stack)
        hl -= min(healAmount,healedAmount)
        if (!stack.isDamaged){
            list.remove(stack)
        }
        return manaHealItems(list,world,hl)
    }

    override fun getTranslationKey(stack: ItemStack): String {
        return super.getTranslationKey(stack)
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        tooltip.add(AcText.translatable("item.amethyst_imbuement.mana_potion.tooltip1").formatted(Formatting.AQUA, Formatting.ITALIC))
    }

    override fun hasGlint(stack: ItemStack?): Boolean {
        return true
    }

    override fun appendStacks(group: ItemGroup, stacks: DefaultedList<ItemStack>) {
        if (isIn(group)) {
            stacks.add(ItemStack(this))
        }
    }

}