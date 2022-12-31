package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.coding_util.PlayerParticlesV2.scepterParticlePos
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterToolMaterial
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.tag.BlockTags
import net.minecraft.world.World

class BuilderScepterItem(material: ScepterToolMaterial, settings: Settings): CustomScepterItem(material, settings) {

    override fun getMiningSpeedMultiplier(stack: ItemStack, state: BlockState): Float {
        return if (state.isIn(BlockTags.AXE_MINEABLE) || state.isIn(BlockTags.SHOVEL_MINEABLE)) 6.0f else 1.0f
    }

}