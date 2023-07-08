package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.entity.TickEffect
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.projectile.WitherSkullEntity
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.Difficulty
import net.minecraft.world.World
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.sqrt

open class PlayerWitherSkullEntity: WitherSkullEntity, ModifiableEffectEntity<PlayerWitherSkullEntity> {
    constructor(entityType: EntityType<out PlayerWitherSkullEntity?>, world: World): super(entityType, world)
    constructor(world: World, owner: LivingEntity, directionX: Double, directionY: Double, directionZ: Double): super(RegisterEntity.PLAYER_WITHER_SKULL, world){
        this.owner = owner
        this.setRotation(owner.yaw, owner.pitch)
        this.refreshPositionAndAngles(owner.x, owner.y, owner.z, yaw, pitch)
        this.refreshPosition()
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
    override val tickEffects: ConcurrentLinkedQueue<TickEffect> = ConcurrentLinkedQueue()
    override var processContext: ProcessContext = ProcessContext.EMPTY

    override fun tickingEntity(): PlayerWitherSkullEntity {
        return this
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
        /*if (bl && entity is LivingEntity) {
            var i = -1
            if (world.difficulty == Difficulty.NORMAL) {
                i = 0
            } else if (world.difficulty == Difficulty.HARD) {
                i = 1
            }
            if (i > -1) {
                entity.addStatusEffect(StatusEffectInstance(StatusEffects.WITHER, entityEffects.duration(i), entityEffects.amplifier(0)), this.effectCause)
            }
        }*/
    }

    open fun onSkullEntityHit(entityHitResult: EntityHitResult, entity: LivingEntity){
        if (entity is SpellCastingEntity) {
            spells.processSingleEntityHit(entityHitResult,world,this,entity, Hand.MAIN_HAND,level,entityEffects)
            if (!entityHitResult.entity.isAlive){
                spells.processOnKill(entityHitResult,world,this,entity, Hand.MAIN_HAND,level,entityEffects)
            }
        }
    }

}
