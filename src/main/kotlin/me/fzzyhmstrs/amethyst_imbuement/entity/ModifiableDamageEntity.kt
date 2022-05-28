package me.fzzyhmstrs.amethyst_imbuement.entity

import net.minecraft.entity.LivingEntity
import java.util.function.Consumer

interface ModifiableDamageEntity {

    var damage: Float
    var amplifier: Int
    var range: Double
    var duration: Int
    var consumers: List<Consumer<List<LivingEntity>>>
}