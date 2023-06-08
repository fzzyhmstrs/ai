package me.fzzyhmstrs.amethyst_imbuement.entity.living

import me.fzzyhmstrs.amethyst_imbuement.AI
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleDefaultedRegistry
import net.minecraft.util.Identifier

class HamsterVariant(val texture: Identifier) {

    companion object{
        val HAMSTERS: SimpleDefaultedRegistry<HamsterVariant> = FabricRegistryBuilder.createDefaulted(RegistryKey.ofRegistry<HamsterVariant>(Identifier(AI.MOD_ID,"hamster_variant")),
            Identifier(AI.MOD_ID,"dwarf_hamster")).buildAndRegister()
        val TRACKED_HAMSTER = TrackedDataHandler.of(HAMSTERS)

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

        ////////////////////////////////

        private fun register(name: String, texture: String): HamsterVariant{
            return Registry.register(HAMSTERS, Identifier(AI.MOD_ID,name), HamsterVariant(Identifier(AI.MOD_ID,texture)))
        }
    }

}