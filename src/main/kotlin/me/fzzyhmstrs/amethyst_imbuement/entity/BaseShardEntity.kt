package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_core.entity_util.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World
import java.util.*
import kotlin.math.max

open class BaseShardEntity(entityType: EntityType<out BaseShardEntity?>, world: World): PersistentProjectileEntity(entityType, world), ModifiableEffectEntity {

    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(6F).withAmplifier(0)
    private val struckEntities: MutableList<UUID> = mutableListOf()
    protected var scepterAugment: ScepterAugment = RegisterEnchantment.ICE_SHARD
    
    fun setAugment(aug: ScepterAugment){
        this.scepterAugment = aug
    }

    override fun passEffects(ae: AugmentEffect, level: Int) {
        super.passEffects(ae, level)
        entityEffects.setDamage(ae.damage(level))
        entityEffects.setAmplifier(ae.amplifier(level))
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        if (world.isClient) {
            return
        }
        val entity = owner
        if (entity is LivingEntity) {
            val entity2 = entityHitResult.entity
            if (!(entity2 is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(entity, entity2, scepterAugment))){
                val bl = entity2.damage(entity.damageSources.mobProjectile(this,entity),
                    max(1f,entityEffects.damage(0) - struckEntities.size)
                )
                if (!struckEntities.contains(entity2.uuid)){
                    struckEntities.add(entity2.uuid)
                }
                if (bl) {
                    entityEffects.accept(entity, AugmentConsumer.Type.BENEFICIAL)
                    applyDamageEffects(entity as LivingEntity?, entity2)
                    if (entity2 is LivingEntity) {
                        onEntityHitSideEffects(entity2)
                        entityEffects.accept(entity2, AugmentConsumer.Type.HARMFUL)
                    }
                }
            }
        }
        if (struckEntities.size > entityEffects.amplifier(0)){
            discard()
        }
    }

    open fun onEntityHitSideEffects(entity: LivingEntity){

    }

    override fun onBlockHit(blockHitResult: BlockHitResult) {
        super.onBlockHit(blockHitResult)
        onBlockHitSideEffects(blockHitResult)
    }

    open fun onBlockHitSideEffects(blockHitResult: BlockHitResult){

    }

    override fun asItemStack(): ItemStack {
        return ItemStack.EMPTY
    }

    override fun tryPickup(player: PlayerEntity): Boolean {
        return false
    }

    override fun tick() {
        super.tick()
        if (this.age > 1200){
            discard()
        }
        if (!inGround)
            addParticles(velocity.x, velocity.y, velocity.z)
    }

    open fun particle(): ParticleEffect{
        return ParticleTypes.CRIT
    }

    private fun addParticles(x2: Double, y2: Double, z2: Double){
        if (this.isTouchingWater) {
            for (i in 0..2) {
                world.addParticle(
                    ParticleTypes.BUBBLE,
                    this.x + x2 * (world.random.nextFloat()-0.5f),
                    this.y + y2 * (world.random.nextFloat()-0.5f),
                    this.z + z2 * (world.random.nextFloat()-0.5f),
                    0.0,
                    0.0,
                    0.0
                )
            }
        } else {
            for (i in 0..2) {
                world.addParticle(
                    particle(),
                    this.x + x2 * (world.random.nextFloat()-0.5f),
                    this.y + y2 * (world.random.nextFloat()-0.5f),
                    this.z + z2 * (world.random.nextFloat()-0.5f),
                    0.0,
                    0.0,
                    0.0
                )
            }
        }
    }

}
