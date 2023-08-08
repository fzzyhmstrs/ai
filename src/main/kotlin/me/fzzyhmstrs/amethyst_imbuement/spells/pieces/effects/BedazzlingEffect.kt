package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects

import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.util.math.Box

object BedazzlingEffect {

    fun drawAggro(entity: Entity, owner: Entity?, processContext: ProcessContext){
        if (entity.world.time % 22 != 0L) return
        if (entity !is LivingEntity) return
        val box = Box(entity.pos.x + 8.0, entity.pos.y + 8.0, entity.pos.z + 8.0, entity.pos.x - 8.0, entity.pos.y - 8.0, entity.pos.z - 8.0)
        val list = entity.world.getEntitiesByClass(MobEntity::class.java,box) { it is Monster && it.target !is ModifiableEffectEntity }
        list.forEach {
            it.target = entity
        }
    }

}