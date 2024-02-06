package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.registry.variant.Variant
import me.fzzyhmstrs.fzzy_core.registry.variant.VariantRegistryBuilder
import net.minecraft.entity.EntityType
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object Variants{

    val CHORSE = VariantRegistryBuilder<Variant>(AI.identity("chorse_variants"), AI.identity("minecraft_chicken")).buildSimpleTracked{ id -> Variant(id) }

    val MINECRAFT_CHICKEN = CHORSE.registerVariant(AI.identity("minecraft_chicken"),AI.identity("textures/entity/chorse/chorse.png"))
    
    val HAMSTER = VariantRegistryBuilder<HamsterVariant>(AI.identity("hamster_variants"), AI.identity("dwarf_hamster")).buildSimpleTracked { id -> HamsterVariant(id) }

    val BLACK_BEAR = HAMSTER.registerVariant(AI.identity("black_bear_hamster"),AI.identity("textures/entity/hamster/black_bear.png"))
    val CALICO = HAMSTER.registerVariant(AI.identity("calico_hamster"),AI.identity("textures/entity/hamster/calico.png"))
    val DWARF = HAMSTER.registerVariant(AI.identity("dwarf_hamster"),AI.identity("textures/entity/hamster/dwarf.png"))
    val JAMSTER = HAMSTER.registerVariant(AI.identity("jeans_hamster"),AI.identity("textures/entity/hamster/jeans.png"))
    val MINK = HAMSTER.registerVariant(AI.identity("mink_hamster"),AI.identity("textures/entity/hamster/mink.png"))
    val SIBERIAN = HAMSTER.registerVariant(AI.identity("siberian_hamster"),AI.identity("textures/entity/hamster/siberian.png"))
    val SYRIAN = HAMSTER.registerVariant(AI.identity("syrian_hamster"),AI.identity("textures/entity/hamster/syrian.png"))

    val MAGMA = HAMSTER.registerVariant(AI.identity("magma_hamster"),AI.identity("textures/entity/hamster/magma.png"), false)
    val FROST = HAMSTER.registerVariant(AI.identity("frost_hamster"),AI.identity("textures/entity/hamster/frost.png"), false)
    val CRYSTAL = HAMSTER.registerVariant(AI.identity("crystal_hamster"),AI.identity("textures/entity/hamster/crystal.png"), false)
    val ZOMBIE = HAMSTER.registerVariant(AI.identity("zombie_hamster"),AI.identity("textures/entity/hamster/zombie.png"), false)

    class HamsterVariant(texture: Identifier) : Variant(texture){
        private val translationAppend = texture.path.substringAfterLast('/',"generic.png").substringBefore('.',"generic")

        fun getName(type: EntityType<*>): Text{
            return AcText.translatable(type.translationKey + "." + translationAppend)
        }

    }

}
