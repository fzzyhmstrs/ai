package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTool
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.UseAction
import net.minecraft.world.World
import net.minecraft.util.TypedActionResult as TypedActionResult1

@Suppress("ObjectPropertyName", "PrivatePropertyName")
open class SniperBowItem(settings: Settings) :  CrossbowItem(settings) {
    private val CHARGED_PROJECTILES_KEY = "ChargedProjectiles"
    private var charged = false
    private var loaded = false

    companion object{
        var _hand: Hand = Hand.MAIN_HAND
        var _world: World? = null
        private var _sniper_scope_index = 0
        private val SNIPER_BOW_SCOPE_LIST = arrayOf(Identifier(AI.MOD_ID,"textures/misc/sniper_bow_scope_0.png"),
            Identifier("minecraft","textures/misc/spyglass_scope.png"),
            Identifier(AI.MOD_ID,"textures/misc/sniper_bow_scope_6.png"),
            Identifier(AI.MOD_ID,"textures/misc/sniper_bow_scope_5.png"),
            Identifier(AI.MOD_ID,"textures/misc/sniper_bow_scope_4.png"),
            Identifier(AI.MOD_ID,"textures/misc/sniper_bow_scope_3.png"),
            Identifier(AI.MOD_ID,"textures/misc/sniper_bow_scope_2.png"),
            Identifier(AI.MOD_ID,"textures/misc/sniper_bow_scope_1.png")) //arranging it like this so scrolling down moves through the custom crosshairs and ends at the spyglass, not the other way around. figure scrolling down is more common
        private val SNIPER_BOW_SCOPE_LIST_LENGTH = SNIPER_BOW_SCOPE_LIST.size-1 //return 0-indexed length of the above array (4 elements = length 3 etc.)
        var SNIPER_BOW_SCOPE = SNIPER_BOW_SCOPE_LIST[_sniper_scope_index]

        fun changeScope(up: Boolean){
            val si = _sniper_scope_index
            val sl = SNIPER_BOW_SCOPE_LIST_LENGTH
            _sniper_scope_index = if (up) {
                if (si > 0) {
                    si - 1
                } else {
                    sl
                }

            } else {
                if (si < sl) {
                    si + 1
                } else {
                    0
                }
            }
            SNIPER_BOW_SCOPE = SNIPER_BOW_SCOPE_LIST[_sniper_scope_index]

        }
    }

    protected open fun getSpeed(stack: ItemStack): Float {
        val i = EnchantmentHelper.getLevel(RegisterEnchantment.DEADLY_SHOT, stack)
        return if (hasProjectile(stack, Items.FIREWORK_ROCKET)) {
            RegisterEnchantment.DEADLY_SHOT.getSpeed(i,firework = true,sniper = true)
        } else RegisterEnchantment.DEADLY_SHOT.getSpeed(i,firework = false,sniper = true)
    }

