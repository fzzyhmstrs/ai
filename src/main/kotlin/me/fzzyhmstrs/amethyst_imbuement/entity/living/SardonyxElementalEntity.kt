package me.fzzyhmstrs.amethyst_imbuement.entity.living

import net.minecraft.entity.EntityType
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.world.World

class SardonyxElementalEntity(entityType: EntityType<out HostileEntity>?, world: World?) : HostileEntity(entityType, world) {

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