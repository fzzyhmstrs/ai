package me.fzzyhmstrs.amethyst_imbuement.entity

import me.emafire003.dev.coloredglowlib.ColoredGlowLib
import me.fzzyhmstrs.amethyst_imbuement.augment.DraconicVisionAugment
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack
import net.minecraft.util.Arm
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class DraconicBoxEntity(entityType: EntityType<DraconicBoxEntity>, world: World, block: Block, age: Int, bp: BlockPos): LivingEntity(entityType,world) {

    constructor(entityType: EntityType<DraconicBoxEntity>, world: World): this(entityType, world,Blocks.AIR,40, BlockPos(0,0,0))

    private var entityBlock: Block
    private var maxAge: Int
    private var startingBlockPos : BlockPos

    init{
        this.isInvulnerable  = true
        entityBlock = block
        maxAge = age
        startingBlockPos = bp
        if (DraconicVisionAugment.oreIsRainbow(entityBlock)){
            ColoredGlowLib.setRainbowColorToEntity(this,true)
        } else {
            val color = DraconicVisionAugment.oreGlowColor(entityBlock)
            ColoredGlowLib.setColorToEntity(this, color)
        }
    }

    fun extendBoxLife(time: Int) {
        this.age = 0
        if (time != maxAge) {
            maxAge = time
        }
        this.addStatusEffect(StatusEffectInstance(StatusEffects.GLOWING, maxAge))
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
            this.addStatusEffect(StatusEffectInstance(StatusEffects.GLOWING, maxAge))
        }
        this.setNoGravity(true)
        noClip = true
        super.tick()
        noClip = false

        if (world.isClient) return
        if (yaw != 0.0F){
            yaw = 0.0F
        }
        if (this.age >= maxAge || world.getBlockState(blockPos).isOf(Blocks.AIR)) {
            ColoredGlowLib.removeColor(this)
            DraconicVisionAugment.removeBoxFromMap(startingBlockPos)
            //world.playSound(null,blockPos,SoundEvents.BLOCK_BUBBLE_COLUMN_BUBBLE_POP,SoundCategory.NEUTRAL,1.0F,1.0F)
            this.discard()
        }
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