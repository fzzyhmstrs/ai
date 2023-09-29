package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
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

class ElixirOfRebindingItem(settings: Settings) : PotionItem(settings) {

    override fun getDefaultStack(): ItemStack {
        return ItemStack(this)
    }

    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        if (user !is PlayerEntity) return stack
        if (user is ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger(user, stack)
        }
        user.addStatusEffect(StatusEffectInstance(RegisterStatus.ARCANE_AURA,6000,1))
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
        tooltip.add(AcText.translatable("item.amethyst_imbuement.elixir_of_rebinding.tooltip1").formatted(Formatting.AQUA, Formatting.ITALIC))
    }

    override fun hasGlint(stack: ItemStack?): Boolean {
        return true
    }

    override fun getMaxUseTime(stack: ItemStack?): Int {
        return 32
    }

}