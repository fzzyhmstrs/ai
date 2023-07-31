package me.fzzyhmstrs.amethyst_imbuement.entity.living

import net.minecraft.entity.EntityType
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.world.World

open class AttackTicksGolemEntity(entityType: EntityType<out AttackTicksGolemEntity>, world: World): GolemEntity(entityType,world) {

    protected var attackTicksLeft = 0

    override fun tickMovement() {
        super.tickMovement()
        if (attackTicksLeft > 0) {
            --attackTicksLeft
        }
    }

    override fun handleStatus(status: Byte) {
        if (status.toInt() == 4) {
            attackTicksLeft = 10
        } else {
            super.handleStatus(status)
        }
    }

    fun getAttackTicks(): Int {
        return attackTicksLeft
    }
}
