package me.fzzyhmstrs.amethyst_imbuement.item.scepter

import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterToolMaterial
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.world.World

open class CustomSpellbladeItem(material: ScepterToolMaterial, damage: Int, attackSpeed: Float, settings: Settings) :
    SpellbladeItem(material, damage, attackSpeed, settings) {

    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, world, entity, slot, selected)
        if (entity !is PlayerEntity) return
        if (world.isClient && selected){
            val rnd = world.random.nextInt(particleChance())
            if (rnd < 1){
                emitParticles(world, MinecraftClient.getInstance(), entity)
            }
        }
    }

}