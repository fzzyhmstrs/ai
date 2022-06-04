package me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

abstract class PlaceItemAugment(tier: Int, maxLvl: Int,item: Item, vararg slot: EquipmentSlot): ScepterAugment(tier,maxLvl, EnchantmentTarget.WEAPON, *slot), BuilderAugment {
    private val _item = item

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(4.5)

    override fun applyTasks(world: World, user: LivingEntity, hand: Hand, level: Int, effects: AugmentEffect): Boolean {
        val bl = RaycasterUtil.raycastBlock(effects.range(level),entity = user) != null
        if (bl){
            effects.accept(user,AugmentConsumer.Type.BENEFICIAL)
        }
        return bl
    }

    override fun clientTask(world: World, user: LivingEntity, hand: Hand, level: Int) {
        if (user !is PlayerEntity) return
        val hit = MinecraftClient.getInstance().crosshairTarget ?: return
        if (hit.type != HitResult.Type.BLOCK) return
        when (val testItem = itemToPlace(world,user).item) {
            is BlockItem -> {
                testItem.place(ItemPlacementContext(user, hand, ItemStack(testItem),hit as BlockHitResult))
                ClientPlayNetworking.send(PLACE_ITEM_PACKET,placeItemPacket(ItemStack(testItem),hand,hit))
            }
            is BucketItem -> {
                testItem.placeFluid(user,world,(hit as BlockHitResult).blockPos,hit)
                ClientPlayNetworking.send(PLACE_ITEM_PACKET,placeItemPacket(ItemStack(testItem),hand,hit))
            }
            else -> {
                return
            }
        }
    }

    private fun placeItemPacket(itemStack: ItemStack, hand: Hand, hit: BlockHitResult): PacketByteBuf{
        val buf = PacketByteBufs.create()
        buf.writeItemStack(itemStack)
        buf.writeEnumConstant(hand)
        buf.writeBlockHitResult(hit)
        return buf
    }

    open fun itemToPlace(world: World, user: LivingEntity): ItemStack {
        return ItemStack(_item)
    }

    companion object{

        private val PLACE_ITEM_PACKET = Identifier(AI.MOD_ID,"place_item_packet")

        fun registerServer(){
            ServerPlayNetworking.registerGlobalReceiver(PLACE_ITEM_PACKET)
            { _: MinecraftServer,
              serverPlayerEntity: ServerPlayerEntity,
              _: ServerPlayNetworkHandler,
              packetByteBuf: PacketByteBuf,
              _: PacketSender ->
                val stack = packetByteBuf.readItemStack()
                val hand = packetByteBuf.readEnumConstant(Hand::class.java)
                val hit = packetByteBuf.readBlockHitResult()
                placeItem(serverPlayerEntity.world,serverPlayerEntity,stack, hand, hit)
            }
        }

        private fun placeItem(world: World, user: PlayerEntity, itemStack: ItemStack, hand: Hand, hit: HitResult){
            when (val testItem = itemStack.item) {
                is BlockItem -> {
                    testItem.place(ItemPlacementContext(user, hand, ItemStack(testItem),hit as BlockHitResult))
                }
                is BucketItem -> {
                    testItem.placeFluid(user,world,(hit as BlockHitResult).blockPos,hit)
                }
                else -> {
                    return
                }
            }
        }
    }

}