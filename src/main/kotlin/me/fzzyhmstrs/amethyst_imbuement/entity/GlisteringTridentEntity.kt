package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity.GLISTERING_TRIDENT_ENTITY
import me.fzzyhmstrs.fzzy_core.entity_util.BasicCustomTridentEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class GlisteringTridentEntity : BasicCustomTridentEntity {

    constructor(entityType: EntityType<out GlisteringTridentEntity>, world: World) : super(entityType, world)
    constructor(world: World, owner: LivingEntity, stack: ItemStack) : super(GLISTERING_TRIDENT_ENTITY, world, owner, stack)

}