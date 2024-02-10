package me.fzzyhmstrs.amethyst_imbuement.entity.variant

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.variant.Variants.Type.CHOLEM
import me.fzzyhmstrs.amethyst_imbuement.entity.variant.Variants.Type.CHORSE
import me.fzzyhmstrs.amethyst_imbuement.entity.variant.Variants.Type.HAMSTER
import me.fzzyhmstrs.fzzy_core.registry.variant.Variant
import me.fzzyhmstrs.fzzy_core.registry.variant.VariantRegistryBuilder

object Variants{

    fun registerAll(){
        Cholem.init()
        Chorse.init()
        Hamster.init()
    }

    object Type{

        val CHOLEM = VariantRegistryBuilder<CholemNamedVariant>(AI.identity("cholem_variants"), AI.identity("white_cholem")).buildSimpleTracked{ id -> CholemNamedVariant(id) }

        val CHORSE = VariantRegistryBuilder<Variant>(AI.identity("chorse_variants"), AI.identity("minecraft_chicken")).buildSimpleTracked{ id -> Variant(id) }

        val HAMSTER = VariantRegistryBuilder<NamedVariant>(AI.identity("hamster_variants"), AI.identity("dwarf_hamster")).buildSimpleTracked { id -> NamedVariant(id) }

    }

    object Cholem {
        val WHITE = CHOLEM.registerVariant(AI.identity("white_cholem"),AI.identity("textures/entity/cholem/cholem.png"))
        val JOLEM = CHOLEM.registerVariant(AI.identity("jeans_cholem"),AI.identity("textures/entity/cholem/jeans_cholem.png"))
        val CHUNK = CHOLEM.registerVariant(AI.identity("punk_cholem"),AI.identity("textures/entity/cholem/punk_cholem.png"))
        
        fun init(){}
    }

    object Chorse {
        val MINECRAFT_CHICKEN = CHORSE.registerVariant(AI.identity("minecraft_chicken"),AI.identity("textures/entity/chorse/chorse.png"))
        val DAPPER = CHORSE.registerVariant(AI.identity("dapper_chorse"),AI.identity("textures/entity/chorse/dapper_chorse.png"))
        val JORSE = CHORSE.registerVariant(AI.identity("jeans_chorse"),AI.identity("textures/entity/chorse/jeans_chorse.png"))
        val CHERRY = CHORSE.registerVariant(AI.identity("cherry_chorse"),AI.identity("textures/entity/chorse/cherry_chorse.png"))
        val MARAN = CHORSE.registerVariant(AI.identity("maran_chorse"),AI.identity("textures/entity/chorse/maran_chorse.png"))

        fun init(){}
    }

    object Hamster {
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

        fun init(){}
    }
}
