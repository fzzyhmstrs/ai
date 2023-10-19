package me.fzzyhmstrs.amethyst_imbuement.block

import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.DustParticleEffect
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.World

class SardonyxCrystalBlock(settings:Settings):Block(settings) {

    companion object{
        private val SMALL_DUST = DustParticleEffect(DustParticleEffect.RED,0.8f)
        private val MESSAGES = Array<Text>(5) { i -> AcText.translatable("block.amethyst_imbuement.sardonyx_crystal.message$i").formatted(Formatting.RED)}
    }

    override fun onBreak(world: World?, pos: BlockPos?, state: BlockState?, player: PlayerEntity?) {
        super.onBreak(world, pos, state, player)
    }

    /*@Deprecated("Deprecated in Java")
    override fun onStateReplaced(
        state: BlockState,
        world: World,
        pos: BlockPos?,
        newState: BlockState,
        moved: Boolean
    ) {
        if (state.isOf(newState.block)) {
            return
        }
        //summon SardonyxElemental here
        super.onStateReplaced(state, world, pos, newState, moved)
    }*/

    override fun randomDisplayTick(state: BlockState?, world: World, pos: BlockPos, random: Random) {
        val d = pos.x.toDouble() + world.random.nextDouble()
        val e = pos.y.toDouble() + world.random.nextDouble()
        val f = pos.z.toDouble() + world.random.nextDouble()
        world.addParticle(SMALL_DUST, d, e, f, 0.0, 0.0, 0.0)
        if (world.random.nextFloat() < 0.975f) return
        val player = MinecraftClient.getInstance().player ?: return
        if (pos.getManhattanDistance(player.blockPos) > 12) return
        val message = MESSAGES[world.random.nextInt(5)]
        player.sendMessage(message, true)
        //sned random messages to player
    }
}
