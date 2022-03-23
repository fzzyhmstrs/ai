package me.fzzyhmstrs.amethyst_imbuement.entity

import me.emafire003.dev.coloredglowlib.ColoredGlowLib
import me.emafire003.dev.coloredglowlib.util.Color
import me.fzzyhmstrs.amethyst_imbuement.augment.DraconicVisionAugment
import me.fzzyhmstrs.amethyst_imbuement.util.GlowColorUtil
import me.fzzyhmstrs.amethyst_imbuement.util.NbtKeys
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
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Arm
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class DraconicBoxEntity(entityType: EntityType<DraconicBoxEntity>, world: World): LivingEntity(entityType,world) {

    constructor(entityType: EntityType<DraconicBoxEntity>, world: World, block: Block, age: Int, bp: BlockPos): this(entityType, world) {
        entityBlock = block
        maxAge = age
        startingBlockPos = bp
    }

    private var entityBlock = Blocks.AIR
    private var maxAge = 9999
    private var startingBlockPos: BlockPos = BlockPos.ORIGIN

    init{
        this.isInvulnerable  = true
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
            if (GlowColorUtil.oreIsRainbow(entityBlock)){
                ColoredGlowLib.setRainbowColorToEntity(this,true)
            } else {
                val color = GlowColorUtil.oreGlowColor(entityBlock)
                ColoredGlowLib.setColorToEntity(this, color)
            }
            this.addStatusEffect(StatusEffectInstance(StatusEffects.GLOWING, maxAge))

        }
        this.setNoGravity(true)
        noClip = true
        super.tick()
        noClip = false
        if (this.age >= maxAge) {
            ColoredGlowLib.setRainbowColorToEntity(this,false)
            DraconicVisionAugment.removeBoxFromMap(startingBlockPos)
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

    /*override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        if (ColoredGlowLib.getEntityRainbowColor(this)){
            nbt.putInt(NbtKeys.GLOW_COLOR.str(),-1)
        } else if (ColoredGlowLib.per_entity_color_map.containsKey(this.uuid)){
            nbt.putInt(NbtKeys.GLOW_COLOR.str(), Color.translateFromHEX(ColoredGlowLib.per_entity_color_map[uuid]).colorValue)
        } else {
            nbt.putInt(NbtKeys.GLOW_COLOR.str(),Color.getWhiteColor().colorValue)
        }
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound?) {
        super.readCustomDataFromNbt(nbt)
        val color = nbt?.getInt(NbtKeys.GLOW_COLOR.str())
        if (color != null){
            if (color < 0){
                ColoredGlowLib.setRainbowColorToEntity(this,true)
            } else {
                ColoredGlowLib.setColorToEntity(this, Color(color))
            }
        }
    }*/

}