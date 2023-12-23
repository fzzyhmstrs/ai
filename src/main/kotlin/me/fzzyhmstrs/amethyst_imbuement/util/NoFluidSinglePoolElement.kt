package me.fzzyhmstrs.amethyst_imbuement.util

import com.mojang.datafixers.kinds.Applicative
import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterWorldgen
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.structure.StructurePlacementData
import net.minecraft.structure.StructureTemplate
import net.minecraft.structure.pool.SinglePoolElement
import net.minecraft.structure.pool.StructurePool
import net.minecraft.structure.pool.StructurePoolElementType
import net.minecraft.structure.processor.StructureProcessorList
import net.minecraft.util.BlockRotation
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockBox

class NoFluidSinglePoolElement(
    location: Either<Identifier, StructureTemplate>?,
    processors: RegistryEntry<StructureProcessorList>?,
    projection: StructurePool.Projection): SinglePoolElement(location, processors, projection) {

        companion object{
            val CODEC: Codec<NoFluidSinglePoolElement> =
                RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<NoFluidSinglePoolElement> ->
                    instance.group(
                        locationGetter(),
                        processorsGetter(),
                        projectionGetter()
                    ).apply(instance as Applicative<RecordCodecBuilder.Mu<NoFluidSinglePoolElement>, *>) { a, b, c ->
                        NoFluidSinglePoolElement(a, b, c)
                    }
                }

        }

    override fun getType(): StructurePoolElementType<*> {
        return RegisterWorldgen.NO_FLUID_SINGLE_POOL_ELEMENT
    }

    override fun createPlacementData(
        rotation: BlockRotation?,
        box: BlockBox?,
        keepJigsaws: Boolean
    ): StructurePlacementData {
        return super.createPlacementData(rotation, box, keepJigsaws).setPlaceFluids(false)
    }

    override fun toString(): String {
        return "NoFluidSingle[" + this.location + "]"
    }

}
