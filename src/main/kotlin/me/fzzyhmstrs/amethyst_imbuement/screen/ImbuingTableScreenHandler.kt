package me.fzzyhmstrs.amethyst_imbuement.screen

import com.google.common.collect.Lists
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentModifier
import me.fzzyhmstrs.amethyst_core.modifier_util.ModifierHelper
import me.fzzyhmstrs.amethyst_core.nbt_util.Nbt
import me.fzzyhmstrs.amethyst_core.registry.ModifierRegistry
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterHelper
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterHandler
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import me.shedaniel.rei.api.common.transfer.RecipeFinder
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.block.Blocks
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.Property
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import net.minecraft.util.collection.Weighting
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.random.Random
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import java.util.*
import kotlin.math.max
import kotlin.math.roundToInt

@Suppress("SENSELESS_COMPARISON", "unused", "SameParameterValue")
class ImbuingTableScreenHandler(
    syncID: Int,
    playerInventory: PlayerInventory,
    private val context: ScreenHandlerContext
):  ScreenHandler(RegisterHandler.IMBUING_SCREEN_HANDLER, syncID) {

    constructor(syncID: Int, playerInventory: PlayerInventory) : this(
        syncID,
        playerInventory,
        ScreenHandlerContext.EMPTY,
    )

    private val inventory: SimpleInventory = object : SimpleInventory(13) {
        override fun markDirty() {
            super.markDirty()
            this@ImbuingTableScreenHandler.onContentChanged(this)
        }
    }

    private val random = net.minecraft.util.math.random.LocalRandom(0L)
    private val player = playerInventory.player
    private val seed = Property.create()
    var enchantmentPower = IntArray(3)
    var enchantmentId = intArrayOf(-1, -1, -1)
    var enchantmentLevel = intArrayOf(-1, -1, -1)
    var imbueId = intArrayOf(0,0,0)
    var modId = intArrayOf(0,0,0)
    var levelLow = intArrayOf(0,0,0)
    //private var matchBut: Optional<ImbuingRecipe>? = Optional.empty()


    override fun canUse(player: PlayerEntity): Boolean {
        return canUse(this.context, player, RegisterBlock.IMBUING_TABLE)
    }



    fun getLapisCount(): Int {
        val itemStack: ItemStack = this.inventory.getStack(7)
        return if (itemStack.isEmpty) {
            0
        } else if(!itemStack.isOf(Items.LAPIS_LAZULI)){
            0
        } else{
            itemStack.count
        }
    }

    fun getSeed(): Int {
        return this.seed.get()
    }

    override fun close(player: PlayerEntity?) {
        super.close(player)
        context.run { _: World?, _: BlockPos? ->
            dropInventory(
                player,
                inventory
            )
        }
    }

    override fun onContentChanged(inventory: Inventory) {
        if (inventory === this.inventory) {
            val itemStack = inventory.getStack(6)

            if (itemStack.isEmpty) { //|| !itemStack.isEnchantable
                for (i in 0..2) {
                    enchantmentPower[i] = 0
                    enchantmentId[i] = -1
                    enchantmentLevel[i] = -1
                    imbueId[i] = 0
                    levelLow[i] = 0
                }
            }else {
                context.run { world: World, pos: BlockPos ->
                    var match: Optional<ImbuingRecipe>? = null
                    if (!world.isClient) {
                        match = (world).recipeManager.getFirstMatch(
                            ImbuingRecipe.Type,
                            inventory,
                            world
                        )
                    }
                    if (match != null && match.isPresent){
                        enchantmentPower[0] = MathHelper.ceil(match.get().getCost() * MathHelper.clamp(AiConfig.altars.imbuingTableDifficultyModifier,0.0F,10.0F))
                        enchantmentPower[1] = 0
                        enchantmentPower[2] = 0
                        enchantmentLevel[0] = 1
                        enchantmentLevel[1] = -1
                        enchantmentLevel[2] = -1
                        enchantmentId[0] = -1
                        enchantmentId[1] = -1
                        enchantmentId[2] = -1
                        imbueId[0] = 0
                        imbueId[1] = 0
                        imbueId[2] = 0
                        modId[0] = 0
                        modId[1] = 0
                        modId[2] = 0
                        levelLow[0] = 0
                        levelLow[1] = 0
                        levelLow[2] = 0
                        if (match.get().getAugment() != ""){
                            val id = Identifier(match.get().getAugment())
                            val augment = Registry.ENCHANTMENT.get(id)
                            if (augment != null) {
                                val augCheck = if (augment is ScepterAugment){
                                    val blA = ScepterHelper.isAcceptableScepterItem(augment,itemStack, player)
                                    val blB = augment.isAcceptableItem(itemStack)
                                    Pair(blA,blB)
                                } else {
                                    Pair(true,augment.isAcceptableItem(itemStack))
                                }
                                if(augCheck.first && augCheck.second) {
                                    val l = EnchantmentHelper.get(itemStack)
                                    var bl1 = false
                                    for (p in l.keys) {
                                        if(p === augment){
                                            if (l[p] == augment.maxLevel){
                                                bl1 = true
                                            }
                                        }
                                    }
                                    if(!bl1) {
                                        enchantmentId[0] = Registry.ENCHANTMENT.getRawId(augment)
                                        imbueId[0] = Registry.ENCHANTMENT.getRawId(augment)
                                    } else {
                                        enchantmentPower[0] = 0
                                    }
                                } else if(!augCheck.first && augCheck.second) {
                                    levelLow[0] = Registry.ENCHANTMENT.getRawId(augment)
                                } else if(!augCheck.second) {
                                    levelLow[0] = -1 * Registry.ENCHANTMENT.getRawId(augment)
                                }else{
                                    enchantmentPower[0] = 0
                                }
                            }else if (ModifierRegistry.isModifier(id)) {
                                val modifier = ModifierRegistry.getByType<AugmentModifier>(id)
                                val blA = modifier?.isAcceptableItem(itemStack)?:false
                                val blB = ModifierHelper.checkModifierLineage(id,itemStack)
                                if (blA && blB){
                                    modId[0] = ModifierRegistry.getRawId(id)
                                    enchantmentId[0] = -2
                                } else if (blA && !blB) {
                                    modId[0] = -1 * ModifierRegistry.getRawId(id)
                                    enchantmentId[0] = -2
                                } else {
                                    enchantmentPower[0] = 0
                                }
                            } else {
                                println("Could not find augment or modifier under the key $id")
                            }
                        } else{
                            enchantmentId[0] = -2
                            imbueId[0] = Registry.ITEM.getRawId(match.get().output.item) * -1
                        }
                    } else if (!itemStack.isEnchantable){
                        for (i in 0..2) {
                            enchantmentPower[i] = 0
                            enchantmentLevel[i] = -1
                        }
                    } else if (AiConfig.altars.imbuingTableEnchantingEnabled && checkLapisAndSlots(inventory)) {
                        val i = checkBookshelves(world, pos)

                        random.setSeed(seed.get().toLong())
                        var j = 0
                        while (j < 3) {
                            enchantmentPower[j] =
                                this.calculateRequiredExperienceLevel(random, j, i, itemStack)
                            enchantmentId[j] = -1
                            enchantmentLevel[j] = -1
                            if (enchantmentPower[j] >= j + 1) {
                                ++j
                                continue
                            }
                            enchantmentPower[j] = 0
                            ++j
                        }
                        j = 0
                        while (j < 3) {
                            val k: List<EnchantmentLevelEntry> = this.generateEnchantments(itemStack,j,enchantmentPower[j])
                            if (enchantmentPower[j] <= 0 || k == null || k.isEmpty()) {
                                ++j
                                continue
                            }
                            val enchantmentLevelEntry = k[random.nextInt(k.size)]
                            enchantmentId[j] =
                                Registry.ENCHANTMENT.getRawId(enchantmentLevelEntry.enchantment)
                            enchantmentLevel[j] = enchantmentLevelEntry.level
                            ++j
                        }
                    } else {
                        for (i in 0..2) {
                            enchantmentPower[i] = 0
                            enchantmentLevel[i] = -1
                        }
                    }
                    sendContentUpdates()
                }
            }
        }
    }
    override fun onButtonClick(player: PlayerEntity, id: Int): Boolean {
        if (player.world.isClient) return false
        val itemStack = inventory.getStack(6)
        val itemStack2 = inventory.getStack(7)
        var i = id + 1
        var match: Optional<ImbuingRecipe> = Optional.empty()
        context.run { world: World, _: BlockPos? ->
            if (!world.isClient) {
                match = (world).recipeManager.getFirstMatch(
                    ImbuingRecipe.Type,
                    inventory,
                    world
                )
            }
        }
        if (match.isEmpty){
            if(enchantmentPower[id] in 31..40){
                i = id + 2
            }else if(enchantmentPower[id] in 41..50){
                i = id + 3
            }else if(enchantmentPower[id] in 51..60){
                i = id + 4
            }
            if ((itemStack2.isEmpty || itemStack2.count < i) && !player.abilities.creativeMode) {
                return false
            }
        }else{
            i = MathHelper.ceil(match.get().getCost() * MathHelper.clamp(AiConfig.altars.imbuingTableDifficultyModifier,0.0F,10.0F))
        }

        if (enchantmentPower[id] > 0 && !itemStack.isEmpty && ((player.experienceLevel >= i && player.experienceLevel >= enchantmentPower[id] && levelLow[id] == 0)  || player.abilities.creativeMode)) {
            var buttonWorked = true
            context.run { world: World, pos: BlockPos? ->
                var itemStack3 = itemStack
                if (match.isEmpty) {
                    val list = generateEnchantments(itemStack3, id, enchantmentPower[id])
                    if (list.isNotEmpty()) {
                        player.applyEnchantmentCosts(itemStack3, i)
                        val bl = itemStack3.isOf(Items.BOOK)
                        if (bl) {
                            itemStack3 = ItemStack(Items.ENCHANTED_BOOK)
                            val nbtCompound = itemStack.nbt
                            if (nbtCompound != null) {
                                itemStack3.nbt = nbtCompound.copy()
                            }
                            inventory.setStack(6, itemStack3)
                        }
                        for (nbtCompound in list.indices) {
                            val enchantmentLevelEntry = list[nbtCompound]
                            if (bl) {
                                EnchantedBookItem.addEnchantment(itemStack3, enchantmentLevelEntry)
                                continue
                            }
                            itemStack3.addEnchantment(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level)
                        }
                        if (!player.abilities.creativeMode) {
                            itemStack2.decrement(i)
                            if (itemStack2.isEmpty) {
                                inventory.setStack(7, ItemStack.EMPTY)
                            }
                        }
                        player.incrementStat(Stats.ENCHANT_ITEM)
                        if (player is ServerPlayerEntity) {
                            Criteria.ENCHANTED_ITEM.trigger(player, itemStack3, i)
                        }
                        inventory.markDirty()
                        seed.set(player.enchantmentTableSeed)
                        onContentChanged(inventory)
                        playEnchantmentSound(world,pos)
                    }
                } else if(match.get().getAugment() != ""){
                    val augId = Identifier(match.get().getAugment())
                    val augmentChk = Registry.ENCHANTMENT.get(augId)
                    val modChk = ModifierRegistry.getByType<AugmentModifier>(augId)
                    if (augmentChk == null && modChk == null){
                        buttonWorked = false
                        return@run
                    }
                    if (augmentChk != null && augmentChk.isAcceptableItem(itemStack3)){
                        val bl = itemStack3.isOf(Items.BOOK)
                        if (bl) {
                            itemStack3 = ItemStack(Items.ENCHANTED_BOOK)
                            val nbtCompound = itemStack.nbt
                            if (nbtCompound != null) {
                                itemStack3.nbt = nbtCompound.copy()
                            }
                            inventory.setStack(6, itemStack3)
                            player.applyEnchantmentCosts(itemStack3, i)
                            EnchantedBookItem.addEnchantment(itemStack3, EnchantmentLevelEntry(augmentChk,1))

                        } else {
                            val l = EnchantmentHelper.get(itemStack3)
                            var r: Int
                            var bl1 = false
                            for (p in l.keys) {
                                if (p == null) continue
                                if(p === augmentChk){
                                    buttonWorked = false
                                    r = l[p]?.plus(1) ?: return@run
                                    if (r > augmentChk.maxLevel){
                                        return@run
                                    }
                                    l[p] = r
                                    bl1 = true
                                    buttonWorked = true
                                }
                            }
                            player.applyEnchantmentCosts(itemStack3, i)
                            if(bl1) {
                                EnchantmentHelper.set(l, itemStack3)
                            } else{
                                itemStack3.addEnchantment(augmentChk,1)
                            }
                        }
                        for (j in 0..12) { //decrement inventory slots even for creative mode!
                            if (j == 6 || player.abilities.creativeMode) continue //avoid bulldozing itemslot 6
                            if (inventory.getStack(j).item.hasRecipeRemainder()){
                                inventory.setStack(j, ItemStack(inventory.getStack(j).item.recipeRemainder, 1))
                            }else {
                                inventory.getStack(j).decrement(1)
                                if (inventory.getStack(j).isEmpty) {
                                    inventory.setStack(j, ItemStack.EMPTY)
                                }
                            }
                        }
                    } else if (modChk != null && modChk.isAcceptableItem(itemStack3)){
                        if (ModifierHelper.addModifier(augId,itemStack3)){
                            player.applyEnchantmentCosts(itemStack3, i)
                            for (j in 0..12) { //decrement inventory slots even for creative mode!
                                if (j == 6 || player.abilities.creativeMode) continue //avoid bulldozing itemslot 6
                                if (inventory.getStack(j).item.hasRecipeRemainder()){
                                    inventory.setStack(j, ItemStack(inventory.getStack(j).item.recipeRemainder, 1))
                                }else {
                                    inventory.getStack(j).decrement(1)
                                    if (inventory.getStack(j).isEmpty) {
                                        inventory.setStack(j, ItemStack.EMPTY)
                                    }
                                }
                            }
                        } else {
                            buttonWorked = false
                            return@run
                        }
                    } else {
                        buttonWorked = false
                        return@run
                    }
                    player.incrementStat(Stats.ENCHANT_ITEM)
                    if (player is ServerPlayerEntity) {
                        Criteria.ENCHANTED_ITEM.trigger(player, itemStack3, i)
                    }
                    inventory.markDirty()
                    seed.set(player.enchantmentTableSeed)
                    onContentChanged(inventory)
                    playEnchantmentSound(world,pos)
                } else{
                    player.applyEnchantmentCosts(itemStack3, i)
                    val l = EnchantmentHelper.get(itemStack3)
                    for (j in 0..12) {
                        if (j != 6 && player.abilities.creativeMode) continue //only decrement the middle slot if its creative mode, to make way for the new item stack
                        if (inventory.getStack(j).item.hasRecipeRemainder()){
                            inventory.setStack(j, ItemStack(inventory.getStack(j).item.recipeRemainder, 1))
                        }else {
                            inventory.getStack(j).decrement(1)
                            if (inventory.getStack(j).isEmpty) {
                                inventory.setStack(j, ItemStack.EMPTY)
                            }
                        }
                    }
                    val itemStack4 = match.get().output
                    itemStack4.item.onCraft(itemStack4,world, player)
                    inventory.setStack(6,itemStack4)
                    if(match.get().getTransferEnchant()){
                        Nbt.transferNbt(itemStack3,itemStack4)
                        EnchantmentHelper.set(l,itemStack4)
                    }
                    inventory.markDirty()
                    onContentChanged(inventory)
                    playEnchantmentSound(world,pos)
                }
            }
            return buttonWorked
        }
        return false
    }

    override fun transferSlot(player: PlayerEntity, index: Int): ItemStack? {
        var itemStack = ItemStack.EMPTY
        val slot = this.slots[index]
        if (slot != null && slot.hasStack()) {
            val itemStack2 = slot.stack
            itemStack = itemStack2.copy()
            if (index < 13) {
                if (!insertItem(itemStack2, 13, 49, true)) {
                    return ItemStack.EMPTY
                }
            } else if (index in 13..39) {
                if (itemStack2.isOf(Items.LAPIS_LAZULI)){
                    if (!slotChecker(itemStack2,7,40,49)){
                        return ItemStack.EMPTY
                    }
                } else {
                    if (!slotChecker(itemStack2,6,40,49)){
                        return ItemStack.EMPTY
                    }
                }
            } else if (index in 40..48) {
                if (itemStack2.isOf(Items.LAPIS_LAZULI)){
                    if (!slotChecker(itemStack2,7,13,40)){
                        return ItemStack.EMPTY
                    }
                } else {
                    if (!slotChecker(itemStack2,6,13,40)){
                        return ItemStack.EMPTY
                    }
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

    private fun generateEnchantments(stack: ItemStack, slot: Int, level: Int): List<EnchantmentLevelEntry> {
        random.setSeed((seed.get() + slot).toLong())
        val list: ArrayList<EnchantmentLevelEntry> = generateEnchantmentList(random, stack, level, false)
        if (stack.isOf(Items.BOOK) && list.size > 1) {
            list.removeAt(random.nextInt(list.size))
        }
        return list
    }

    private fun playEnchantmentSound(world: World, pos: BlockPos?){
        world.playSound(
            null,
            pos,
            SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
            SoundCategory.BLOCKS,
            1.0f,
            world.random.nextFloat() * 0.1f + 0.9f
        )
    }
    private fun slotChecker(stack: ItemStack, firstSlot:Int, playerSlotStart: Int, playerSlotEnd: Int): Boolean{
        if (!DisenchantingTableScreenHandler.insertItem(stack, firstSlot, firstSlot + 1, false, this.slots)) {
            if (!DisenchantingTableScreenHandler.insertItem(stack, 0, firstSlot, false, this.slots)) {
                if (!DisenchantingTableScreenHandler.insertItem(stack, firstSlot + 1, 13, false, this.slots)) {
                    if (!DisenchantingTableScreenHandler.insertItem(stack, playerSlotStart, playerSlotEnd, true, this.slots)) {
                        return false
                    }
                }
            }
        }
        return true
    }
    private fun calculateRequiredExperienceLevel(random: Random, slotIndex: Int, bookshelfCount: Int, stack: ItemStack): Int {
        var bookshelfCnt = bookshelfCount
        val item = stack.item
        val i = item.enchantability
        if (i <= 0) {
            return 0
        }
        if (bookshelfCount > 30) {
            bookshelfCnt = 30
        }
        val j = random.nextInt(8) + 1 + (bookshelfCnt shr 1) + random.nextInt(bookshelfCnt + 1)
        if (slotIndex == 0) {
            return max(j / 3, 1)
        }
        return if (slotIndex == 1) {
            j * 2 / 3 + 1 + bookshelfCount / 3
        } else max(j, bookshelfCnt * 2)
    }
    private fun checkBookshelves(world: World, pos: BlockPos): Int{
        var i = 0
        var j: Int = -1
        while (j <= 1) {
            for (k in -1..1) {
                if (j == 0 && k == 0 || !world.isAir(pos.add(k, 0, j)) || !world.isAir(
                        pos.add(
                            k,
                            1,
                            j
                        )
                    )
                ) continue
                if (world.getBlockState(pos.add(k * 2, 0, j * 2)).isOf(Blocks.BOOKSHELF)) {
                    ++i
                }
                if (world.getBlockState(pos.add(k * 2, 1, j * 2)).isOf(Blocks.BOOKSHELF)) {
                    ++i
                }
                if (k == 0 || j == 0) continue
                if (world.getBlockState(pos.add(k * 2, 0, j)).isOf(Blocks.BOOKSHELF)) {
                    ++i
                }
                if (world.getBlockState(pos.add(k * 2, 1, j)).isOf(Blocks.BOOKSHELF)) {
                    ++i
                }
                if (world.getBlockState(pos.add(k, 0, j * 2)).isOf(Blocks.BOOKSHELF)) {
                    ++i
                }
                if (!world.getBlockState(pos.add(k, 1, j * 2)).isOf(Blocks.BOOKSHELF)) continue
                ++i
            }
            ++j
        }
        return i
    }
    private fun checkLapisAndSlots(inventory: Inventory): Boolean{
        for (i in intArrayOf(0,1,2,3,4,5,8,9,10,11,12)){
            if (!inventory.getStack(i).isEmpty) return false
        }
        return (inventory.getStack(7).isEmpty || inventory.getStack(7).isOf(Items.LAPIS_LAZULI))
    }

    fun populateRecipeFinder(finder: RecipeFinder) {
        for (i in 0..12){
            val stack = inventory.getStack(i)
            finder.addNormalItem(stack)
        }
    }

    init{
        //coordinate system is in pixels, thank god
        //add top two imbuement slots
        val ofst = 0 //offset to get the slots drawing correctly, will attempt to fix later
        val ofst2 = 4 //offset based on guid picture change. 2x change for the inventory slots
        addSlot(object : Slot(inventory, 0, 8+ofst, 9+ofst2) {
            override fun canInsert(stack: ItemStack): Boolean { return true }
        })
        addSlot(object : Slot(inventory, 1, 95+ofst, 9+ofst2) {
            override fun canInsert(stack: ItemStack): Boolean { return true }
        })
        //add main imbuement crafting grid
        for (k in 0..2) {
            for(j in 0..2) {
                if (2+k+(3*j) == 6) {  //skipping the main middle slot, so I can set max count 1
                    addSlot(object : Slot(inventory, 2 + j + (3 * k), 30 + 21 * j + ofst, 13 + 21 * k + ofst2) {
                        override fun canInsert(stack: ItemStack): Boolean {
                            return true
                        }
                        override fun getMaxItemCount(): Int {
                            return 1
                        }
                    })
                }else {
                    addSlot(object : Slot(inventory, 2 + j + (3 * k), 30 + 21 * j + ofst, 13 + 21 * k + ofst2) {
                        override fun canInsert(stack: ItemStack): Boolean {
                            return true
                        }
                    })
                }
            }
        }


        //add bottom two imbuement slots
        addSlot(object : Slot(inventory, 11, 8+ofst, 59+ofst2) {
            override fun canInsert(stack: ItemStack): Boolean { return true }
        })
        addSlot(object : Slot(inventory, 12, 95+ofst, 59+ofst2) {
            override fun canInsert(stack: ItemStack): Boolean { return true }
        })
        //add the player main inventory
        for(j in 0..8){
            for(k in 0..2){
                addSlot(object : Slot(playerInventory, 9+j+(9*k), 38+(18*j)+ofst, 84+(18*k)+ofst2*2) {})
            }
        }
        //add the player hotbar
        for(j in 0..8) {
            addSlot(object : Slot(playerInventory, j, 38+(18*j)+ofst, 142+ofst2*2) {})
        }

        //add the properties for the three enchantment bars
        addProperty(seed).set(playerInventory.player.enchantmentTableSeed)
        addProperty(Property.create(this.enchantmentPower, 0))
        addProperty(Property.create(this.enchantmentPower, 1))
        addProperty(Property.create(this.enchantmentPower, 2))
        addProperty(Property.create(this.enchantmentId, 0))
        addProperty(Property.create(this.enchantmentId, 1))
        addProperty(Property.create(this.enchantmentId, 2))
        addProperty(Property.create(this.enchantmentLevel, 0))
        addProperty(Property.create(this.enchantmentLevel, 1))
        addProperty(Property.create(this.enchantmentLevel, 2))
        addProperty(Property.create(this.imbueId, 0))
        addProperty(Property.create(this.imbueId, 1))
        addProperty(Property.create(this.imbueId, 2))
        addProperty(Property.create(this.modId, 0))
        addProperty(Property.create(this.modId, 1))
        addProperty(Property.create(this.modId, 2))
        addProperty(Property.create(this.levelLow, 0))
        addProperty(Property.create(this.levelLow, 1))
        addProperty(Property.create(this.levelLow, 2))
    }

    companion object {

        private fun generateEnchantmentList(
            random: Random,
            stack: ItemStack,
            level: Int,
            treasureAllowed: Boolean
        ): ArrayList<EnchantmentLevelEntry> {
            var level1 = level
            val list = Lists.newArrayList<EnchantmentLevelEntry>()
            val item = stack.item
            val i = item.enchantability
            if (i <= 0) {
                return list
            }
            level1 += 1 + random.nextInt(i / 4 + 1) + random.nextInt(i / 4 + 1)
            val f = (random.nextFloat() + random.nextFloat() - 1.0f) * 0.15f
            val list2 = getPossibleEntries(
                MathHelper.clamp(
                    (level1.toFloat() + level1.toFloat() * f).roundToInt(),
                    1,
                    Int.MAX_VALUE
                ).also {
                    level1 = it
                }, stack, treasureAllowed
            )
            if (list2.isNotEmpty()) {
                Weighting.getRandom(random, list2).ifPresent { e: EnchantmentLevelEntry ->
                    list.add(
                        e
                    )
                }
                while (random.nextInt(50) <= level1) {
                    if (list.isNotEmpty()) {
                        EnchantmentHelper.removeConflicts(list2, Util.getLast(list))
                    }
                    if (list2.isEmpty()) break
                    Weighting.getRandom(random, list2).ifPresent { e: EnchantmentLevelEntry ->
                        list.add(
                            e
                        )
                    }
                    level1 /= 2
                }
            }
            return list
        }

        private fun getPossibleEntries(power: Int, stack: ItemStack, treasureAllowed: Boolean): ArrayList<EnchantmentLevelEntry> {
            val list = Lists.newArrayList<EnchantmentLevelEntry>()
            block0@ for (enchantment in Registry.ENCHANTMENT) {
                if (enchantment.isTreasure && !treasureAllowed || !enchantment.isAvailableForRandomSelection || !enchantment.isAcceptableItem(
                        stack
                    ) && !stack.isOf(Items.BOOK)
                ) continue
                for (i in enchantment.maxLevel downTo enchantment.minLevel - 1 + 1) {
                    if (power < enchantment.getMinPower(i) || power > enchantment.getMaxPower(i)) continue
                    list.add(EnchantmentLevelEntry(enchantment, i))
                    continue@block0
                }
            }
            return list
        }

    }

}