    override fun canRepair(stack: ItemStack, ingredient: ItemStack): Boolean {
        return ingredient.isOf(RegisterItem.BERYL_COPPER_INGOT) && stack.isOf(RegisterTool.SNIPER_BOW)
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult1<ItemStack> {
        val itemStack = user.getStackInHand(hand)
        _hand = hand
        _world = world
        if (isCharged(itemStack)) {
            user.playSound(SoundEvents.ITEM_SPYGLASS_USE, 1.0f, 1.0f)
            user.incrementStat(Stats.USED.getOrCreateStat(this))
            return ItemUsage.consumeHeldItem(world, user, hand)
        }
        if (!user.getProjectileType(itemStack).isEmpty) {
            if (!isCharged(itemStack)) {
                this.charged = false
                this.loaded = false
                user.setCurrentHand(hand)
            }
            return TypedActionResult1.consume(itemStack)
        }
        return TypedActionResult1.fail(itemStack)
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        val i = getMaxUseTime(stack) - remainingUseTicks
        val f = getPullProgress(i, stack)
        if (f >= 1.0f && !isCharged(stack) && loadProjectiles(user, stack)) {
            setCharged(stack, true)
            val soundCategory = if (user is PlayerEntity) SoundCategory.PLAYERS else SoundCategory.HOSTILE
            world.playSound(
                null,
                user.x,
                user.y,
                user.z,
                SoundEvents.ITEM_CROSSBOW_LOADING_END,
                soundCategory,
                1.0f,
                1.0f / (world.getRandom().nextFloat() * 0.5f + 1.0f) + 0.2f
            )
        } else if (isCharged(stack)){
            shootAll(world, user, _hand, stack, getSpeed(stack), 0.1f)
            setCharged(stack, false)
            this.playStopUsingSound(user)
            if (user is PlayerEntity) {
                if(EnchantmentHelper.getLevel(RegisterEnchantment.ILLUMINATING,stack) > 0){
                    val helmet = user.inventory.armor[3]
                    if (user.hasStatusEffect(StatusEffects.NIGHT_VISION) && EnchantmentHelper.getLevel(RegisterEnchantment.NIGHT_VISION,helmet) == 0) {
                        user.removeStatusEffect(StatusEffects.NIGHT_VISION)
                    }
                }
            }
        }
    }


    override fun getUseAction(stack: ItemStack): UseAction {
        return if (isCharged(stack)) {
            UseAction.SPYGLASS
        } else {
            UseAction.CROSSBOW
        }
    }

    override fun getMaxUseTime(stack: ItemStack): Int {
        return getPullTimeSniperBow(stack) + 3
    }

    private fun getPullTimeSniperBow(stack: ItemStack): Int {
        val i = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, stack)
        return if (i == 0) 30 else 30 - 5 * i
    }


    private fun getPullProgress(useTicks: Int, stack: ItemStack): Float {
        var f = useTicks.toFloat() / getPullTimeSniperBow(stack).toFloat()
        if (f > 1.0f) {
            f = 1.0f
        }
        return f
    }


    private fun putProjectile(crossbow: ItemStack, projectile: ItemStack) {
        val nbtCompound = crossbow.orCreateNbt
        val nbtList = if (nbtCompound.contains(
                CHARGED_PROJECTILES_KEY,
                9
            )
        ) nbtCompound.getList(CHARGED_PROJECTILES_KEY, 10) else NbtList()
        val nbtCompound2 = NbtCompound()
        projectile.writeNbt(nbtCompound2)
        nbtList.add(nbtCompound2)
        nbtCompound.put(CHARGED_PROJECTILES_KEY, nbtList)
    }

    private fun loadProjectile(
        shooter: LivingEntity,
        crossbow: ItemStack,
        projectile: ItemStack,
        simulated: Boolean,
        creative: Boolean
    ): Boolean {
        val itemStack: ItemStack
        if (projectile.isEmpty) {
            return false
        }
        val bl: Boolean = creative && projectile.item is ArrowItem
        if (!(bl || creative || simulated)) {
            itemStack = projectile.split(1)
            if (projectile.isEmpty && shooter is PlayerEntity) {
                shooter.inventory.removeOne(projectile)
            }
        } else {
            itemStack = projectile.copy()
        }
        putProjectile(crossbow, itemStack)
        return true
    }

    private fun loadProjectiles(shooter: LivingEntity, projectile: ItemStack): Boolean {
        val i = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, projectile)
        val j = if (i == 0) 1 else 3
        val bl = shooter is PlayerEntity && shooter.abilities.creativeMode
        var itemStack = shooter.getProjectileType(projectile)
        var itemStack2 = itemStack.copy()
        for (k in 0 until j) {
            if (k > 0) {
                itemStack = itemStack2.copy()
            }
            if (itemStack.isEmpty && bl) {
                itemStack = ItemStack(Items.ARROW)
                itemStack2 = itemStack.copy()
            }
            if (loadProjectile(shooter, projectile, itemStack, k > 0, bl)) continue
            return false
        }
        return true
    }

    private fun playStopUsingSound(user: LivingEntity) {
        user.playSound(SoundEvents.ITEM_SPYGLASS_STOP_USING, 1.0f, 1.0f)
    }


}