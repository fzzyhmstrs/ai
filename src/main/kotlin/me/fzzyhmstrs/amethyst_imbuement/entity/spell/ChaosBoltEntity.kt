package me.fzzyhmstrs.amethyst_imbuement.entity.spell

import me.fzzyhmstrs.amethyst_core.entity_util.MissileEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.particle.DustParticleEffect
import net.minecraft.particle.ParticleEffect
import net.minecraft.registry.Registries
import net.minecraft.util.DyeColor
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class ChaosBoltEntity(entityType: EntityType<ChaosBoltEntity>, world: World): MissileEntity(entityType, world) {

    constructor(world: World,owner: LivingEntity, speed: Float, divergence: Float, x: Double, y: Double, z: Double) : this(RegisterEntity.CHAOS_BOLT_ENTITY,world){
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

    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(1.95F,0.05F,0.0F).withDuration(80)
    private var augment: ScepterAugment = RegisterEnchantment.CHAOS_BOLT
    
    fun setAugment(aug: ScepterAugment){
        this.augment = aug
    }

    override fun passEffects(ae: AugmentEffect, level: Int) {
        super.passEffects(ae, level)
        ae.setDuration(ae.duration(level))
        ae.setAmplifier(ae.amplifier(level))
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        if (world.isClient) {
            return
        }
        val entity = owner
        if (entity is LivingEntity) {
            val entity2 = entityHitResult.entity
            if (!(entity2 is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(entity, entity2, augment))) {
                val ownerEffects = entity.statusEffects.filter { !it.isInfinite }
                val beneficialEffects = ownerEffects.filter { it.effectType.isBeneficial }
                val negativeEffects = ownerEffects.filter { !it.effectType.isBeneficial }
                val multiplier = 1f + beneficialEffects.size * 0.75f + negativeEffects.size * 1.5f
                val bl = entity2.damage(
                    entity.damageSources.indirectMagic(this,owner),
                    entityEffects.damage(0) * multiplier
                )
                if (bl) {
                    entityEffects.accept(entity, AugmentConsumer.Type.BENEFICIAL)
                    applyDamageEffects(entity as LivingEntity?, entity2)
                    if (entity2 is LivingEntity) {
                        val effects = Registries.STATUS_EFFECT.filter { !it.isBeneficial }
                        entity2.addStatusEffect(StatusEffectInstance(effects.random(),entityEffects.duration(0), entityEffects.amplifier(0)))
                        entityEffects.accept(entity2, AugmentConsumer.Type.HARMFUL)
                    }
                }
            }
        }
        discard()
    }

    override fun onMissileBlockHit(blockHitResult: BlockHitResult) {
        super.onMissileBlockHit(blockHitResult)
        /*val owner = this.owner
        if (owner !is PlayerEntity) return
        val blockPos1 = blockHitResult.blockPos.offset(blockHitResult.side)
        if ((world.gameRules.getBoolean(GameRules.DO_MOB_GRIEFING))
            && world.isAir(blockPos1)
            && CommonProtection.canPlaceBlock(world,blockPos1,owner.gameProfile,owner)) {
            world.setBlockState(blockPos1, AbstractFireBlock.getState(world, blockPos1))
        }*/
    }

    override fun getParticleType(): ParticleEffect {
        val rnd3 = world.random.nextInt(DyeColor.values().size)
        val colorInt = DyeColor.values()[rnd3].signColor
        val color = Vec3d.unpackRgb(colorInt).toVector3f()
        return DustParticleEffect(color,0.8f)
    }

    companion object{
        fun createChaosBolt(world: World, user: LivingEntity, speed: Float, div: Float, effects: AugmentEffect, level: Int, augment:ScepterAugment): ChaosBoltEntity {
            val fbe = ChaosBoltEntity(
                world, user, speed, div,
                user.x - (user.width + 0.5f) * 0.5 * MathHelper.sin(user.bodyYaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(
                    user.pitch * (Math.PI.toFloat() / 180)
                ),
                user.eyeY - 0.6 - 0.8 * MathHelper.sin(user.pitch * (Math.PI.toFloat() / 180)),
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
