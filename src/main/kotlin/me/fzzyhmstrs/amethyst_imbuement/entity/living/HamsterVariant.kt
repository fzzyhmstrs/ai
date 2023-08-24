package me.fzzyhmstrs.amethyst_imbuement.entity.hamster

import me.fzzyhmstrs.amethyst_imbuement.AI
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleDefaultedRegistry
import net.minecraft.util.Identifier
import net.minecraft.util.math.random.Random

class HamsterVariant(val texture: Identifier, private val randomlySelectable: Boolean = true) {

    companion object{
        val HAMSTERS: SimpleDefaultedRegistry<HamsterVariant> = FabricRegistryBuilder.createDefaulted(RegistryKey.ofRegistry<HamsterVariant>(AI.identity("hamster_variant")),
            AI.identity("dwarf_hamster")).buildAndRegister()
        val TRACKED_HAMSTER = TrackedDataHandler.of(HAMSTERS)

        private val SELECTABLE_HAMSTERS: MutableList<HamsterVariant> = mutableListOf()

        fun randomVariant(random: Random): HamsterVariant?{
            return if (SELECTABLE_HAMSTERS.isEmpty()) null else SELECTABLE_HAMSTERS[random.nextInt(SELECTABLE_HAMSTERS.size)]
        }

        fun registerAll(){
            TrackedDataHandlerRegistry.register(TRACKED_HAMSTER)
        }

        ////////////////////////////////

        val BLACK_BEAR = register("black_bear_hamster","textures/entity/hamster/black_bear.png")
        val CALICO = register("calico_hamster","textures/entity/hamster/calico.png")
        val DWARF = register("dwarf_hamster","textures/entity/hamster/dwarf.png")
        val JAMSTER = register("jeans_hamster","textures/entity/hamster/jeans.png")
        val MINK = register("mink_hamster","textures/entity/hamster/mink.png")
        val SIBERIAN = register("siberian_hamster","textures/entity/hamster/siberian.png")
        val SYRIAN = register("syrian_hamster","textures/entity/hamster/syrian.png")

        val MAGMA = register("magma_hamster","textures/entity/hamster/magma.png", false)
        val FROST = register("frost_hamster","textures/entity/hamster/frost.png", false)
        val CRYSTAL = register("crystal_hamster","textures/entity/hamster/crystal.png", false)
        val ZOMBIE = register("zombie_hamster","textures/entity/hamster/zombie.png", false)

        ////////////////////////////////

        private fun register(name: String, texture: String, randomlySelectable: Boolean = true): HamsterVariant {
            return Registry.register(HAMSTERS, AI.identity(name), HamsterVariant(AI.identity(texture),randomlySelectable)).also { if(randomlySelectable) SELECTABLE_HAMSTERS.add(it) }
        }
    }

}
