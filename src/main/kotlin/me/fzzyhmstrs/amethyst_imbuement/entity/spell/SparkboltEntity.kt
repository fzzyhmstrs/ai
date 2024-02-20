package me.fzzyhmstrs.amethyst_imbuement.entity.spell

import me.fzzyhmstrs.amethyst_core.entity_util.MissileEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellDamageSource
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.fzzy_core.coding_util.compat.FzzyDamage
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.RaycastContext
import net.minecraft.world.World

class SparkboltEntity(entityType: EntityType<SparkboltEntity>, world: World): MissileEntity(entityType,world) {

    constructor(world: World, owner: LivingEntity, level: Int) : this(RegisterEntity.SPARKBOLT_ENTITY,world){
        this.level = level
        this.owner = owner
        this.setPosition(
            owner.x,
            owner.eyeY - (owner.height * 0.25),
            owner.z
        )
        this.setRotation(owner.yaw, owner.pitch)
    }

    private var level: Int = 1
    override var entityEffects: AugmentEffect = AugmentEffect()
    private var augment: ScepterAugment = RegisterEnchantment.SPARKBOLT
    
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
                val bl = entity2.damage(SpellDamageSource(FzzyDamage.lightning(this),augment), entityEffects.damage(0))

                if (bl) {
                    if (entity2 is LivingEntity) {
                        entityEffects.accept(entity2, AugmentConsumer.Type.HARMFUL)
                        if (world.random.nextDouble() < entityEffects.amplifier(0) / 1000.0)
                            RegisterStatus.stun(entity2, entityEffects.duration(0))
                    }
                    applyDamageEffects(entity, entity2)
                    entityEffects.accept(entity, AugmentConsumer.Type.BENEFICIAL)
                }
                val box = this.boundingBox.expand(entityEffects.range(0))
                val entityList = world.getOtherEntities(owner, box)
                for (entity3 in entityList) {
                    if (entity3 === entity2) continue
                    if (AiConfig.entities.shouldItHitBase(entity, entity3, augment)) {
                        (owner as? LivingEntity)?.let { RegisterEnchantment.SPARKBOLT.entityTask(world,entity3, it, level.toDouble(), entityHitResult, entityEffects) }
                        (world as? ServerWorld)?.let {  beam(it,entity3) }
                        world.playSound(null,this.blockPos, SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.NEUTRAL,0.3f,2.0f + world.random.nextFloat() * 0.4f - 0.2f)
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
        if (world !is ServerWorld) return
        if (owner == null || owner !is LivingEntity) return
        val box = this.boundingBox.expand(entityEffects.range(0))
        val entities = world.getOtherEntities(owner, box)
        for (entity in entities){
            if (!AiConfig.entities.shouldItHitBase(owner as? LivingEntity, entity, augment)) continue
            (owner as? LivingEntity)?.let { RegisterEnchantment.SPARKBOLT.entityTask(world,entity, it, level.toDouble(), blockHitResult, entityEffects) }
            beam(world as ServerWorld,entity)
            world.playSound(null,this.blockPos, SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.NEUTRAL,0.3f,2.0f + world.random.nextFloat() * 0.4f - 0.2f)
        }
        world.playSound(null,this.blockPos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.NEUTRAL,0.3f,2.0f + world.random.nextFloat() * 0.4f - 0.2f)
        super.onMissileBlockHit(blockHitResult)
    }

    private fun beam(serverWorld: ServerWorld, entity: Entity){
        val startPos = this.pos.add(0.0,0.25,0.0)
        val endPos = entity.pos.add(0.0,entity.height/2.0,0.0)
        val vec = endPos.subtract(startPos).multiply(0.1)
        var pos = startPos
        for (i in 1..10){
            serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK,pos.x,pos.y,pos.z,2,vec.x,vec.y,vec.z,0.0)
            pos = pos.add(vec)
        }

    }

    override fun getParticleType(): ParticleEffect? {
        return ParticleTypes.ELECTRIC_SPARK
    }

    override fun playHitSound(world: World, pos: BlockPos) {
        world.playSound(null,pos, SoundEvents.ENTITY_PLAYER_HURT_FREEZE, SoundCategory.PLAYERS,0.4f,1.0f)
    }

}
