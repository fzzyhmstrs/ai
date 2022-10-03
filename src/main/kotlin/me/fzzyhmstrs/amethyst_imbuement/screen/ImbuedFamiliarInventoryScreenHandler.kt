package me.fzzyhmstrs.amethyst_imbuement.screen

import me.fzzyhmstrs.amethyst_core.coding_util.AcText
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.ImbuedFamiliarEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterHandler
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.passive.CatVariant
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.Property
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.Slot
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

private fun buildFamiliar(buf: PacketByteBuf, world: World): ImbuedFamiliarEntity{
    val id = buf.readInt() //familiar ID 2
    val entity = world.getEntityById(id)
    val fallback = ImbuedFamiliarEntity(RegisterEntity.IMBUED_FAMILIAR_ENTITY,
        world,
        null,
        buf.readDouble(), //modDamage
        buf.readDouble(), //modHealth
        buf.readInt(), //invSlots
        buf.readInt(), //level
        buf.readInt(), //followMode
        buf.readInt()) //attackMode
    return if (entity is ImbuedFamiliarEntity){entity} else {fallback}

}

class ImbuedFamiliarInventoryScreenHandler(
    syncId: Int,
    val familiarId: Int,
    private val familiar: ImbuedFamiliarEntity,
    playerInventory: PlayerInventory,
    private val context: ScreenHandlerContext):
    ScreenHandler(RegisterHandler.FAMILIAR_SCREEN_HANDLER,syncId) {

        constructor(syncId: Int, playerInventory: PlayerInventory, buf: PacketByteBuf):
                this(syncId,
                    buf.readInt(), //familiarId
                    buildFamiliar(buf,playerInventory.player.world),
                    playerInventory,
                    ScreenHandlerContext.EMPTY)

    private val inventory: Inventory = familiar.items
    val cat: Property = Property.create()
    val followMode: Property = Property.create()
    val attackMode: Property = Property.create()

    init{
        cat.set(catMap.getOrDefault(familiar.getVariant(),0))
        addSlot(object : FamiliarSlot(inventory, 0, 8, 18) {
            override fun canInsert(stack: ItemStack): Boolean {
                return familiar.isHorseArmor(stack)
            }

            override fun isEnabled(): Boolean {
                return true
            }

            override fun getMaxItemCount(): Int {
                return 1
            }
        })
        for (i in 1 until inventory.size()){
            val xOffset = (i-1)/3
            val yOffset = (i-1)%3
            addSlot(FamiliarSlot(inventory, i, 80 + xOffset * 18, 18 + yOffset * 18))

        }
        var k = 0
        while (k < 3) {
            var l = 0
            while (l < 9) {
                addSlot(Slot(playerInventory, l + k * 9 + 9, 8 + l * 18, 102 + k * 18 + -18))
                ++l
            }
            ++k
        }
        k = 0
        while (k < 9) {
            addSlot(Slot(playerInventory, k, 8 + k * 18, 142))
            ++k
        }
        addProperty(cat)
        addProperty(followMode)
        addProperty(attackMode)
        sendContentUpdates()
    }


    override fun transferSlot(player: PlayerEntity, index: Int): ItemStack {
        var itemStack = ItemStack.EMPTY
        val slot = slots[index]
        if (slot.hasStack()) {
            val itemStack2 = slot.stack
            itemStack = itemStack2.copy()
            val i = inventory.size()
            if (index < i) {
                if (!insertItem(itemStack2, i, slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (getSlot(0).canInsert(itemStack2)) {
                if (!insertItem(itemStack2, 0, 1, false)) {
                    return ItemStack.EMPTY
                }
            } else if (i <= 2 || !insertItem(itemStack2, 2, i, false)) {
                val k: Int = i + 27
                val m = k + 9
                return if (when (index) {
                    in k until m -> !insertItem(
                        itemStack2,
                        i,
                        k,
                        false
                    )
                    in i until k -> !insertItem(itemStack2, k, m, false)
                    else -> !insertItem(
                        itemStack2,
                        k,
                        k,
                        false
                    )
                }
                ) {
                    ItemStack.EMPTY
                } else ItemStack.EMPTY
            }
            if (itemStack2.isEmpty) {
                slot.stack = ItemStack.EMPTY
            } else {
                slot.markDirty()
            }
        }
        return itemStack
    }

    fun disableFamiliarSlots(){
        for (i in 0 until inventory.size()){
            val slot = slots[i]
            if (slot is FamiliarSlot){
                slot.disable()
            }
        }
    }
    fun enableFamiliarSlots(){
        for (i in 0 until inventory.size()){
            val slot = slots[i]
            if (slot is FamiliarSlot){
                slot.enable()
            }
        }
    }
    fun getChestSlots(): Int{
        return inventory.size()-1
    }


    override fun canUse(player: PlayerEntity): Boolean {
        return true
    }

    override fun close(player: PlayerEntity?) {
        this.inventory.onClose(player)
        super.close(player)
    }

    internal fun processUpdate(nameCheck: String, variantCheck: Identifier, followCheck: ImbuedFamiliarEntity.FollowMode, attackCheck: ImbuedFamiliarEntity.AttackMode){

        if (nameCheck != familiar.name.string){
            familiar.customName = AcText.literal(nameCheck)
        }
        if (variantCheck != Registry.CAT_VARIANT.getId(familiar.getVariant())){
            println("whee")
            familiar.setVariant(Registry.CAT_VARIANT.get(variantCheck)?:familiar.getVariant())
        }
        if (followCheck != familiar.followMode){
            familiar.followMode = followCheck
        }
        if (attackCheck != familiar.attackMode){
            familiar.attackMode = attackCheck
        }
    }

    private open class FamiliarSlot(inventory: Inventory, index: Int, x: Int, y: Int): Slot(inventory,index, x, y){
        private var enabled = true

        fun disable(){
            enabled = false
        }

        fun enable(){
            enabled = true
        }

        override fun isEnabled(): Boolean {
            return enabled
        }

    }

    companion object{

        val catMap: Map<CatVariant,Int> = mapOf(
            CatVariant.ALL_BLACK to 0,
            CatVariant.BLACK to 1,
            CatVariant.BRITISH_SHORTHAIR to 2,
            CatVariant.CALICO to 3,
            CatVariant.JELLIE to 4,
            CatVariant.PERSIAN to 5,
            CatVariant.RAGDOLL to 6,
            CatVariant.RED to 7,
            CatVariant.SIAMESE to 8,
            CatVariant.TABBY to 9,
            CatVariant.WHITE to 10
        )

        val UPDATE_FAMILIAR =Identifier(AI.MOD_ID,"update_familiar")
        fun registerServer(){
            ServerPlayNetworking.registerGlobalReceiver(UPDATE_FAMILIAR) { server, player, _, buf, _ ->
                val handler = player.currentScreenHandler
                if (handler is ImbuedFamiliarInventoryScreenHandler){
                    val nameCheck = buf.readString()
                    val variantCheck = buf.readIdentifier()
                    val followCheck = handler.familiar.followMode.fromIndex(buf.readByte().toInt())
                    val attackCheck = handler.familiar.attackMode.fromIndex(buf.readByte().toInt())
                    server.execute {
                        handler.processUpdate(nameCheck, variantCheck, followCheck, attackCheck)
                    }
                }
            }
        }


    }

}