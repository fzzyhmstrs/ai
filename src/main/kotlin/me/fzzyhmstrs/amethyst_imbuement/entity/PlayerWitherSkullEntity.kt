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
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World
import kotlin.math.sqrt

open class PlayerWitherSkullEntity: WitherSkullEntity, ModifiableEffectEntity {
    constructor(entityType: EntityType<out PlayerWitherSkullEntity?>, world: World): super(entityType, world)
    constructor(world: World, owner: LivingEntity, directionX: Double, directionY: Double, directionZ: Double): super(RegisterEntity.PLAYER_WITHER_SKULL, world){
        this.owner = owner
        this.refreshPositionAndAngles(owner.x, owner.y, owner.z, owner.yaw, owner.pitch)
        val d = sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ)
        if (d != 0.0){
            powerX = directionX / d * 0.1
            powerY = directionY / d * 0.1
            powerZ = directionZ / d * 0.1
        }
    }

    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(8.0f).withAmplifier(1).withDuration(200,600)
    override var level: Int = 1
    override var spells: PairedAugments = PairedAugments()
    override var modifiableEffects = ModifiableEffectContainer()
    override var processContext: ProcessContext = ProcessContext.EMPTY_CONTEXT

    override fun tick() {
        super.tick()
        tickTickEffects(this, owner, processContext)
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
