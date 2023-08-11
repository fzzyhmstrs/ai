package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectContainer
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.WitherSkullEntity
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

open class PlayerWitherSkullEntity: WitherSkullEntity, ModifiableEffectEntity {
    constructor(entityType: EntityType<out PlayerWitherSkullEntity?>, world: World): super(entityType, world)
    constructor(world: World, owner: LivingEntity, direction: Vec3d): super(RegisterEntity.PLAYER_WITHER_SKULL, world){
        this.owner = owner
        this.refreshPositionAndAngles(owner.x, owner.eyeY - 0.2, owner.z, owner.yaw, owner.pitch)
        val d = direction.length()
        if (d != 0.0){
            powerX = direction.x / d * 0.1
            powerY = direction.y / d * 0.1
            powerZ = direction.z / d * 0.1
        }
    }

    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(8.0f).withAmplifier(1).withDuration(200,600)
    override var level: Int = 1
    override var spells: PairedAugments = PairedAugments()
    override var modifiableEffects = ModifiableEffectContainer()
    override var processContext: ProcessContext = ProcessContext.FROM_ENTITY_CONTEXT

    override fun tick() {
        super.tick()
        tickTickEffects(this, owner, processContext)
    }

    override fun onCollision(hitResult: HitResult) {
        processContext.beforeRemoval()
        val type = hitResult.type
        if (type == HitResult.Type.ENTITY) {
            onEntityHit((hitResult as EntityHitResult?)!!)
            world.emitGameEvent(GameEvent.PROJECTILE_LAND, hitResult.pos, GameEvent.Emitter.of(this, null))
        } else if (type == HitResult.Type.BLOCK) {
            val blockHitResult = hitResult as BlockHitResult
            onBlockHit(blockHitResult)
            val blockPos = blockHitResult.blockPos
            world.emitGameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Emitter.of(this, world.getBlockState(blockPos)))
        }
        val entity = this.owner
        if (!world.isClient && entity is LivingEntity?) {
            spells.causeExplosion(processContext,this,entity,world,Hand.MAIN_HAND,level,entityEffects)
            discard()
        }
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        super.onEntityHit(entityHitResult)
        if (world.isClient) {
            return
        }
        val augment = spells.primary() ?: return
        val entity = entityHitResult.entity
        val entity2 = owner

        if (entity2 is LivingEntity && !(entity is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(entity2, entity, augment))) {
            onSkullEntityHit(entityHitResult, entity2)
        }
    }

    open fun onSkullEntityHit(entityHitResult: EntityHitResult, entity: LivingEntity){
        if (entity is SpellCastingEntity && !spells.empty()) {
            runEffect(ModifiableEffectEntity.DAMAGE,this,entity,processContext)
            spells.processSingleEntityHit(entityHitResult,processContext,world,this,entity,Hand.MAIN_HAND,level,entityEffects)
            if (!entityHitResult.entity.isAlive){
                runEffect(ModifiableEffectEntity.KILL,this,entity,processContext)
                spells.processOnKill(entityHitResult,processContext,world,this,entity,Hand.MAIN_HAND,level,entityEffects)
            }
        } else {
            val bl = entityHitResult.entity.damage(this.damageSources.mobProjectile(this,entity),entityEffects.damage(0))
            if (bl){
                entity.applyDamageEffects(entity,entityHitResult.entity)
            }
        }

    }

}
