package me.fzzyhmstrs.amethyst_imbuement.item.book

import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentBookItem
import me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.addIfDistinct
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.item.GlisteringKeyItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterSound
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTag
import me.fzzyhmstrs.amethyst_imbuement.screen.KnowledgeBookScreen
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*
import java.util.function.Supplier

class BookOfTalesItem(settings: Settings) : AbstractAugmentBookItem(settings), BookOfKnowledge, GlisteringKeyItem.GlisteringKeyUnlockable {
    override val loreTier: LoreTier = TALES_TIER
    override val bindingUV: Pair<Int, Int> = Pair(81,184)

    object TALES_TIER: LoreTier() {
        private val secretList: MutableList<String> =  mutableListOf()

        override fun addToList(string: String) {
            secretList.addIfDistinct(string)
        }
        override fun list(): List<String> {
            return secretList
        }
    }

    companion object{
        private val clientLastReadMap: MutableMap<UUID,String> = mutableMapOf()
        private val serverLastReadMap: MutableMap<UUID,String> = mutableMapOf()

        private val TALES_SYNC = Identifier(AI.MOD_ID, "tales_sync")
        
        fun hasReadLast(player: PlayerEntity,tale: String): Boolean{
            if (player.world.isClient()){
                if(clientLastReadMap[player.uuid] == tale) return true
                clientLastReadMap[player.uuid] = tale
                return false
            } else {
                if(serverLastReadMap[player.uuid] == tale) return true
                serverLastReadMap[player.uuid] = tale
                return false
            }
        }

        fun saveToPlayer(player: ServerPlayerEntity, nbt: NbtCompound){
            val tale = serverLastReadMap[player.uuid]
            if (tale != null){
                nbt.putString("BookOfTalesLastRead", tale)
            }
        }

        fun loadFromPlayer(player: ServerPlayerEntity, nbt: NbtCompound){
            if (nbt.contains("BookOfTalesLastRead")){
                val tale = nbt.getString("BookOfTalesLastRead")
                serverLastReadMap[player.uuid] = tale
                val buf = PacketByteBufs.create()
                buf.writeString(tale)
                ServerPlayNetworking.send(player, TALES_SYNC, buf)
            }
        }

        fun registerClient(){
            ClientPlayNetworking.registerGlobalReceiver(TALES_SYNC){ client, _, buf, _ ->
                val player = client.player ?: return@registerGlobalReceiver
                val tale = buf.readString()
                clientLastReadMap[player.uuid] = tale
            }
        }
        
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
        val nbt = stack.nbt ?: return TypedActionResult.fail(stack)
        if (hand == Hand.OFF_HAND) return TypedActionResult.fail(stack)
        if (!nbt.getBoolean("unlocked")) {
            user.sendMessage(AcText.translatable(this.translationKey + ".locked"), true)
            if (world.isClient)
                user.playSound(RegisterSound.LOCKED_BOOK, SoundCategory.PLAYERS, 1.0f, 1.2f)
            return TypedActionResult.fail(stack)
        }
        return super.use(world, user, hand)
    }

    override fun useAfterWriting(
        stack: ItemStack,
        world: World,
        user: PlayerEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        val nbt = stack.nbt
        if (nbt != null){
            if (!nbt.contains(NbtKeys.LORE_KEY.str())) return super.useAfterWriting(stack, world, user, hand)
            val bolaId = Identifier(nbt.getString(NbtKeys.LORE_KEY.str()))
            if (FzzyPort.ENCHANTMENT.get(bolaId) == null) return super.useAfterWriting(stack, world, user, hand)
            val bola = bolaId.toString()
            val type = AugmentHelper.getAugmentType(bola)
            if (nbt.contains(NbtKeys.LORE_TYPE.str())){
                if (nbt.getString(NbtKeys.LORE_TYPE.str()) != type.str()){
                    nbt.putString(NbtKeys.LORE_TYPE.str(),type.str())
                }
            } else {
                nbt.putString(NbtKeys.LORE_TYPE.str(),type.str())
            }

        }
        if (world.isClient){
            world.playSound(user,user.blockPos,SoundEvents.ITEM_BOOK_PAGE_TURN,SoundCategory.NEUTRAL,0.7f,1.0f)
            MinecraftClient.getInstance().setScreen(KnowledgeBookScreen(stack))
            return TypedActionResult.success(stack)
        }
        return super.useAfterWriting(stack, world, user, hand)
    }

    override fun getRandomBookAugment(list: List<String>, user: PlayerEntity, hand: Hand): String {
        val stack = if (hand == Hand.MAIN_HAND) user.offHandStack else user.mainHandStack
        if (stack.isIn(RegisterTag.ALL_FURY_SCEPTERS_TAG)) {
            for (i in 1..3) {
                val aug = getUniqueTale(user) { super.getRandomBookAugment(list, user, hand) }
                val type = AugmentHelper.getAugmentType(aug)
                if (type == SpellType.FURY) {
                    return aug
                }
            }
        } else if (stack.isIn(RegisterTag.ALL_WIT_SCEPTERS_TAG)) {
            for (i in 1..3) {
                val aug = getUniqueTale(user) { super.getRandomBookAugment(list, user, hand) }
                val type = AugmentHelper.getAugmentType(aug)
                if (type == SpellType.WIT) {
                    return aug
                }
            }
        } else if (stack.isIn(RegisterTag.ALL_GRACE_SCEPTERS_TAG)) {
            for (i in 1..3) {
                val aug = getUniqueTale(user) { super.getRandomBookAugment(list, user, hand) }
                val type = AugmentHelper.getAugmentType(aug)
                if (type == SpellType.GRACE) {
                    return aug
                }
            }
        }
        return getUniqueTale(user) { super.getRandomBookAugment(list, user, hand) }
    }

    private fun getUniqueTale(player: PlayerEntity, supplierOfTales: Supplier<String>): String{
        var tale = supplierOfTales.get()
        while (hasReadLast(player,tale)){
            tale = supplierOfTales.get()
        }
        return tale
    }

    override fun unlock(world: World, blockPos: BlockPos, stack: ItemStack?) {
        stack?.orCreateNbt?.putBoolean("unlocked",true)
    }

    override fun consumeItem(): Boolean {
        return false
    }
}
