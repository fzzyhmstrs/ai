package me.fzzyhmstrs.amethyst_imbuement.entity.spell

import eu.pb4.common.protection.api.CommonProtection
import me.fzzyhmstrs.amethyst_core.entity_util.MissileEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellDamageSource
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.fzzy_core.coding_util.compat.FzzyDamage
import net.minecraft.block.AbstractFireBlock
import net.minecraft.enchantment.ProtectionEnchantment
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.world.GameRules
import net.minecraft.world.World

class FlameboltEntity(entityType: EntityType<FlameboltEntity>, world: World): MissileEntity(entityType, world) {

    constructor(world: World,owner: LivingEntity, speed: Float, divergence: Float, x: Double, y: Double, z: Double) : this(RegisterEntity.FLAMEBOLT_ENTITY,world){
        this.owner = owner
        this.setVelocity(owner,
            owner.pitch,
            owner.yaw,
            0.0f,
            speed,
            divergence)
        this.setPosition(x,y,z)
        this.setRotation(owner.yaw, owner.pitch)
    }

    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(5.8F,0.2F,0.0F).withDuration(80)
    private var augment: ScepterAugment = RegisterEnchantment.FLAMEBOLT
    
    fun setAugment(aug: ScepterAugment){
        this.augment = aug
    }

    override fun passEffects(ae: AugmentEffect, level: Int) {
        super.passEffects(ae, level)
        ae.addDuration(ae.amplifier(level))
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        if (world.isClient) {
            return
        }
        val entity = owner
        if (entity is LivingEntity) {
            val entity2 = entityHitResult.entity
            if (!entity2.isFireImmune && AiConfig.entities.shouldItHitBase(entity, entity2, augment)) {
                val i = entity2.fireTicks
                val j = if (entity2 is LivingEntity) ProtectionEnchantment.transformFireDuration(entity2, entityEffects.duration(0)) else entityEffects.duration(0)
                if (i < j)
                    entity2.fireTicks = j
                val bl = entity2.damage(
                    SpellDamageSource(FzzyDamage.fireball(this,null,owner), augment),
                    entityEffects.damage(0)
                )
                if (!bl) {
                    entity2.fireTicks = i
                } else {
                    entityEffects.accept(entity, AugmentConsumer.Type.BENEFICIAL)
                    applyDamageEffects(entity as LivingEntity?, entity2)
                    if (entity2 is LivingEntity) {
                        entityEffects.accept(entity2, AugmentConsumer.Type.HARMFUL)
                    }
                }
            }
        }
        discard()
    }

    override fun onMissileBlockHit(blockHitResult: BlockHitResult) {
        super.onMissileBlockHit(blockHitResult)
        val owner = this.owner
        if (owner !is PlayerEntity) return
        val blockPos1 = blockHitResult.blockPos.offset(blockHitResult.side)
        if ((world.gameRules.getBoolean(GameRules.DO_MOB_GRIEFING))
            && world.isAir(blockPos1)
            && CommonProtection.canPlaceBlock(world,blockPos1,owner.gameProfile,owner)) {
            world.setBlockState(blockPos1, AbstractFireBlock.getState(world, blockPos1))
        }
    }

    override fun isBurning(): Boolean {
        return this.age > 1
    }

    override fun getParticleType(): ParticleEffect {
        return ParticleTypes.FLAME
    }

    companion object{
        fun createFlamebolt(world: World, user: LivingEntity, speed: Float, div: Float, effects: AugmentEffect, level: Int, augment:ScepterAugment): FlameboltEntity {
            val fbe = FlameboltEntity(
                world, user, speed, div,
                user.x - (user.width + 0.5f) * 0.5 * MathHelper.sin(user.bodyYaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(
                    user.pitch * (Math.PI.toFloat() / 180)
                ),
                user.eyeY - (user.height * 0.3333333) - 0.8 * MathHelper.sin(user.pitch * (Math.PI.toFloat() / 180)),
                user.z + (user.width + 0.5f) * 0.5 * MathHelper.cos(user.bodyYaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(
                    user.pitch * (Math.PI.toFloat() / 180)
                ),
            )
            fbe.passEffects(effects, level)
            fbe.setAugment(augment)
            return fbe
        }
    }

}
