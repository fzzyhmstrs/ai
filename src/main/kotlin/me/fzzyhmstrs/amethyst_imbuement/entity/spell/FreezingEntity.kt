package me.fzzyhmstrs.amethyst_imbuement.entity.spell

import me.fzzyhmstrs.amethyst_core.entity_util.MissileEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.CustomDamageSources
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellDamageSource
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.block.Blocks
import net.minecraft.block.FrostedIceBlock
import net.minecraft.block.ShapeContext
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.world.RaycastContext
import net.minecraft.world.World

class FreezingEntity(entityType: EntityType<FreezingEntity>, world: World): MissileEntity(entityType,world) {

    constructor(world: World,owner: LivingEntity,_level: Int) : this(RegisterEntity.FREEZING_ENTITY,world){
        this.level = _level
        this.owner = owner
        this.setPosition(
            owner.x,
            owner.eyeY - (user.height * 0.25),
            owner.z
        )
        this.setRotation(owner.yaw, owner.pitch)
    }

    private var level: Int = 1
    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(3.0F).withRange(4.0).withDuration(180)
    private var augment: ScepterAugment = RegisterEnchantment.FREEZING
    
    fun setAugment(aug: ScepterAugment){
        this.augment = aug
    }

    override fun passEffects(ae: AugmentEffect, level: Int) {
        super.passEffects(ae, level)
        entityEffects.setRange(ae.range(level))
        entityEffects.setDuration(ae.duration(level))
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        val entity = owner
        if (entity is LivingEntity) {
            val entity2 = entityHitResult.entity
            if (AiConfig.entities.shouldItHitBase(entity, entity2, augment)) {
                val bl = if (!entity2.isFireImmune) {
                    if (entity2 is LivingEntity) entity2.frozenTicks = entityEffects.duration(0)
                    entity2.damage(SpellDamageSource(CustomDamageSources.freeze(world,this, entity),augment), entityEffects.damage(0))
                } else {
                    if (entity2 is LivingEntity) entity2.frozenTicks = (entityEffects.duration() * 1.6).toInt()
                    entity2.damage(SpellDamageSource(CustomDamageSources.freeze(world,this, entity),augment), entityEffects.damage(0) * 1.6F)
                }
                if (bl) {
                    if (entity2 is LivingEntity) entityEffects.accept(entity2, AugmentConsumer.Type.HARMFUL)
                    applyDamageEffects(entity, entity2)
                    entityEffects.accept(entity, AugmentConsumer.Type.BENEFICIAL)
                }
                val entityList =
                    RaycasterUtil.raycastEntityArea(distance = entityEffects.range(0), entityHitResult.entity)
                if (entityList.isNotEmpty()) {
                    for (entity3 in entityList) {
                        if (AiConfig.entities.shouldItHitBase(entity, entity3, augment)) {
                            RegisterEnchantment.FREEZING.entityTask(
                                entity.world,
                                entity3,
                                entity,
                                level.toDouble(),
                                null,
                                entityEffects
                            )
                        }
                    }
                }
            }

        }
        discard()
    }

    override fun fluidHandling(): RaycastContext.FluidHandling {
        return RaycastContext.FluidHandling.WATER
    }

    override fun onMissileBlockHit(blockHitResult: BlockHitResult) {
        super.onMissileBlockHit(blockHitResult)
        val owner = this.owner as? LivingEntity ?: return
        freezeWater(world, blockHitResult.blockPos.offset(Direction.UP))
    }

    private fun freezeWater(world: World, blockPos: BlockPos) {
        val blockState = Blocks.FROSTED_ICE.defaultState
        val i = 3
        val mutable = BlockPos.Mutable()
        for (blockPos2 in BlockPos.iterate(blockPos.add(-i, -1, -i), blockPos.add(i, -1, i))) {
            if (!blockPos2.isWithinDistance(blockPos,i.toDouble())) continue
            mutable[blockPos2.x, blockPos2.y + 1] = blockPos2.z
            val blockState2 = world.getBlockState(mutable)
            if (!blockState2.isAir
                || world.getBlockState(blockPos2) != FrostedIceBlock.getMeltedState()
                || !blockState.canPlaceAt(world, blockPos2)
                || !world.canPlace(blockState, blockPos2, ShapeContext.absent())) continue
            world.setBlockState(blockPos2, blockState)
            world.scheduleBlockTick(blockPos2, Blocks.FROSTED_ICE, MathHelper.nextInt(world.random, 60, 120))
        }
    }

    override fun getParticleType(): ParticleEffect? {
        return ParticleTypes.SNOWFLAKE
    }

    override fun playHitSound(world: World, pos: BlockPos) {
        world.playSound(null,pos, SoundEvents.ENTITY_PLAYER_HURT_FREEZE, SoundCategory.PLAYERS,0.4f,1.0f)
    }

}
