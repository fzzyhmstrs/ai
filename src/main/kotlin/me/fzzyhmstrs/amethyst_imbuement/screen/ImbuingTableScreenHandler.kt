package me.fzzyhmstrs.amethyst_imbuement.screen

import com.google.common.collect.Lists
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentModifier
import me.fzzyhmstrs.amethyst_core.modifier_util.ModifierHelper
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterHelper
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.LOGGER
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.block.ImbuingTableBlockEntity
import me.fzzyhmstrs.amethyst_imbuement.item.Reactant
import me.fzzyhmstrs.amethyst_imbuement.item.Reagent
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterCriteria
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterHandler
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import me.fzzyhmstrs.fzzy_core.nbt_util.Nbt
import me.fzzyhmstrs.fzzy_core.nbt_util.NbtKeys
import me.fzzyhmstrs.fzzy_core.registry.ModifierRegistry
import me.shedaniel.rei.api.common.transfer.RecipeFinder
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.block.EnchantingTableBlock
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.screen.ingame.EnchantingPhrases
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.Property
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.text.MutableText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import net.minecraft.util.collection.Weighting
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import kotlin.math.max
import kotlin.math.roundToInt

@Suppress("SENSELESS_COMPARISON", "unused", "SameParameterValue")
class ImbuingTableScreenHandler(
    syncID: Int,
    private val parentEntity: ImbuingTableBlockEntity?,
    playerInventory: PlayerInventory,
    private val inventory: ImbuingTableBlockEntity.ImbuingInventory,
    private val context: ScreenHandlerContext
):  ScreenHandler(RegisterHandler.IMBUING_SCREEN_HANDLER, syncID) {

    constructor(syncID: Int, playerInventory: PlayerInventory) : this(
        syncID,
        null,
        playerInventory,
        ImbuingTableBlockEntity.ImbuingInventory(13, null),
        ScreenHandlerContext.EMPTY,
    )
    fun getInventory(): Inventory{
        return inventory
    }
    private fun onParentChanged(parentInventory: Inventory){
        this.onContentChanged(parentInventory)
    }

    private val random = net.minecraft.util.math.random.LocalRandom(0L)
    val player: PlayerEntity = playerInventory.player
    private val logger = LOGGER
    private val seed = Property.create()
    val lapisSlot: Property = Property.create()
    val reroll: Property = Property.create()
    var results: List<TableResult> = listOf()
    var resultsIndexes =  intArrayOf(-1, -1, -1)
    var resultsCanUp = false
    var resultsCanDown = false
    private val listener = { inventory: Inventory -> onParentChanged(inventory) }

    val canUse: Property = Property.create()

    init{
        canUse.set(if(parentEntity?.checkCanUse(player.uuid) == true) 1 else 0)
        //coordinate system is in pixels, thank god
        //add top two imbuement slots
        val ofst = 0 //offset to get the slots drawing correctly, will attempt to fix later
        val ofst2 = 4 //offset based on guid picture change. 2x change for the inventory slots
        addSlot(ImbuingSlot(inventory, 0, 8+ofst, 9+ofst2, canUse))
        addSlot(ImbuingSlot(inventory, 1, 95+ofst, 9+ofst2, canUse))
        //add main imbuement crafting grid
        for (k in 0..2) {
            for(j in 0..2) {
                if (2+k+(3*j) == 6) {  //skipping the main middle slot, so I can set max count 1
                    addSlot(object : ImbuingSlot(inventory, 2 + j + (3 * k), 30 + 21 * j + ofst, 13 + 21 * k + ofst2, canUse) {
                        override fun getMaxItemCount(): Int {
                            return 1
                        }
                    })
                }else {
                    addSlot(ImbuingSlot(inventory, 2 + j + (3 * k), 30 + 21 * j + ofst, 13 + 21 * k + ofst2, canUse))
                }
            }
        }
        //add bottom two imbuement slots
        addSlot(ImbuingSlot(inventory, 11, 8+ofst, 59+ofst2, canUse) )
        addSlot(ImbuingSlot(inventory, 12, 95+ofst, 59+ofst2, canUse))
        //add the player main inventory
        for(j in 0..8){
            for(k in 0..2){
                addSlot(Slot(playerInventory, 9+j+(9*k), 38+(18*j)+ofst, 84+(18*k)+ofst2*2))
            }
        }
        //add the player hotbar
        for(j in 0..8) {
            addSlot(Slot(playerInventory, j, 38+(18*j)+ofst, 142+ofst2*2))
        }
        inventory.addListener(listener)
        //add the properties for the three enchantment bars
        addProperty(seed).set(playerInventory.player.enchantmentTableSeed)
        addProperty(lapisSlot).set(0)
        addProperty(reroll).set(0)
        addProperty(canUse)
        sendContentUpdates()
    }

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

    override fun onClosed(player: PlayerEntity) {
        super.onClosed(player)
        inventory.removeListener(listener)
        parentEntity?.clearInUse(player.uuid)
        //context.run { _: World?, _: BlockPos? -> dropInventory(player, inventory) }
    }

    fun requestContent(){
        ClientPlayNetworking.send(REQUEST_CONTENTS,PacketByteBufs.create())
    }

    override fun onContentChanged(inventory: Inventory) {
        if (player.world.isClient) return
        if (inventory === this.inventory) {
            val itemStack = inventory.getStack(6)
            val lapisStack = inventory.getStack(7)
            val lapisStackFull = if(lapisStack.isEmpty) {0} else {1}
            lapisSlot.set(lapisStackFull)
            reroll.set(0)
            results = listOf()
            resultsIndexes =  intArrayOf(-1, -1, -1)

            if (!itemStack.isEmpty) {
                context.run { world: World, pos: BlockPos ->
                    val tempResults: MutableList<TableResult> = mutableListOf()
                    var matches: List<ImbuingRecipe> = mutableListOf()
                    if (!world.isClient) {
                        matches = FzzyPort.getAllMatches(ImbuingRecipe.Type, inventory, world)
                    }
                    val shelves = checkBookshelves(world, pos)
                    if (shelves == 30 && player is ServerPlayerEntity){
                        RegisterCriteria.AMPED_UP.trigger(player)
                    }
                    if (matches.isNotEmpty()){
                        matches.forEach {  imbuingRecipe ->
                            val power = MathHelper.ceil(imbuingRecipe.getCost() * AiConfig.blocks.imbuing.difficultyModifier.get())
                            if (imbuingRecipe.getAugment() != "") {
                                val id = Identifier(imbuingRecipe.getAugment())
                                val augment = FzzyPort.ENCHANTMENT.get(id)
                                if (augment != null) {
                                    val augCheck = if (augment is ScepterAugment) {
                                        val blA = ScepterHelper.isAcceptableScepterItem(augment, itemStack, player)
                                        val blB = checkAcceptableAugment(augment,itemStack)
                                        Pair(blA, blB)
                                    } else {
                                        Pair(true, checkAcceptableAugment(augment,itemStack))
                                    }
                                    if (augCheck.first && augCheck.second) {
                                        val l = EnchantmentHelper.get(itemStack)
                                        var bl1 = false
                                        for (p in l.keys) {
                                            if (p === augment) {
                                                if (l[p] == augment.maxLevel) {
                                                    bl1 = true
                                                }
                                            }
                                        }
                                        if (!bl1) {
                                            tempResults.add(ImbuingResult(imbuingRecipe,power))
                                        }
                                    } else if (!augCheck.first && augCheck.second) {
                                        val str = augment.getName(1).string
                                        tempResults.add(LevelLowErrorResult(str,power))
                                    } else if (!augCheck.second) {
                                        val str = augment.getName(1).string
                                        tempResults.add(ScepterLowErrorResult(str,
                                            power))
                                    } else {
                                        tempResults.add(EmptyResult())
                                    }
                                } else if (ModifierRegistry.isModifier(id)) {
                                    val modifier = ModifierRegistry.getByType<AugmentModifier>(id)
                                    if (modifier != null) {
                                        val blA = modifier.isAcceptableItem(itemStack)
                                        val blB = modifier.getDescendant()?.modifierId
                                        if (blA && blB != null) {
                                            tempResults.add(ModifierResult(imbuingRecipe,blB.toString(),false, power))
                                        } else if (blA && blB == null) {
                                            tempResults.add(ModifierResult(imbuingRecipe,modifier.modifierId.toString(),true, power))
                                        } else {
                                            tempResults.add(EmptyResult())
                                        }
                                    } else {
                                        tempResults.add(EmptyResult())
                                    }
                                } else {
                                    logger.warning("Could not find augment or modifier under the key $id")
                                }
                            } else {
                                tempResults.add(ImbuingResult(imbuingRecipe,power))
                            }
                        }
                    } else if (!itemStack.isEnchantable){
                        for (i in 0..2) {
                            tempResults.add(EmptyResult())
                        }
                    } else if (AiConfig.blocks.imbuing.enchantingEnabled.get() && checkLapisAndSlots(inventory)) {
                        random.setSeed(seed.get().toLong())
                        val enchantmentPower = IntArray(3)
                        val enchantmentId = intArrayOf(-1, -1, -1)
                        val enchantmentLevel = intArrayOf(-1, -1, -1)
                        if (AiConfig.blocks.imbuing.getRerollEnabled()) {
                            val result = (if (lapisStack.count >= AiConfig.blocks.imbuing.getLapisCost()) 0 else -1) + (if (player.experienceLevel >= AiConfig.blocks.imbuing.getLevelCost()) 0 else -2)
                            reroll.set(if (result == 0) 1 else result)
                        }
                        var j = 0
                        while (j < 3) {
                            enchantmentPower[j] =
                                this.calculateRequiredExperienceLevel(random, j, shelves, itemStack)
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
                                FzzyPort.ENCHANTMENT.getRawId(enchantmentLevelEntry.enchantment)
                            enchantmentLevel[j] = enchantmentLevelEntry.level
                            ++j
                        }
                        for (e in -0..2){
                            if (enchantmentPower[e] <= 0){
                                tempResults.add(EmptyResult())
                                continue
                            }
                            tempResults.add(EnchantingResult(enchantmentPower[e], enchantmentId[e], enchantmentLevel[e], e))
                        }
                    } else {
                        for (i in 0..2) {
                            tempResults.add(EmptyResult())
                        }
                    }
                    results = tempResults
                    for (i in 0..2){
                        if (results.size >= i + 1){
                            if (results[i].power > 0){
                                resultsIndexes[i] = i
                            } else {
                                resultsIndexes[i] = -1
                            }
                        } else {
                            resultsIndexes[i] = -1
                        }
                    }
                    resultsCanUp = false
                    resultsCanDown = results.size > 3

                }
            }
            sendContentUpdates()
            sendPacket(player,this)
        }
    }
    override fun onButtonClick(player: PlayerEntity, id: Int): Boolean {
        if (id == 3){
            if (resultsIndexes[0] == 0) return false
            val max = results.size - 1
            for (i in 0..2){
                if (resultsIndexes[i] > 0 && resultsIndexes[i] != -1){
                    resultsIndexes[i] = resultsIndexes[i] - 1
                }
            }
            resultsCanUp = resultsIndexes[0] > 0
            resultsCanDown = resultsIndexes[2] < max
            sendPacket(player,this)
            context.run { world,pos->
                world.playSound(null,pos,SoundEvents.UI_BUTTON_CLICK.value(),SoundCategory.BLOCKS,0.5f,1.2f)
                world.playSound(null,pos,SoundEvents.BLOCK_AMETHYST_BLOCK_HIT,SoundCategory.BLOCKS,0.6f,0.6f + world.random.nextFloat() * 1.2f)
            }
            return true
        }
        if (id == 4){
            val max = results.size - 1
            if (resultsIndexes[2] == max) return false
            for (i in 0..2){
                if (resultsIndexes[i] < max && resultsIndexes[i] != -1){
                    resultsIndexes[i] = resultsIndexes[i] + 1
                }
            }
            resultsCanUp = resultsIndexes[0] > 0
            resultsCanDown = resultsIndexes[2] < max
            sendPacket(player,this)
            context.run { world,pos->
                world.playSound(null,pos,SoundEvents.UI_BUTTON_CLICK.value(),SoundCategory.BLOCKS,0.5f,1.2f)
                world.playSound(null,pos,SoundEvents.BLOCK_AMETHYST_BLOCK_HIT,SoundCategory.BLOCKS,0.6f,0.6f + world.random.nextFloat() * 1.2f)
            }
            return true
        }
        if (id == 5){
            return if (AiConfig.blocks.imbuing.getRerollEnabled() && !inventory.getStack(6).hasEnchantments()) {
                val result = inventory.getStack(7).count >= AiConfig.blocks.imbuing.getLapisCost() && player.experienceLevel >= AiConfig.blocks.imbuing.getLevelCost()
                if (result || player.isCreative){
                    player.applyEnchantmentCosts(ItemStack.EMPTY,AiConfig.blocks.imbuing.getLevelCost())
                    seed.set(player.enchantmentTableSeed)
                    val stack = inventory.getStack(7)
                    stack.decrement(AiConfig.blocks.imbuing.getLapisCost())
                    inventory.setStack(7,stack)
                    if (inventory.getStack(7).isEmpty){
                        inventory.setStack(7, ItemStack.EMPTY)
                    }
                    onContentChanged(inventory)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }
        if (resultsIndexes[id] == -1) return false
        val itemStack = inventory.getStack(6)
        val result = results[resultsIndexes[id]]
        var buttonWorked = true
        context.run { world: World, pos: BlockPos? ->
            buttonWorked = result.applyResult(player,itemStack,world,pos,this)
            if (buttonWorked) {
                if (player is ServerPlayerEntity) {
                    RegisterCriteria.IMBUING_USE.trigger(player)
                    onContentChanged(inventory)
                    world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.9f, world.random.nextFloat() * 0.1f + 0.9f)
                    world.playSound(null,pos,SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,SoundCategory.BLOCKS,2.0f,0.5f + world.random.nextFloat() * 1.2f)
                }
            }
        }
        return buttonWorked
    }

    override fun quickMove(player: PlayerEntity, index: Int): ItemStack? {
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
        for (blockPos in EnchantingTableBlock.POWER_PROVIDER_OFFSETS) {
            if (!EnchantingTableBlock.canAccessPowerProvider(world, pos, blockPos)) continue
            ++i
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

    companion object {

        private val RESULTS_PACKET = Identifier(AI.MOD_ID, "results_packet")
        private val REQUEST_CONTENTS = Identifier(AI.MOD_ID, "request_contents")
        private var lastPacketTime: Long = 0L

        fun registerClient(){
            ClientPlayNetworking.registerGlobalReceiver(RESULTS_PACKET) {client,_,buf,_ ->
                val player = client.player?:return@registerGlobalReceiver
                val world = client.world?:return@registerGlobalReceiver
                val syncID = buf.readInt()
                val handler = player.currentScreenHandler
                if (handler.syncId != syncID) return@registerGlobalReceiver
                if (handler !is ImbuingTableScreenHandler) return@registerGlobalReceiver
                val resultCount = buf.readByte().toInt()
                if (resultCount == 0){
                    handler.results = listOf()
                    handler.resultsIndexes =  intArrayOf(-1, -1, -1)
                    handler.resultsCanUp = false
                    handler.resultsCanDown = false
                    return@registerGlobalReceiver
                }
                val tempResults: MutableList<TableResult> = mutableListOf()
                for (i in 1..resultCount){
                    tempResults.add(TableResult.resultFromBuf(world, buf))
                }
                handler.results = tempResults
                val int1 = buf.readByte().toInt()
                val int2 = buf.readByte().toInt()
                val int3 = buf.readByte().toInt()
                handler.resultsIndexes = intArrayOf(int1, int2, int3)
                handler.resultsCanUp = buf.readBoolean()
                handler.resultsCanDown = buf.readBoolean()
            }
        }

        fun registerServer(){
            ServerPlayNetworking.registerGlobalReceiver(REQUEST_CONTENTS) {server,player,_,_,_ ->
                val handler = player.currentScreenHandler
                if (handler is ImbuingTableScreenHandler){
                    server.execute {
                        handler.onContentChanged(handler.inventory)
                    }
                }
            }
        }

        fun sendPacket(player: PlayerEntity, handler: ImbuingTableScreenHandler){
            if (player !is ServerPlayerEntity) return
            val buf = PacketByteBufs.create()
            buf.writeInt(handler.syncId)
            buf.writeByte(handler.results.size)
            for (result in handler.results){
                result.bufResultWriter(buf)
            }
            buf.writeByte(handler.resultsIndexes[0])
            buf.writeByte(handler.resultsIndexes[1])
            buf.writeByte(handler.resultsIndexes[2])
            buf.writeBoolean(handler.resultsCanUp)
            buf.writeBoolean(handler.resultsCanDown)

            ServerPlayNetworking.send(player, RESULTS_PACKET,buf)

        }

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
            block0@ for (enchantment in FzzyPort.ENCHANTMENT) {
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

        private fun checkAcceptableAugment(augment: Enchantment, stack: ItemStack): Boolean{
            val stackEnchants = EnchantmentHelper.get(stack)
            for (entry in stackEnchants){
                if (!entry.key.canCombine(augment)) return false
            }
            return augment.isAcceptableItem(stack)
        }
    }

    open class ImbuingSlot(inventory: Inventory, index: Int, x:Int, y: Int, private val canUse: Property): Slot(inventory, index, x, y) {
        private var locked = false

        override fun canInsert(stack: ItemStack?): Boolean {
            return canUse.get() == 1
        }

        override fun canTakePartial(player: PlayerEntity?): Boolean {
            return canUse.get() == 1
        }

        override fun canTakeItems(playerEntity: PlayerEntity?): Boolean {
            return canUse.get() == 1
        }

        override fun setStack(stack: ItemStack) {
            if (!locked) {
                super.setStack(stack)
            } else {
                inventory.setStack(index, stack)
            }
        }

        fun setLocked(lock: Boolean){
            locked = lock
        }
    }

    interface TableResult{
        val type: Int
        val power: Int
        val lapis: Int

        fun bufResultWriter(buf: PacketByteBuf){
            buf.writeByte(type)
            bufClassWriter(buf)
        }

        fun bufClassWriter(buf: PacketByteBuf)

        fun bufClassReader(world: World,buf: PacketByteBuf): TableResult

        fun buttonStringVisitable(textRenderer: TextRenderer, width: Int): StringVisitable

        fun isReady(id: Int, player: PlayerEntity, handler: ImbuingTableScreenHandler): Boolean

        fun applyResult(player: PlayerEntity, stack: ItemStack, world: World, pos: BlockPos?, handler: ImbuingTableScreenHandler): Boolean

        fun tooltipList(player: PlayerEntity, handler: ImbuingTableScreenHandler): List<Text>

        fun nextRecipeTooltipText(player: PlayerEntity, handler: ImbuingTableScreenHandler): Text

        companion object{

            private val resultTypes = mapOf(
                0 to EnchantingResult(0, 0, 0, 0),
                1 to ImbuingResult(ImbuingRecipe.blankRecipe(),0),
                2 to ModifierResult(ImbuingRecipe.blankRecipe(),"",false,0),
                3 to EmptyResult(),
                4 to LevelLowErrorResult("",0),
                5 to ScepterLowErrorResult("", 0)
            )

            fun resultFromBuf(world: World,buf: PacketByteBuf): TableResult{
                val type = buf.readByte().toInt()
                return resultFromBuf(type, world, buf)
            }

            private fun resultFromBuf(type: Int, world: World, buf: PacketByteBuf): TableResult{
                return resultTypes[type]?.bufClassReader(world, buf) ?: EmptyResult()
            }
        }
    }

    class EmptyResult: TableResult {
        override val type: Int = 3
        override val power: Int = 0
        override val lapis: Int = 0

        override fun bufClassWriter(buf: PacketByteBuf) {
        }

        override fun bufClassReader(world: World, buf: PacketByteBuf): TableResult {
            return EmptyResult()
        }

        override fun buttonStringVisitable(textRenderer: TextRenderer, width: Int): StringVisitable {
            return StringVisitable.EMPTY
        }

        override fun isReady(id: Int, player: PlayerEntity, handler: ImbuingTableScreenHandler): Boolean {
            return false
        }

        override fun applyResult(
            player: PlayerEntity,
            stack: ItemStack,
            world: World,
            pos: BlockPos?,
            handler: ImbuingTableScreenHandler
        ): Boolean {
            return false
        }

        override fun tooltipList(player: PlayerEntity, handler: ImbuingTableScreenHandler): List<Text> {
            return listOf()
        }

        override fun nextRecipeTooltipText(player: PlayerEntity, handler: ImbuingTableScreenHandler): Text {
            return AcText.empty()
        }
    }

    class EnchantingResult(override val power: Int, val id: Int, val level: Int, val slot: Int): TableResult{

        override val type: Int = 0
        private val etdLoaded = FabricLoader.getInstance().isModLoaded("enchanting-table-descriptions")

        override val lapis: Int = when(power){
            in 1..10 -> 1
            in 11..20 -> 2
            in 21..30 -> 3
            in 31..40 -> 4
            in 41..50 -> 5
            in 51..60 -> 6
            else -> 1
        }

        override fun bufClassWriter(buf: PacketByteBuf) {
            buf.writeShort(power)
            buf.writeShort(id)
            buf.writeByte(level)
            buf.writeByte(slot)
        }

        override fun bufClassReader(world: World, buf: PacketByteBuf): TableResult {
            val pow = buf.readShort().toInt()
            val i = buf.readShort().toInt()
            val lvl = buf.readByte().toInt()
            val slt = buf.readByte().toInt()
            return EnchantingResult(pow,i,lvl, slt)
        }

        override fun buttonStringVisitable(textRenderer: TextRenderer, width: Int): StringVisitable {
            return EnchantingPhrases.getInstance().generatePhrase(textRenderer, width)
        }

        override fun isReady(id: Int, player: PlayerEntity, handler: ImbuingTableScreenHandler): Boolean {
            val bl = handler.getLapisCount() >= lapis
            val bl2 = (player.experienceLevel >= lapis && player.experienceLevel >= power) || player.abilities.creativeMode
            return bl && bl2
        }

        override fun applyResult(
            player: PlayerEntity,
            stack: ItemStack,
            world: World,
            pos: BlockPos?,
            handler: ImbuingTableScreenHandler
        ): Boolean {
            if (world.isClient) return true
            if ((player.experienceLevel < lapis || player.experienceLevel < power || handler.getLapisCount() < lapis) && !player.abilities.creativeMode) return false
            var itemStack3 = stack
            val list = handler.generateEnchantments(itemStack3, slot, power)
            if (list.isNotEmpty()) {
                player.applyEnchantmentCosts(itemStack3, lapis)
                val bl = itemStack3.isOf(Items.BOOK)
                if (bl) {
                    itemStack3 = ItemStack(Items.ENCHANTED_BOOK)
                    val nbtCompound = stack.nbt
                    if (nbtCompound != null) {
                        itemStack3.nbt = nbtCompound.copy()
                    }
                    handler.inventory.setStack(6, itemStack3)
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
                    val itemStack2 = handler.inventory.getStack(7)
                    itemStack2.decrement(lapis)
                    if (itemStack2.isEmpty) {
                        handler.inventory.setStack(7, ItemStack.EMPTY)
                    }
                }
                player.incrementStat(Stats.ENCHANT_ITEM)
                if (player is ServerPlayerEntity) {
                    Criteria.ENCHANTED_ITEM.trigger(player, itemStack3, lapis)
                }
                handler.seed.set(player.enchantmentTableSeed)
            }
            return true
        }

        override fun tooltipList(player: PlayerEntity, handler: ImbuingTableScreenHandler): List<Text> {
            val list: MutableList<Text> = mutableListOf()
            val enchantment = Enchantment.byRawId(id)
            if (enchantment != null) {
                list.add(AcText.translatable("container.enchant.clue", enchantment.getName(level)).formatted(Formatting.WHITE))
                if (etdLoaded){
                    val descKey = enchantment.translationKey + ".desc"
                    val descText = AcText.translatable(descKey)
                    if (descKey != descText.string){
                        list.add(descText.formatted(Formatting.DARK_GRAY))
                    }
                }
            }
            if (!player.abilities.creativeMode) {
                list.add(AcText.empty())
                if (player.experienceLevel < power) {
                    list.add(
                        AcText.translatable(
                            "container.enchant.level.requirement",
                            power
                        ).formatted(Formatting.RED)
                    )
                } else {
                    val mutableText = if (lapis == 1) AcText.translatable("container.enchant.lapis.one") else AcText.translatable(
                        "container.enchant.lapis.many",
                        lapis
                    )
                    list.add(mutableText.formatted(if (handler.getLapisCount() >= lapis) Formatting.GRAY else Formatting.RED))
                    val mutableText2 =
                        if (lapis == 1) AcText.translatable("container.enchant.level.one") else AcText.translatable(
                            "container.enchant.level.many",
                            lapis
                        )
                    list.add(mutableText2.formatted(Formatting.GRAY))
                }
            }
            return list
        }

        override fun nextRecipeTooltipText(player: PlayerEntity, handler: ImbuingTableScreenHandler): Text {
            val enchantment = Enchantment.byRawId(id)
            return if (enchantment != null) {
                AcText.translatable("container.enchant.clue", enchantment.getName(level)).formatted(Formatting.WHITE)
            } else {
                AcText.empty()
            }
        }

    }

    class ImbuingResult(val recipe: ImbuingRecipe, override val power: Int): TableResult{

        override val type: Int = 1
        override val lapis = MathHelper.ceil(recipe.getCost() * AiConfig.blocks.imbuing.difficultyModifier.get())
        private val etdLoaded = FabricLoader.getInstance().isModLoaded("enchanting-table-descriptions")

        override fun bufClassWriter(buf: PacketByteBuf) {
            buf.writeIdentifier(recipe.id)
            buf.writeShort(power)
        }

        override fun bufClassReader(world: World, buf: PacketByteBuf): TableResult {
            val recipeId = buf.readIdentifier()
            val pow = buf.readShort().toInt()
            val opt = FzzyPort.getRecipe(recipeId, world.recipeManager)
            val recipe = if(opt.isPresent){
                opt.get()
            } else {
                null
            } ?: return EmptyResult()
            return if (recipe is ImbuingRecipe){
                ImbuingResult(recipe, pow)
            } else {
                EmptyResult()
            }
        }

        override fun buttonStringVisitable(textRenderer: TextRenderer, width: Int): StringVisitable {
            return if(recipe.getAugment() != "") {
                val augId = Identifier(recipe.getAugment())
                val str = FzzyPort.ENCHANTMENT.get(augId)?.getName(1)?.string
                    ?: AcText.translatable("container.disenchanting_table.button.missing_enchantment").toString()
                AcText.literal(str).fillStyle(Style.EMPTY.withFont(Identifier("minecraft", "default")))
            } else {
                val str = AcText.translatable("container.imbuing_table.item_result", recipe.getCount(), recipe.getOutput().item.name).string
                AcText.literal(str).fillStyle(Style.EMPTY.withFont(Identifier("minecraft", "default")))
            }
        }

        override fun isReady(id: Int, player: PlayerEntity, handler: ImbuingTableScreenHandler): Boolean {
            return player.experienceLevel >= power || player.abilities.creativeMode
        }

        override fun applyResult(
            player: PlayerEntity,
            stack: ItemStack,
            world: World,
            pos: BlockPos?,
            handler: ImbuingTableScreenHandler
        ): Boolean {
            var itemStack3 = stack
            val i = lapis
            if ((player.experienceLevel < i || player.experienceLevel < power) && !player.abilities.creativeMode) return false
            if(recipe.getAugment() != ""){
                val augId = Identifier(recipe.getAugment())
                val augmentChk = FzzyPort.ENCHANTMENT.get(augId) ?: return false
                if (checkAcceptableAugment(augmentChk,itemStack3)){
                    val item = itemStack3.item
                    if (item is Reactant){
                        if (!item.canReact(itemStack3, Reagent.getReagents(handler.inventory), player, ImbuingRecipe.Type)){
                            println("Item recipe has reagents [${Reagent.getReagents(handler.inventory)}] that can't react with the reactant [${itemStack3}]!")
                            return false
                        }
                    }
                    val bl = itemStack3.isOf(Items.BOOK)
                    if (bl) {
                        itemStack3 = ItemStack(Items.ENCHANTED_BOOK)
                        val nbtCompound = stack.nbt
                        if (nbtCompound != null) {
                            itemStack3.nbt = nbtCompound.copy()
                        }
                        handler.inventory.setStack(6, itemStack3)
                        player.applyEnchantmentCosts(itemStack3, i)
                        EnchantedBookItem.addEnchantment(itemStack3, EnchantmentLevelEntry(augmentChk,1))

                    } else {
                        val l = EnchantmentHelper.get(itemStack3)
                        var r: Int
                        var bl1 = false
                        for (p in l.keys) {
                            if (p == null) continue
                            if(p === augmentChk){
                                r = l[p]?.plus(1) ?: return false
                                if (r > augmentChk.maxLevel){
                                    return false
                                }
                                l[p] = r
                                bl1 = true
                            }
                        }
                        player.applyEnchantmentCosts(itemStack3, i)
                        if(bl1) {
                            EnchantmentHelper.set(l, itemStack3)
                        } else{
                            itemStack3.addEnchantment(augmentChk,1)
                        }
                    }
                    val reagents = if (item is Reactant){
                        Reagent.getReagents(handler.inventory).stream().map{ it.copy() }.toList()
                    } else {
                        listOf()
                    }
                    for (j in 0..12) { //decrement inventory slots even for creative mode!
                        if (j == 6 || player.abilities.creativeMode) continue //avoid bulldozing itemslot 6
                        if (handler.inventory.getStack(j).item.hasRecipeRemainder()){
                            handler.inventory.setStack(j, ItemStack(handler.inventory.getStack(j).item.recipeRemainder, 1))
                        }else {
                            handler.inventory.getStack(j).decrement(1)
                            if (handler.inventory.getStack(j).isEmpty) {
                                handler.inventory.setStack(j, ItemStack.EMPTY)
                            }
                        }
                    }
                    if (item is Reactant){
                        item.react(itemStack3, reagents)
                    }
                } else {
                    return false
                }
                player.incrementStat(Stats.ENCHANT_ITEM)
                if (player is ServerPlayerEntity) {
                    RegisterCriteria.AUGMENT_ITEM.trigger(player, augmentChk)
                    Criteria.ENCHANTED_ITEM.trigger(player, itemStack3, i)
                }
            } else{
                val itemStack4 = recipe.getOutput()
                val item = itemStack4.item
                if (item is Reactant){
                    if (!item.canReact(itemStack4, Reagent.getReagents(handler.inventory), handler.player, ImbuingRecipe.Type)){
                        println("Item recipe has reagents [${Reagent.getReagents(handler.inventory)}] that can't react with the reactant [${itemStack4}]!")
                        return false
                    }
                }
                player.applyEnchantmentCosts(itemStack3, i)
                val reagents = if (item is Reactant){
                    Reagent.getReagents(handler.inventory).stream().map{ it.copy() }.toList()
                } else {
                    listOf()
                }
                for (j in 0..12) {
                    if (j != 6 && player.abilities.creativeMode) continue //only decrement the middle slot if its creative mode, to make way for the new item stack
                    if (handler.inventory.getStack(j).item.hasRecipeRemainder()){
                        handler.inventory.setStack(j, ItemStack(handler.inventory.getStack(j).item.recipeRemainder, 1))
                    }else {
                        handler.inventory.getStack(j).decrement(1)
                        if (handler.inventory.getStack(j).isEmpty) {
                            handler.inventory.setStack(j, ItemStack.EMPTY)
                        }
                    }
                }

                val itemStack5 = if (recipe.getTransferEnchant()){
                    val nbtTemp = itemStack3.orCreateNbt.copy()
                    nbtTemp.remove(NbtKeys.ITEM_STACK_ID.str())
                    Nbt.createItemStackWithNbt(itemStack4.item,itemStack4.count,nbtTemp)
                } else {
                    itemStack4.copy()
                }
                if (item is Reactant){
                    item.react(itemStack5, reagents, handler.player,ImbuingRecipe.Type)
                }
                handler.inventory.setStack(6,itemStack5)
                itemStack5.onCraft(world, player,itemStack5.count)
            }
            return true
        }

        override fun tooltipList(player: PlayerEntity, handler: ImbuingTableScreenHandler): List<Text> {
            val list: MutableList<Text> = mutableListOf()
            if (recipe.getAugment() != "") {
                val augment = FzzyPort.ENCHANTMENT.get(Identifier(recipe.getAugment()))
                if (augment != null){
                    list.add(AcText.translatable("container.enchant.clue", (augment.getName(1) as MutableText).formatted(Formatting.WHITE)))
                    if (etdLoaded){
                        val descKey = augment.translationKey + ".desc"
                        val descText = AcText.translatable(descKey)
                        if (descKey != descText.string){
                            list.add(descText.formatted(Formatting.DARK_GRAY))
                        }
                    }
                    list.add(AcText.empty())
                }
            } else {
                list.add(AcText.translatable("container.imbuing_table.item_result", recipe.getCount(), recipe.getOutput().item.name))
                list.add(AcText.empty())
            }
            if (player.experienceLevel < power){
                list.add(AcText.translatable("container.enchant.level.requirement", power).formatted(Formatting.RED))
            } else {
                val mutableText2 =
                    if (power == 1) AcText.translatable("container.enchant.level.one") else AcText.translatable(
                        "container.enchant.level.many",
                        power
                    )
                list.add(mutableText2.formatted(Formatting.GRAY))
            }
            return list
        }

        override fun nextRecipeTooltipText(player: PlayerEntity, handler: ImbuingTableScreenHandler): Text {
            return if (recipe.getAugment() != "") {
                val augment = FzzyPort.ENCHANTMENT.get(Identifier(recipe.getAugment()))
                if (augment != null){
                    AcText.translatable("container.enchant.clue", (augment.getName(1) as MutableText).formatted(Formatting.WHITE))
                } else {
                    AcText.empty()
                }
            } else {
                AcText.translatable("container.imbuing_table.item_result", recipe.getCount(), recipe.getOutput().item.name)
            }
        }

    }

    class ModifierResult(val recipe: ImbuingRecipe, private val nextInLine: String, private val lineageMax: Boolean, override val power: Int): TableResult{
        override val type: Int = 2
        override val lapis: Int = MathHelper.ceil(recipe.getCost() * AiConfig.blocks.imbuing.difficultyModifier.get())

        override fun bufClassWriter(buf: PacketByteBuf) {
            buf.writeIdentifier(recipe.id)
            buf.writeString(nextInLine)
            buf.writeBoolean(lineageMax)
            buf.writeShort(power)
        }

        override fun bufClassReader(world: World, buf: PacketByteBuf): TableResult {
            val recipeId = buf.readIdentifier()
            val nextInLine = buf.readString()
            val lineage = buf.readBoolean()
            val pow = buf.readShort().toInt()
            val opt = FzzyPort.getRecipe(recipeId, world.recipeManager)
            val recipe = if(opt.isPresent){
                opt.get()
            } else {
                null
            } ?: return EmptyResult()
            return if (recipe is ImbuingRecipe){
                ModifierResult(recipe,nextInLine,lineage, pow)
            } else {
                EmptyResult()
            }
        }

        override fun buttonStringVisitable(textRenderer: TextRenderer, width: Int): StringVisitable {
            val str = AcText.translatable("scepter.modifier.$nextInLine").string
            return AcText.literal(str).fillStyle(Style.EMPTY.withFont(Identifier("minecraft", "default")))
        }

        override fun isReady(id: Int, player: PlayerEntity, handler: ImbuingTableScreenHandler): Boolean {
            return (player.experienceLevel >= power || player.abilities.creativeMode) && !lineageMax
        }

        override fun applyResult(
            player: PlayerEntity,
            stack: ItemStack,
            world: World,
            pos: BlockPos?,
            handler: ImbuingTableScreenHandler
        ): Boolean {
            val i = lapis
            if ((player.experienceLevel < i || player.experienceLevel < power)  && !player.abilities.creativeMode) return false
            if (lineageMax) return false
            if(recipe.getAugment() != ""){
                val augId = Identifier(recipe.getAugment())
                val modChk = ModifierRegistry.getByType<AugmentModifier>(augId) ?: return false
                if (modChk.isAcceptableItem(stack)){
                    if (ModifierHelper.addModifier(augId, stack)){
                        player.applyEnchantmentCosts(stack, i)
                        for (j in 0..12) { //decrement inventory slots even for creative mode!
                            if (j == 6 || player.abilities.creativeMode) continue //avoid bulldozing itemslot 6
                            if (handler.inventory.getStack(j).item.hasRecipeRemainder()){
                                handler.inventory.setStack(j, ItemStack(handler.inventory.getStack(j).item.recipeRemainder, 1))
                            }else {
                                handler.inventory.getStack(j).decrement(1)
                                if (handler.inventory.getStack(j).isEmpty) {
                                    handler.inventory.setStack(j, ItemStack.EMPTY)
                                }
                            }
                        }
                    } else {
                        return false
                    }
                } else {
                    return false
                }
                player.incrementStat(Stats.ENCHANT_ITEM)
                if (player is ServerPlayerEntity) {
                    Criteria.ENCHANTED_ITEM.trigger(player, stack, i)
                }
            }
            return true
        }

        override fun tooltipList(player: PlayerEntity, handler: ImbuingTableScreenHandler): List<Text> {
            val list: MutableList<Text> = mutableListOf()
            val textName: MutableText = AcText.translatable("scepter.modifier.$nextInLine")
            if (!lineageMax){
                list.add(AcText.translatable("container.enchant.clue", textName.formatted(Formatting.WHITE)))
                list.add(AcText.empty())
            } else {
                list.add(AcText.translatable("container.imbuing_table.modifier_max").formatted(Formatting.RED))
                list.add(AcText.translatable("container.imbuing_table.next_recipe_1", AcText.translatable("scepter.modifier.$nextInLine")).formatted(Formatting.GRAY))
                list.add(AcText.empty())
            }
            if (player.experienceLevel < power){
                list.add(AcText.translatable("container.enchant.level.requirement", power).formatted(Formatting.RED))
            } else {
                val mutableText2 =
                    if (power == 1) AcText.translatable("container.enchant.level.one") else AcText.translatable(
                        "container.enchant.level.many",
                        power
                    )
                list.add(mutableText2.formatted(Formatting.GRAY))
            }
            return list
        }

        override fun nextRecipeTooltipText(player: PlayerEntity, handler: ImbuingTableScreenHandler): Text {
            val textName: MutableText = AcText.translatable("scepter.modifier.$nextInLine")
            return AcText.translatable("container.enchant.clue", textName.formatted(Formatting.WHITE))
        }
    }

    abstract class ErrorResult(private val error: List<Text>,
                      private val nextRecipeError: Text,
                      private val stringVisitable: StringVisitable,
                      override val lapis: Int,
                      override val power: Int): TableResult{
        override val type: Int = -1

        override fun buttonStringVisitable(textRenderer: TextRenderer, width: Int): StringVisitable {
            return stringVisitable
        }

        override fun isReady(id: Int, player: PlayerEntity, handler: ImbuingTableScreenHandler): Boolean {
            return false
        }

        override fun applyResult(
            player: PlayerEntity,
            stack: ItemStack,
            world: World,
            pos: BlockPos?,
            handler: ImbuingTableScreenHandler
        ): Boolean {
            return false
        }

        override fun tooltipList(player: PlayerEntity, handler: ImbuingTableScreenHandler): List<Text> {
            return error
        }

        override fun nextRecipeTooltipText(player: PlayerEntity, handler: ImbuingTableScreenHandler): Text {
            return nextRecipeError
        }

    }

    class LevelLowErrorResult(private val augment: String, pow: Int): ErrorResult(listOf(AcText.translatable("container.imbuing_table.level_low").formatted(Formatting.RED)),
        AcText.literal(augment),
        AcText.literal(augment).fillStyle(Style.EMPTY.withFont(Identifier("minecraft", "default"))),
        pow,
        pow) {

        override val type: Int = 4

        override fun bufClassWriter(buf: PacketByteBuf) {
            buf.writeString(augment)
            buf.writeShort(power)
        }

        override fun bufClassReader(world: World, buf: PacketByteBuf): TableResult {
            val aug = buf.readString()
            val pow = buf.readShort().toInt()
            return LevelLowErrorResult(aug, pow)
        }
    }

    class ScepterLowErrorResult(private val augment: String, pow: Int): ErrorResult(listOf(AcText.translatable("container.imbuing_table.scepter_low").formatted(Formatting.RED)),
        AcText.literal(augment),
        AcText.literal(augment).fillStyle(Style.EMPTY.withFont(Identifier("minecraft", "default"))),
        pow,
        pow) {

        override val type: Int = -5

        override fun bufClassWriter(buf: PacketByteBuf) {
            buf.writeString(augment)
            buf.writeShort(power)
        }

        override fun bufClassReader(world: World, buf: PacketByteBuf): TableResult {
            val aug = buf.readString()
            val pow = buf.readShort().toInt()
            return LevelLowErrorResult(aug, pow)
        }
    }

}
