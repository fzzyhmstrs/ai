package me.fzzyhmstrs.amethyst_imbuement.block


import net.minecraft.block.BlockState
import net.minecraft.block.SlabBlock
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.world.World


@Suppress("PrivatePropertyName")
class PolishedAmethystSlabBlock(settings: Settings): SlabBlock( settings){
    override fun onProjectileHit(world: World, state: BlockState?, hit: BlockHitResult, projectile: ProjectileEntity?) {
        if (!world.isClient) {
            val blockPos = hit.blockPos
            world.playSound(
                null,
                blockPos,
                SoundEvents.BLOCK_AMETHYST_BLOCK_HIT,
                SoundCategory.BLOCKS,
                1.0f,
                0.5f + world.random.nextFloat() * 1.2f
            )
            world.playSound(
                null,
                blockPos,
                SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
                SoundCategory.BLOCKS,
                1.0f,
                0.5f + world.random.nextFloat() * 1.2f
            )
        }
    }

}