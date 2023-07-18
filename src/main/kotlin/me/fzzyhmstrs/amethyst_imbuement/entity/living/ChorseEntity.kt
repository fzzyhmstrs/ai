package me.fzzyhmstrs.amethyst_imbuement.entity.living

import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.entity.Scalable
import me.fzzyhmstrs.fzzy_core.entity_util.PlayerCreatable
import net.minecraft.entity.EntityType
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.passive.AbstractHorseEntity
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World

class ChorseEntity(entityType: EntityType<out AbstractHorseEntity>?, world: World?) : PlayerCreatedHorseEntity(entityType, world),
    ModifiableEffectEntity, PlayerCreatable, Scalable {
    companion object{
        fun createChorseBaseAttributes(): DefaultAttributeContainer.Builder{
            return createMobAttributes()
                .add(EntityAttributes.HORSE_JUMP_STRENGTH,0.93)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.275)
        }
    }

    var flapProgress = 0f
    var maxWingDeviation = 0f
    var prevMaxWingDeviation = 0f
    var prevFlapProgress = 0f
    private var flapSpeed = 1.0f

    override fun tickMovement() {
        super.tickMovement()
        prevFlapProgress = flapProgress
        prevMaxWingDeviation = maxWingDeviation
        maxWingDeviation += (if (this.isOnGround) -1.0f else 4.0f) * 0.3f
        maxWingDeviation = MathHelper.clamp(maxWingDeviation, 0.0f, 1.0f)
        if (!this.isOnGround && flapSpeed < 1.0f) {
            flapSpeed = 1.0f
        }
        flapSpeed *= 0.9f
        val vec3d = velocity
        if (!this.isOnGround && vec3d.y < 0.0) {
            velocity = if (hasPassengers()) {
                vec3d.multiply(1.0, 0.8, 1.0)
            } else {
                vec3d.multiply(1.0, 0.6, 1.0)
            }
        }
        flapProgress += flapSpeed * 2.0f
    }
}