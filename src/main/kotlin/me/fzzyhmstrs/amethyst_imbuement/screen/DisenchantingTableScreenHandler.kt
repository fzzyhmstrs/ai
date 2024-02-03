package me.fzzyhmstrs.amethyst_imbuement.screen

import me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.SpellScrollItem
import me.fzzyhmstrs.amethyst_imbuement.registry.*
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.screen.Property
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import java.util.*
import kotlin.math.min

@Suppress("SENSELESS_COMPARISON", "unused", "UnnecessaryVariable")
class DisenchantingTableScreenHandler(
    syncID: Int,
    private val playerInventory: PlayerInventory,
    private val context: ScreenHandlerContext
):  ScreenHandler(RegisterHandler.DISENCHANTING_TABLE_SCREEN_HANDLER, syncID) {

    constructor(syncID: Int, playerInventory: PlayerInventory) : this(
        syncID,
        playerInventory,
        ScreenHandlerContext.EMPTY,
    )

    private val inventory: Inventory = object : SimpleInventory(2) {
        override fun markDirty() {
            super.markDirty()
            this@DisenchantingTableScreenHandler.onContentChanged(this)
        }
    }
    private val random = Random()
    private val seed = Property.create()
    private var activeItem = Property.create()
    var enchantmentId = intArrayOf(-1, -1, -1)
    var enchantmentLevel = intArrayOf(-1, -1, -1)
    var disenchantCost = IntArray(1)
    private var removing = false
    private val player = playerInventory.player


    override fun canUse(player: PlayerEntity): Boolean {
        return canUse(this.context, player, RegisterBlock.DISENCHANTING_TABLE)
    }

    fun getSlotStack(index:Int): ItemStack{
        if (index < 0 || index > inventory.size()-1) return ItemStack.EMPTY
        return inventory.getStack(index)

    }

    fun getSeed(): Int {
        return this.seed.get()
    }

    override fun onClosed(player: PlayerEntity) {
        context.run { _: World, _: BlockPos ->
            dropInventory(
                player,
                inventory
            )
        }
        super.onClosed(player)
    }

    override fun onContentChanged(inventory: Inventory) {
        if (playerInventory.player.world.isClient) return
        if (removing) return
        if (inventory === this.inventory) {
            val itemStack = inventory.getStack(0)
            if (itemStack.isEmpty || !itemStack.hasEnchantments()) { //|| !itemStack.isEnchantable
                for (i in 0..2) {
                    enchantmentId[i] = -1
                    enchantmentLevel[i] = -1
                }
                disenchantCost[0] = -1
            }else {
                context.run { world: World, pos: BlockPos ->
                    //first addition of the item to the table, initialize with a default selection
                    if (enchantmentId[1] == -1 || !checkForEnchantMatch(itemStack)){
                        val enchantList = getEnchantments(itemStack)
                        enchantmentId[0] = -1
                        enchantmentLevel[0] = -1
                        val defaultEnchant = enchantList[0].key
                        val defaultEnchantId = FzzyPort.ENCHANTMENT.getRawId(defaultEnchant)
                        val defaultEnchantLevel = enchantList[0].value
                        enchantmentId[1] = defaultEnchantId
                        enchantmentLevel[1] = defaultEnchantLevel
                        if (enchantList.size > 1){
                            val defaultEnchant2 = enchantList[1].key
                            val defaultEnchantId2 = FzzyPort.ENCHANTMENT.getRawId(defaultEnchant2)
                            val defaultEnchantLevel2 = enchantList[1].value
                            enchantmentId[2] = defaultEnchantId2
                            enchantmentLevel[2] = defaultEnchantLevel2
                        }
                    }
                    activeItem.set(FzzyPort.ITEM.getRawId(itemStack.item))
                    if (!world.isClient) {
                        val nbt = itemStack.nbt
                        if (nbt == null) {
                            disenchantCost[0] = calculateRequiredExperienceLevel(0)
                        } else if (!nbt.contains(NbtKeys.DISENCHANT_COUNT.str())) {
                            disenchantCost[0] = calculateRequiredExperienceLevel(0)
                        } else {
                            val level = nbt.getInt(NbtKeys.DISENCHANT_COUNT.str())
                            updateDisenchantCost(level,world, pos)
                        }
                    }
                }
            }
            sendContentUpdates()
        }
    }

    private fun getEnchantments(stack: ItemStack): List<Map.Entry<Enchantment, Int>> {
        val nbt = stack.nbt ?: return listOf()
        val list = if (nbt != null) {
            nbt.getList(ItemStack.ENCHANTMENTS_KEY, 10)
        } else NbtList()
        return EnchantmentHelper.fromNbt(list).filterKeys { !FzzyPort.ENCHANTMENT.isInTag(it,RegisterTag.DISENCHANTING_BLACKLIST) }.map { it }
    }

    override fun onButtonClick(player: PlayerEntity, id: Int): Boolean {
        val itemStack = inventory.getStack(0)
        val itemStack2 = inventory.getStack(1)
        if (itemStack.isEmpty) return false
        val checkNbt = itemStack.nbt
        if (checkNbt != null && checkNbt.contains(NbtKeys.DISENCHANT_COUNT.str())){
            var atMax = false
            context.run { world: World, pos: BlockPos ->
                val lvl = checkNbt.getInt(NbtKeys.DISENCHANT_COUNT.str())
                val maxLevel = checkPillars(world, pos) / 2 + AiConfig.blocks.disenchanter.baseDisenchantsAllowed.get()
                if (lvl >= maxLevel) atMax = true
            }
            if (atMax) return false
        }
        when (id) {
            0 -> {
                if (enchantmentId[id] == -1) return false
                context.run { world: World, pos: BlockPos ->
                    enchantmentId[2] = enchantmentId[1]
                    enchantmentLevel[2] = enchantmentLevel[1]
                    enchantmentId[1] = enchantmentId[0]
                    enchantmentLevel[1] = enchantmentLevel[0]
                    val refIndex = findReferenceId(itemStack, id)
                    if (refIndex == 0){
                        enchantmentId[0] = -1
                        enchantmentLevel[0] = -1
                    } else {
                        val enchantList = getEnchantments(itemStack)
                        val enchant = enchantList[refIndex-1].key
                        val enchantId = FzzyPort.ENCHANTMENT.getRawId(enchant)
                        val enchantLevel = enchantList[refIndex-1].value
                        enchantmentId[0] = enchantId
                        enchantmentLevel[0] = enchantLevel
                    }
                    world.playSound(
                        null,
                        pos,
                        SoundEvents.UI_BUTTON_CLICK.value(),
                        SoundCategory.BLOCKS,
                        1.0f,
                        world.random.nextFloat() * 0.1f + 0.9f
                    )
                }
                onContentChanged(inventory)
                sendContentUpdates()
            }
            1 -> {
                if (enchantmentId[id] == -1) return false
                if ((itemStack2.isOf(Items.BOOK) && FzzyPort.ENCHANTMENT.get(enchantmentId[id]) is ScepterAugment) || (itemStack2.isOf(RegisterScepter.EMPTY_SPELL_SCROLL) && FzzyPort.ENCHANTMENT.get(enchantmentId[id]) !is ScepterAugment) || (player.experienceLevel < disenchantCost[0] && !player.abilities.creativeMode) || (!itemStack2.isOf(Items.BOOK) && !itemStack2.isOf(RegisterScepter.EMPTY_SPELL_SCROLL))) return false
                context.run { world: World, pos: BlockPos ->
                    removing = true
                    val enchantList3 = EnchantmentHelper.get(itemStack)
                    val enchantCheck = FzzyPort.ENCHANTMENT.get(enchantmentId[id])
                    if (enchantList3.containsKey(enchantCheck)){
                        enchantList3.remove(enchantCheck)
                    } else {
                        return@run
                    }
                    EnchantmentHelper.set(enchantList3,itemStack)
                    val itemStack3 = if (itemStack2.isOf(Items.BOOK)) {
                        val itemStackTemp = ItemStack(Items.ENCHANTED_BOOK)
                        val nbtCompound = itemStack2.nbt
                        if (nbtCompound != null) {
                            itemStackTemp.nbt = nbtCompound.copy()
                        }
                        val entry = EnchantmentLevelEntry(enchantCheck, enchantmentLevel[id])
                        EnchantedBookItem.addEnchantment(itemStackTemp, entry)
                        itemStackTemp
                    } else {
                        if (enchantCheck is ScepterAugment) {
                            SpellScrollItem.createSpellScroll(enchantCheck,true)
                        } else {
                            val itemStackTemp = ItemStack(Items.ENCHANTED_BOOK)
                            val nbtCompound = itemStack2.nbt
                            if (nbtCompound != null) {
                                itemStackTemp.nbt = nbtCompound.copy()
                            }
                            val entry = EnchantmentLevelEntry(enchantCheck, enchantmentLevel[id])
                            EnchantedBookItem.addEnchantment(itemStackTemp, entry)
                            itemStackTemp
                        }
                    }
                    inventory.setStack(1, itemStack3)
                    if (!player.abilities.creativeMode) {
                        player.applyEnchantmentCosts(itemStack, disenchantCost[0])
                    }
                    val enchantList = itemStack.enchantments

                    inventory.markDirty()
                    if (enchantmentId[2] == -1){
                        enchantmentId[1] = -1
                        enchantmentLevel[1] = -1
                    } else {
                        enchantmentId[1] = enchantmentId[2]
                        enchantmentLevel[1] = enchantmentLevel[2]
                        val refIndex = findReferenceId(itemStack, 2)
                        if (refIndex == enchantList.size - 1){
                            enchantmentId[2] = -1
                            enchantmentLevel[2] = -1
                        } else {
                            val identifier2 = EnchantmentHelper.getIdFromNbt(enchantList[refIndex+1] as NbtCompound)
                            val enchant2 = FzzyPort.ENCHANTMENT.get(identifier2)
                            val enchantId2 = FzzyPort.ENCHANTMENT.getRawId(enchant2)
                            val enchantLevel2 = EnchantmentHelper.getLevel(enchant2,itemStack)
                            enchantmentId[2] = enchantId2
                            enchantmentLevel[2] = enchantLevel2
                        }
                    }
                    if (!world.isClient) {
                        val nbt = itemStack.orCreateNbt
                        if (!nbt.contains(NbtKeys.DISENCHANT_COUNT.str())) {
                            nbt.putInt(NbtKeys.DISENCHANT_COUNT.str(), 1)
                            updateDisenchantCost(1,world, pos)
                        } else {
                            val currentLevel = nbt.getInt(NbtKeys.DISENCHANT_COUNT.str())
                            val newLevel = currentLevel + 1
                            nbt.putInt(NbtKeys.DISENCHANT_COUNT.str(),newLevel)
                            updateDisenchantCost(newLevel,world, pos)
                        }
                        world.playSound(
                            null,
                            pos,
                            SoundEvents.BLOCK_GRINDSTONE_USE,
                            SoundCategory.BLOCKS,
                            0.5f,
                            world.random.nextFloat() * 0.1f + 0.9f
                        )
                        world.playSound(
                            null,
                            pos,
                            SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                            SoundCategory.BLOCKS,
                            1.0f,
                            world.random.nextFloat() * 0.1f + 0.9f
                        )
                        if (player is ServerPlayerEntity) {
                            RegisterCriteria.DISENCHANT_USE.trigger(player)
                        }
                        onContentChanged(inventory)
                        sendContentUpdates()
                    }
                    removing = false
                }
            }
            2 -> {
                if (enchantmentId[id] == -1) return false
                context.run { world: World, pos: BlockPos? ->
                    val enchantList = itemStack.enchantments
                    enchantmentId[0] = enchantmentId[1]
                    enchantmentLevel[0] = enchantmentLevel[1]
                    enchantmentId[1] = enchantmentId[2]
                    enchantmentLevel[1] = enchantmentLevel[2]
                    val refIndex = findReferenceId(itemStack, id)
                    if (refIndex >= enchantList.size-1){
                        enchantmentId[2] = -1
                        enchantmentLevel[2] = -1
                    } else {
                        val identifier = EnchantmentHelper.getIdFromNbt(enchantList[refIndex+1] as NbtCompound)
                        val enchant = FzzyPort.ENCHANTMENT.get(identifier)
                        val enchantId = FzzyPort.ENCHANTMENT.getRawId(enchant)
                        val enchantLevel = EnchantmentHelper.getLevel(enchant,itemStack)
                        enchantmentId[2] = enchantId
                        enchantmentLevel[2] = enchantLevel
                    }
                    world.playSound(
                        null,
                        pos,
                        SoundEvents.UI_BUTTON_CLICK.value(),
                        SoundCategory.BLOCKS,
                        1.0f,
                        world.random.nextFloat() * 0.1f + 0.9f
                    )
                }
                onContentChanged(inventory)
                sendContentUpdates()
            }
        }

        return true


    }

    override fun quickMove(player: PlayerEntity, index: Int): ItemStack? {
        var itemStack = ItemStack.EMPTY
        val slot = this.slots[index]
        if (slot != null && slot.hasStack()) {
            val itemStack2 = slot.stack
            itemStack = itemStack2.copy()
            if (index < 2) {
                if (!insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY
                }
            } else if (index in 2..28) {
                if (itemStack2.isOf(Items.BOOK) || itemStack2.isOf(RegisterScepter.EMPTY_SPELL_SCROLL)) {
                    slotChecker(itemStack2,1,29,38)
                } else {
                    slotChecker(itemStack2,0,29,38)
                }
            } else if (index in 29..37) {
                if (itemStack2.isOf(Items.BOOK) || itemStack2.isOf(RegisterScepter.EMPTY_SPELL_SCROLL)) {
                    slotChecker(itemStack2,1,2,29)
                } else {
                    slotChecker(itemStack2,0,2,29)
                }
            } else {
                return ItemStack.EMPTY
            }
            if (itemStack2.isEmpty) {
                slot.stack = ItemStack.EMPTY
            } else {
                slot.markDirty()
            }
            if (itemStack2.count == itemStack.count) {
                return ItemStack.EMPTY
            }
            slot.onTakeItem(player, itemStack2)
        }
        return itemStack
    }

    private fun slotChecker(stack: ItemStack, firstSlot:Int, playerSlotStart: Int, playerSlotEnd: Int): Boolean{
        if (!Companion.insertItem(stack, firstSlot, firstSlot + 1, false,this.slots)) {
            if (!Companion.insertItem(stack, playerSlotStart, playerSlotEnd, true,this.slots)) {
                return false
            }
        }
        return true
    }

    private fun updateDisenchantCost(level: Int, world: World, pos: BlockPos){
        val pillarPairs = checkPillars(world, pos) / 2
        val maxLevel = pillarPairs + AiConfig.blocks.disenchanter.baseDisenchantsAllowed.get()
        if (level >= maxLevel) {
            disenchantCost[0] = -1
        } else {
            disenchantCost[0] = calculateRequiredExperienceLevel(level)
        }
    }
    private fun calculateRequiredExperienceLevel(disenchantCount: Int): Int {
        val index = MathHelper.clamp(disenchantCount,0,AiConfig.blocks.disenchanter.levelCosts.size - 1)
        if (index == AiConfig.blocks.disenchanter.levelCosts.size - 1 && player is ServerPlayerEntity){
            RegisterCriteria.DISENCHANTING_MAX.trigger(player)
        }
        return AiConfig.blocks.disenchanter.levelCosts[index]
    }
    private fun checkForEnchantMatch(stack: ItemStack): Boolean{
        if (FzzyPort.ITEM.getRawId(stack.item) != activeItem.get()) return false
        for (i in 0..2){
            if (enchantmentId[i] == -1) continue
            val enchantTest = Enchantment.byRawId(enchantmentId[i])
            if (EnchantmentHelper.getLevel(enchantTest,stack) != enchantmentLevel[i]){
                return false
            }
        }
        return true
    }
    private fun findReferenceId(stack: ItemStack, id: Int): Int{
        val enchantList2 = EnchantmentHelper.get(stack).keys
        if (enchantList2.isEmpty()) return 0
        for (i in enchantList2.indices){
            val enchant = enchantList2.elementAt(i)
            val enchantId = FzzyPort.ENCHANTMENT.getRawId(enchant)
            if (enchantId == enchantmentId[id]){
                return i
            }
        }
        return 0
    }
    private fun checkPillars(world: World, pos: BlockPos): Int {
        var pillars = 0
        for (i in -1..1) {
            for (j in -1..1) {
                if (i == 0 && j == 0 || !world.isAir(pos.add(i, 0, j))) continue
                val bs = world.getBlockState(pos.add(i * 2, 0, j * 2))
                val bs2 = world.getBlockState(pos.add(i * 2, 1, j * 2))
                val bl = bs.isIn(RegisterTag.PILLARS_TAG) && bs2.isIn(RegisterTag.PILLARS_TAG)
                if (bl) {
                    pillars++
                }
            }
        }
        return pillars
    }

    companion object{

        fun insertItem(stack: ItemStack, startIndex: Int, endIndex: Int, fromLast: Boolean, slots: List<Slot>): Boolean {
            var itemStack: ItemStack
            var slot: Slot
            var bl = false
            var i = startIndex
            if (fromLast) {
                i = endIndex - 1
            }
            if (stack.isStackable) {
                while (!stack.isEmpty && if (fromLast) i >= startIndex else i < endIndex) {
                    slot = slots[i]
                    itemStack = slot.stack
                    if (!itemStack.isEmpty && ItemStack.canCombine(stack, itemStack)) {
                        val j = itemStack.count + stack.count
                        if (j <= stack.maxCount && j <= slot.maxItemCount) {
                            stack.count = 0
                            itemStack.count = j
                            slot.markDirty()
                            bl = true
                        } else if (itemStack.count < stack.maxCount && itemStack.count < slot.maxItemCount) {
                            val minToDec = min(stack.maxCount,slot.maxItemCount)
                            stack.decrement(minToDec - itemStack.count)
                            itemStack.count = minToDec
                            slot.markDirty()
                            bl = true
                        }
                    }
                    if (fromLast) {
                        --i
                        continue
                    }
                    ++i
                }
            }
            if (!stack.isEmpty) {
                i = if (fromLast) endIndex - 1 else startIndex
                while (if (fromLast) i >= startIndex else i < endIndex) {
                    slot = slots[i]
                    itemStack = slot.stack
                    if (itemStack.isEmpty && slot.canInsert(stack)) {
                        if (stack.count > slot.maxItemCount) {
                            slot.stack = stack.split(slot.maxItemCount)
                        } else {
                            slot.stack = stack.split(stack.count)
                        }
                        bl = true
                        break
                    }
                    if (fromLast) {
                        --i
                        continue
                    }
                    ++i
                }
            }
            return bl
        }


    }


    init{
        //coordinate system is in pixels, thank god
        //add top two imbuement slots
        addSlot(object : Slot(inventory, 0, 15, 47) {
            override fun canInsert(stack: ItemStack): Boolean { return true }
            override fun getMaxItemCount(): Int {
                return 1
            }
        })
        addSlot(object : Slot(inventory, 1, 35, 47) {
            override fun canInsert(stack: ItemStack): Boolean { return stack.isOf(Items.BOOK) || stack.isOf(RegisterScepter.EMPTY_SPELL_SCROLL) }
            override fun getMaxItemCount(): Int {
                return 1
            }
        })

        //add the player inventory
        for (i in 0..2) {
            for (j in 0..8) {
                addSlot(Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
            }
        }

        //add the player hotbar
        for (i in 0..8) {
            this.addSlot(Slot(playerInventory, i, 8 + i * 18, 142))
        }

        //add the properties for the three enchantment bars
        addProperty(seed).set(playerInventory.player.enchantmentTableSeed)
        addProperty(activeItem).set(-1)
        addProperty(Property.create(this.enchantmentId, 0))
        addProperty(Property.create(this.enchantmentId, 1))
        addProperty(Property.create(this.enchantmentId, 2))
        addProperty(Property.create(this.enchantmentLevel, 0))
        addProperty(Property.create(this.enchantmentLevel, 1))
        addProperty(Property.create(this.enchantmentLevel, 2))
        addProperty(Property.create(this.disenchantCost, 0))
        if (context != ScreenHandlerContext.EMPTY) {
            context.run { world: World, pos: BlockPos ->
                val pillarPairs = checkPillars(world, pos) / 2
                if (pillarPairs == 4 && player is ServerPlayerEntity){
                    RegisterCriteria.DISENCHANTING_PILLARS.trigger(player)
                }
            }
        }
    }


}
