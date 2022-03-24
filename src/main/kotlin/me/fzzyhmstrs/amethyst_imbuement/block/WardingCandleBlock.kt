package me.fzzyhmstrs.amethyst_imbuement.block

import com.google.common.collect.ImmutableList
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.CandleBlock
import net.minecraft.state.StateManager
import net.minecraft.util.Util
import net.minecraft.util.math.Vec3d
import java.util.function.ToIntFunction

@Suppress("PrivatePropertyName")
class WardingCandleBlock(settings: Settings): CandleBlock(settings) {
    private val CANDLES_TO_PARTICLE_OFFSETS: Int2ObjectMap<List<Vec3d>> = Util.make {
        val int2ObjectMap = Int2ObjectOpenHashMap<List<Vec3d>>()
        int2ObjectMap.defaultReturnValue()
        int2ObjectMap[1] = ImmutableList.of(Vec3d(0.5, 0.5, 0.5))
        int2ObjectMap[2] =
            ImmutableList.of(Vec3d(0.375, 0.44, 0.5), Vec3d(0.625, 0.5, 0.44))
        int2ObjectMap[3] = ImmutableList.of(
            Vec3d(0.5, 0.313, 0.625),
            Vec3d(0.375, 0.44, 0.5),
            Vec3d(0.56, 0.5, 0.44)
        )
        int2ObjectMap[4] = ImmutableList.of(
            Vec3d(0.44, 0.313, 0.56),
            Vec3d(0.625, 0.44, 0.56),
            Vec3d(0.375, 0.44, 0.375),
            Vec3d(0.56, 0.5, 0.375)
        )
        Int2ObjectMaps.unmodifiable(int2ObjectMap)
    }

    companion object {
        val STATE_TO_LUMINANCE =
            ToIntFunction { state: BlockState ->
                if (state.get(LIT) != false)
                    (4 * state.get(
                        CANDLES
                    )
                            ) - 1 else 0
            }

    }

    override fun isNotLit(state: BlockState): Boolean {
        return state.get(WATERLOGGED) == false && super.isNotLit(state)
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        builder.add(CANDLES, LIT, WATERLOGGED)
    }

    override fun getParticleOffsets(state: BlockState): Iterable<Vec3d> {
        return CANDLES_TO_PARTICLE_OFFSETS[state.get(CANDLES)] as Iterable<Vec3d>
    }

}