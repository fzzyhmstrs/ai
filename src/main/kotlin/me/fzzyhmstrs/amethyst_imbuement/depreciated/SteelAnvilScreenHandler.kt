package me.fzzyhmstrs.amethyst_imbuement.depreciated

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTag
import net.minecraft.block.AnvilBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.ForgingScreenHandler
import net.minecraft.screen.Property
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.text.LiteralText
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager

class SteelAnvilScreenHandler(syncId: Int,inventory: PlayerInventory, context: ScreenHandlerContext): ForgingScreenHandler(
    ScreenHandlerType.ANVIL, syncId, inventory, context) {


    private val LOGGER = LogManager.getLogger()
    private val levelCost = Property.create()
    private var repairItemUsage = 0
    private lateinit var newItemName: String

    constructor(syncId: Int,inventory: PlayerInventory) : this(syncId, inventory, ScreenHandlerContext.EMPTY)

    init{
        this.addProperty(levelCost)
    }

    override fun canUse(state: BlockState): Boolean {
        return false // state.isIn(RegisterTag.STEEL_ANVIL_TAG)
    }

    override fun canTakeOutput(player: PlayerEntity, present: Boolean): Boolean {
        return (player.abilities.creativeMode || player.experienceLevel >= this.levelCost.get()) && this.levelCost.get() > 0
    }

    override fun onTakeOutput(player: PlayerEntity, stack: ItemStack) {
        if (!player.abilities.creativeMode) {
            player.addExperienceLevels(-levelCost.get())
        }
        input.setStack(0, ItemStack.EMPTY)
        if (this.repairItemUsage > 0) {
            val itemStack = input.getStack(1)
            if (!itemStack.isEmpty && itemStack.count > this.repairItemUsage) {
                itemStack.decrement(this.repairItemUsage)
                input.setStack(1, itemStack)
            } else {
                input.setStack(1, ItemStack.EMPTY)
            }
        } else {
            input.setStack(1, ItemStack.EMPTY)
        }
        levelCost.set(0)
        context.run { world: World, pos: BlockPos ->
            val blockState = world.getBlockState(pos)
            if (!player.abilities.creativeMode && player.random
                    .nextFloat() < 0.12f
            ) {
                val blockState2 = AnvilBlock.getLandingState(blockState) //fix later if I get it working
                if (blockState2 == null) {
                    world.removeBlock(pos, false)
                    world.syncWorldEvent(WorldEvents.ANVIL_DESTROYED, pos, 0)
                } else {
                    world.setBlockState(pos, blockState2, Block.NOTIFY_LISTENERS)
                    world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0)
                }
            } else {
                world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0)
            }
        }
    }

    override fun updateResult() {
        var bl: Int
        val itemStack = input.getStack(0)
        levelCost.set(1)
        var i = 0
        var j = 0
        var k = 0
        if (itemStack.isEmpty) {
            output.setStack(0, ItemStack.EMPTY)
            levelCost.set(0)
            return
        }
        var itemStack2 = itemStack.copy()
        val itemStack3 = input.getStack(1)
        val map = EnchantmentHelper.get(itemStack2)
        j += itemStack.repairCost + if (itemStack3.isEmpty) 0 else itemStack3.repairCost
        repairItemUsage = 0
        if (!itemStack3.isEmpty) {
            bl = if (itemStack3.isOf(Items.ENCHANTED_BOOK) && !EnchantedBookItem.getEnchantmentNbt(itemStack3)
                    .isEmpty()
            ) 1 else 0
            //val n = bl
            if (itemStack2.isDamageable && itemStack2.item.canRepair(itemStack, itemStack3)) {
                var m: Int
                var l = Math.min(itemStack2.damage, itemStack2.maxDamage / 4)
                if (l <= 0) {
                    output.setStack(0, ItemStack.EMPTY)
                    levelCost.set(0)
                    return
                }
                m = 0
                while (l > 0 && m < itemStack3.count) {
                    val n2 = itemStack2.damage - l
                    itemStack2.damage = n2
                    ++i
                    l = Math.min(itemStack2.damage, itemStack2.maxDamage / 4)
                    ++m
                }
                repairItemUsage = m
            } else {
                var n3: Int
                var m: Int
                if (!(bl != 0 || itemStack2.isOf(itemStack3.item) && itemStack2.isDamageable)) {
                    output.setStack(0, ItemStack.EMPTY)
                    levelCost.set(0)
                    return
                }
                if (itemStack2.isDamageable && bl == 0) {
                    val l = itemStack.maxDamage - itemStack.damage
                    m = itemStack3.maxDamage - itemStack3.damage
                    n3 = m + itemStack2.maxDamage * 12 / 100
                    val o = l + n3
                    var p = itemStack2.maxDamage - o
                    if (p < 0) {
                        p = 0
                    }
                    if (p < itemStack2.damage) {
                        itemStack2.damage = p
                        i += 2
                    }
                }
                val l = EnchantmentHelper.get(itemStack3)
                m = 0
                n3 = 0
                for (p in l.keys) {
                    var r: Int
                    if (p == null) continue
                    val q = map.getOrDefault(p, 0)
                    //println("q: $q")
                    //println("p: " + p.translationKey)
                    r = if (q == l[p]!!.toInt().also { r = it }) r + 1 else Math.max(r, q)
                    var bl2 = p.isAcceptableItem(itemStack)
                    if (player.abilities.creativeMode || itemStack.isOf(Items.ENCHANTED_BOOK)) {
                        bl2 = true
                    }
                    for (enchantment in map.keys) {
                        if (enchantment === p || p.canCombine(enchantment)) continue
                        bl2 = false
                        ++i
                    }
                    if (!bl2) {
                        n3 = 1
                        continue
                    }
                    m = 1
                    if (r > p.maxLevel) {
                        r = p.maxLevel
                    }
                    map[p] = r
                    var s: Int
                    s = when (p.rarity) {
                        Enchantment.Rarity.COMMON -> {
                            1
                        }
                        Enchantment.Rarity.UNCOMMON -> {
                            2
                        }
                        Enchantment.Rarity.RARE -> {
                            4
                        }
                        Enchantment.Rarity.VERY_RARE -> {
                            8
                        } else-> {
                           8
                        }
                    }
                    if (bl != 0) {
                        s = Math.max(1, s / 2)
                    }
                    i += s * r
                    if (itemStack.count <= 1) continue
                    i = 40
                }
                if (n3 != 0 && m == 0) {
                    output.setStack(0, ItemStack.EMPTY)
                    levelCost.set(0)
                    return
                }
            }
        }
        if (StringUtils.isBlank(this.newItemName)) {
            //println("made it to custom name removal")
            if (itemStack.hasCustomName()) {
                k = 1
                i += k
                itemStack2.removeCustomName()
            }
        } else if (this.newItemName != itemStack.name.string) {
            //println("made it to custom name addition")
            k = 1
            i += k
            itemStack2.setCustomName(LiteralText(this.newItemName))
        }
        levelCost.set(j + i)
        if (i <= 0) {
            itemStack2 = ItemStack.EMPTY
        }
        if (k == i && k > 0 && levelCost.get() >= 40) {
            levelCost.set(39)
        }
        if (!itemStack2.isEmpty) {
            bl = itemStack2.repairCost
            if (!itemStack3.isEmpty && bl < itemStack3.repairCost) {
                bl = itemStack3.repairCost
            }
            if (k != i || k == 0) {
                bl = this.getNextCost(bl)
            }
            itemStack2.repairCost = bl
            EnchantmentHelper.set(map, itemStack2)
        }
        output.setStack(0, itemStack2)
        sendContentUpdates()
    }

    fun getNextCost(cost: Int): Int {
        return cost * 2 + 1
    }

    fun setNewItemName(newItemName: String) {
        //println("called for new item name!")
        this.newItemName = newItemName
        if (getSlot(2).hasStack()) {
            val itemStack = getSlot(2).stack
            if (StringUtils.isBlank(newItemName)) {
                itemStack.removeCustomName()
            } else {
                itemStack.setCustomName(LiteralText(this.newItemName))
            }
        }
        updateResult()
    }

    fun getLevelCost(): Int {
        return levelCost.get()
    }
}