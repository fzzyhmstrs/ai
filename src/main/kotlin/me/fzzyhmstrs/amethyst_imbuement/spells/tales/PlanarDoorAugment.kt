package me.fzzyhmstrs.amethyst_imbuement.spells.tales

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.PlaceItemAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.spell.PlanarDoorEntity
import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfTalesItem
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.nbt_util.Nbt
import net.minecraft.item.CompassItem
import net.minecraft.item.Items
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtOps
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.DyeColor
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.world.World
import java.util.*

class PlanarDoorAugment: PlaceItemAugment(ScepterTier.THREE, 10, Items.ENDER_EYE){

    companion object{
        private val colorMap: MutableMap<UUID,Int> = mutableMapOf()
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT, PerLvlI(4800,-120),250,
            24,imbueLevel,30, BookOfTalesItem.TALES_TIER, Items.ENDER_EYE)
    }

    override fun blockPlacing(hit: BlockHitResult, world: World, user: ServerPlayerEntity, hand: Hand, level: Int, effects: AugmentEffect): Boolean{
        val placePos = if (world.getBlockState(hit.blockPos).isReplaceable) hit.blockPos else hit.blockPos.offset(hit.side)
        val stack = user.getStackInHand(hand)
        val nbt = stack.orCreateNbt
        val colorInt = if (!nbt.contains("planar_door_color")){
            val oldCi = colorMap.computeIfAbsent(user.uuid) {0}
            var ci = DyeColor.values().random().signColor
            while (oldCi == ci){
                ci = DyeColor.values().random().signColor
            }
            nbt.putInt("planar_door_color",ci)
            colorMap[user.uuid] = ci
            ci
        } else {
            nbt.getInt("planar_door_color")
        }
        if (nbt.containsUuid("planar_partner")){
            val partnerUUID = nbt.getUuid("planar_partner")
            println(partnerUUID)
            val worldOptional = World.CODEC.parse(NbtOps.INSTANCE, nbt[CompassItem.LODESTONE_DIMENSION_KEY]).result()
            if (world is ServerWorld && worldOptional.isPresent) {
                val newWorld = world.server.getWorld(worldOptional.get()) ?: return false
                val pde = PlanarDoorEntity(user,world,colorInt)
                pde.updatePositionAndAngles(placePos.x + 0.5,placePos.y + 0.0,placePos.z + 0.5,0f,0f)
                if (world.spawnEntity(pde)) {
                    val pos = Nbt.readBlockPos("partner_pos",nbt)
                    newWorld.getBlockState(pos)
                    val partner = newWorld.getEntity(partnerUUID)
                    println(newWorld)
                    println(partner)
                    if (partner is PlanarDoorEntity) {
                        nbt.putUuid("planar_partner_previous",partner.uuid)
                        World.CODEC.encodeStart(NbtOps.INSTANCE, world.registryKey).resultOrPartial { s: String? ->
                            println(s)
                        }.ifPresent { nbtElement: NbtElement? ->
                            nbt.put(CompassItem.LODESTONE_DIMENSION_KEY + "Previous", nbtElement)
                        }
                        Nbt.writeBlockPos("partner_pos_previous",partner.blockPos,nbt)
                        partner.getPartner()?.discard()
                        partner.setPartner(pde)
                        pde.setPartner(partner)
                    } else if (nbt.containsUuid("planar_partner_previous")) {
                        val previousPartnerUUID = nbt.getUuid("planar_partner_previous")
                        println(previousPartnerUUID)
                        val worldOptional2 =
                            World.CODEC.parse(NbtOps.INSTANCE, nbt[CompassItem.LODESTONE_DIMENSION_KEY + "Previous"])
                                .result()
                        if (worldOptional2.isPresent) {
                            val newWorld2 = world.server.getWorld(worldOptional2.get())
                            val pos2 = Nbt.readBlockPos("partner_pos_previous",nbt)
                            newWorld2?.getBlockState(pos)
                            val partner2 = newWorld2?.getEntity(previousPartnerUUID)
                            println(partner2)
                            if (partner2 is PlanarDoorEntity) {
                                partner2.getPartner()?.discard()
                                partner2.setPartner(pde)
                                pde.setPartner(partner2)
                            }
                        }
                    }
                    nbt.putUuid("planar_partner",pde.uuid)
                    World.CODEC.encodeStart(NbtOps.INSTANCE, world.registryKey).resultOrPartial { s: String? ->
                        println(s)
                    }.ifPresent { nbtElement: NbtElement? ->
                        nbt.put(CompassItem.LODESTONE_DIMENSION_KEY, nbtElement)
                    }
                    Nbt.writeBlockPos("partner_pos",pde.blockPos,nbt)
                    return true
                }
            }
        } /*else if (nbt.containsUuid("planar_partner_previous")){
            val previousPartnerUUID = nbt.getUuid("planar_partner_previous")
            val worldOptional = World.CODEC.parse(NbtOps.INSTANCE, nbt[CompassItem.LODESTONE_DIMENSION_KEY + "Previous"]).result()
            if (world is ServerWorld && worldOptional.isPresent) {
                val newWorld = world.server.getWorld(worldOptional.get()) ?: return false

                val pde = PlanarDoorEntity(user,world,colorInt)
                pde.updatePositionAndAngles(placePos.x + 0.5,placePos.y + 0.0,placePos.z + 0.5,0f,0f)
                if (world.spawnEntity(pde)) {
                    val partner = newWorld.getEntity(previousPartnerUUID)
                    if (partner is PlanarDoorEntity) {
                        partner.getPartner()?.discard()
                        partner.setPartner(pde)
                        pde.setPartner(partner)
                    }
                    nbt.putUuid("planar_partner",pde.uuid)
                    World.CODEC.encodeStart(NbtOps.INSTANCE, world.registryKey).resultOrPartial { s: String? ->
                        println(s)
                    }.ifPresent { nbtElement: NbtElement? ->
                        nbt.put(CompassItem.LODESTONE_DIMENSION_KEY, nbtElement)
                    }

                    return true
                }
            }
        }*/ else {
            val pde = PlanarDoorEntity(user,world,colorInt)
            pde.updatePositionAndAngles(placePos.x + 0.5,placePos.y + 0.0,placePos.z + 0.5,0f,0f)
            if (world.spawnEntity(pde)){
                nbt.putUuid("planar_partner",pde.uuid)
                World.CODEC.encodeStart(NbtOps.INSTANCE, world.registryKey).resultOrPartial { s: String? ->
                    println(s)
                }.ifPresent { nbtElement: NbtElement? ->
                    nbt.put(CompassItem.LODESTONE_DIMENSION_KEY, nbtElement)
                }
                Nbt.writeBlockPos("partner_pos",pde.blockPos,nbt)
                return true
            }
        }
        //TntBlock.primeTnt(world,placePos)
        return false
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_END_PORTAL_FRAME_FILL
    }

}
