package me.fzzyhmstrs.amethyst_imbuement.entity

import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack
import net.minecraft.util.Arm
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class DraconicBoxEntity(entityType: EntityType<DraconicBoxEntity>, world: World): LivingEntity(entityType,world) {


    init{
        this.isInvulnerable  = true
    }

    override fun getArmorItems(): MutableIterable<ItemStack> {
        return DefaultedList.ofSize(4, ItemStack.EMPTY)
    }

    override fun equipStack(slot: EquipmentSlot, stack: ItemStack) {
        return
    }

    override fun getEquippedStack(slot: EquipmentSlot?): ItemStack {
        return ItemStack.EMPTY
    }

    override fun getMainArm(): Arm {
        return Arm.RIGHT
    }

    override fun tick() {
        if (!this.hasStatusEffect(StatusEffects.GLOWING)) {
            println(this.world.isClient)
            this.addStatusEffect(StatusEffectInstance(StatusEffects.GLOWING, 220))
        }
        this.setNoGravity(true)
        noClip = true
        super.tick()
        noClip = false
        if (this.age >= 300) this.discard()
    }

    override fun shouldRenderName(): Boolean {
        return false
    }



    override fun canTakeDamage(): Boolean {
        return false
    }

    override fun collides(): Boolean {
        return false
    }

    override fun isPushable(): Boolean {
        return false
    }

    override fun collidesWith(other: Entity?): Boolean {
        return false
    }

    override fun collidesWithStateAtPos(pos: BlockPos?, state: BlockState?): Boolean {
        return false
    }

    companion object {
        fun createMobAttributes(): DefaultAttributeContainer.Builder {
            return createLivingAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 1.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED,0.0)
        }
    }

    override fun checkBlockCollision() {
        return
    }

    override fun isInsideWall(): Boolean {
        return false
    }


}