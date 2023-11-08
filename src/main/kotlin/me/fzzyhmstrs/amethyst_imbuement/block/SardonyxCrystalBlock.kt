package me.fzzyhmstrs.amethyst_imbuement.block

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.DustParticleEffect
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.BlockView
import net.minecraft.world.World

class SardonyxCrystalBlock(settings:Settings):Block(settings) {

    companion object{
        private val SMALL_DUST = DustParticleEffect(DustParticleEffect.RED,0.8f)
        private val MESSAGES = Array<Text>(5) { i -> AcText.translatable("block.amethyst_imbuement.sardonyx_crystal.message$i").formatted(Formatting.RED)}
        private val searchArray = intArrayOf(0,1,-1,2,-2,3,-3)
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        if (player.isCreative) return super.onBreak(world, pos, state, player)
        val placePos = findSpawnPos(world,pos,6,3, tries = 100)
        if (placePos == BlockPos.ORIGIN) {
            val explosionPos = pos.toCenterPos()
            world.createExplosion(null,explosionPos.x,explosionPos.y,explosionPos.z,15f,World.ExplosionSourceType.TNT)
        } else {

            val sge = RegisterEntity.SARDONYX_ELEMENTAL.create(world)
            if (sge == null){
                val explosionPos = pos.toCenterPos()
                world.createExplosion(null,explosionPos.x,explosionPos.y,explosionPos.z,15f,World.ExplosionSourceType.TNT)
                return
            }
            sge.refreshPositionAndAngles(placePos,0f,0f)
            world.spawnEntity(sge)
        }
        super.onBreak(world, pos, state, player)
    }

    private fun findSpawnPos(world: World, startPos: BlockPos, radius: Int, heightNeeded: Int, blockNeeded: Block = Blocks.AIR, tries: Int = 8): BlockPos{
        for (i in 1..tries){
            val xPos = startPos.x + world.random.nextBetween(-radius,radius)
            val yPos = startPos.up().y
            val zPos = startPos.z + world.random.nextBetween(-radius,radius)
            for (j in searchArray){
                val testPos = BlockPos(xPos,yPos + j,zPos)
                if (world.getBlockState(testPos).isOf(blockNeeded)){
                    if (heightNeeded > 1){
                        var found2 = true
                        for (k in 1 until heightNeeded){
                            if (!world.getBlockState(testPos.up(k)).isOf(blockNeeded)){
                                found2 = false
                                break
                            }
                        }
                        if (!found2) continue
                    }

                }
                return testPos
            }
        }
        return BlockPos.ORIGIN
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

    @Deprecated("Deprecated in Java", ReplaceWith("false"))
    override fun canPathfindThrough(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        type: NavigationType?
    ): Boolean {
        return false
    }
}
