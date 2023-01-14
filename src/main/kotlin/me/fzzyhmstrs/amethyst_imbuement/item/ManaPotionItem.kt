package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.coding_util.AcText
import me.fzzyhmstrs.amethyst_core.mana_util.ManaItem
import me.fzzyhmstrs.amethyst_core.trinket_util.TrinketUtil
import me.fzzyhmstrs.amethyst_imbuement.entity.ManaPotionEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.client.item.TooltipContext
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.ItemCooldownManager
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.PotionItem
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
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
        for (stack2 in user.inventory.offHand){
            if (stack2.item is ManaItem && stack2.isDamaged){
                stacks.add(stack2)
            }
        }
        for (stack2 in user.inventory.armor){
            if (stack2.item is ManaItem && stack2.isDamaged){
                stacks.add(stack2)
            }
        }
        val stacks2 = TrinketUtil.getTrinketStacks(user)
        stacks2.forEach {
            if (it.item is ManaItem && it.isDamaged){
                stacks.add(it)
            }
        }
        val healLeft = 100
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
        user.itemCooldownManager.set(RegisterItem.MANA_POTION,12)
        return stack
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stacks: MutableList<ItemStack> = mutableListOf()
        for (stack2 in user.inventory.main){
            if (stack2.item is ManaItem && stack2.isDamaged){
                stacks.add(stack2)
            }
        } // iterate over the inventory and look for items that are interfaced with "ManaItem"
        for (stack2 in user.inventory.offHand){
            if (stack2.item is ManaItem && stack2.isDamaged){
                stacks.add(stack2)
            }
        }
        for (stack2 in user.inventory.armor){
            if (stack2.item is ManaItem && stack2.isDamaged){
                stacks.add(stack2)
            }
        }
        if (stacks.isEmpty()){
            val stack = user.getStackInHand(hand)
            world.playSound(
                null,
                user.x,
                user.y,
                user.z,
                SoundEvents.ENTITY_EXPERIENCE_BOTTLE_THROW,
                SoundCategory.NEUTRAL,
                0.5f,
                0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f)
            )
            if (!world.isClient) {
                val manaPotionEntity = ManaPotionEntity(world,user)
                manaPotionEntity.setItem(stack)
                manaPotionEntity.setVelocity(user, user.pitch, user.yaw, -20.0f, 0.7f, 1.0f)
                world.spawnEntity(manaPotionEntity)
            }
            if (!user.abilities.creativeMode) {
                stack.decrement(1)
            }
            return TypedActionResult.success(stack, world.isClient())
        }
        return super.use(world, user, hand)
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

    override fun getMaxUseTime(stack: ItemStack?): Int {
        return 12
    }

}