package me.fzzyhmstrs.amethyst_imbuement.item.scepter

import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterToolMaterial
import net.minecraft.block.Block
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.registry.tag.TagKey
import net.minecraft.world.World

open class CustomSpellToolItem(
    material: ScepterToolMaterial,
    damage: Float,
    attackSpeed: Float,
    effectiveBlocks: TagKey<Block>,
    settings: Settings
) :
    SpellToolItem(material, damage, attackSpeed, effectiveBlocks, settings)
{

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