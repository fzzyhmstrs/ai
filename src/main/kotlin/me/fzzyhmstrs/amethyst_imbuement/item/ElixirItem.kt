package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.PotionItem
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.stat.Stats
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

class ElixirItem(private val effect: StatusEffect, private val duration: Int, private val amplifier: Int, settings: Settings) : PotionItem(settings) {

    override fun getDefaultStack(): ItemStack {
        return ItemStack(this)
    }

    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        if (user !is PlayerEntity) return stack
        if (user is ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger(user, stack)
        }
        val existingStatus = user.getStatusEffect(effect)
        val amp = existingStatus?.amplifier?.takeIf { it + 1 + amplifier/3 > amplifier } ?: amplifier
        val dur = existingStatus?.duration?.takeIf { it > duration} ?: duration
        user.addStatusEffect(StatusEffectInstance(effect,dur,amp))
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

    override fun getTranslationKey(stack: ItemStack): String {
        return super.getTranslationKey(stack)
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        tooltip.add(AcText.translatable("${getTranslationKey(stack)}.tooltip1").formatted(Formatting.AQUA, Formatting.ITALIC))
    }

    override fun hasGlint(stack: ItemStack?): Boolean {
        return true
    }

    override fun getMaxUseTime(stack: ItemStack?): Int {
        return 32
    }

}
