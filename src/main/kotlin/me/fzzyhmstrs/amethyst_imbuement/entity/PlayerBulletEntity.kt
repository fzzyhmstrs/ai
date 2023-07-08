package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.entity.TickEffect
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ShulkerBulletEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import java.util.concurrent.ConcurrentLinkedQueue

open class PlayerBulletEntity: ShulkerBulletEntity, ModifiableEffectEntity<PlayerBulletEntity> {
    constructor(entityType: EntityType<out PlayerBulletEntity?>, world: World): super(entityType, world)
    constructor(world: World, owner: LivingEntity, target: Entity, axis: Direction.Axis): super(world, owner, target, axis)

    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(4.0f).withDuration(140)
    override var level: Int = 1
    override var spells: PairedAugments = PairedAugments()
    override val tickEffects: ConcurrentLinkedQueue<TickEffect> = ConcurrentLinkedQueue()

    override fun tickingEntity(): PlayerBulletEntity {
        return this
    }

    override fun tick() {
        super.tick()
        tickTickEffects()
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        if (world.isClient) {
            return
        }
        val entity = entityHitResult.entity
        val entity2 = owner
        val augment = spells.primary() ?: return
        if (entity2 is LivingEntity && !(entity is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(entity2, entity, augment))) {
            onBlockEntityHit(entityHitResult, entity2)
        }
    }

    open fun onBlockEntityHit(entityHitResult: EntityHitResult, entity: LivingEntity?){
        if (entity is LivingEntity && entity is SpellCastingEntity) {
            spells.processSingleEntityHit(entityHitResult,world,this,entity, Hand.MAIN_HAND,level,entityEffects)
            if (!entityHitResult.entity.isAlive){
                spells.processOnKill(entityHitResult,world,this,entity, Hand.MAIN_HAND,level,entityEffects)
            }
        }
    }

    override fun onBlockHit(blockHitResult: BlockHitResult) {
        super.onBlockHit(blockHitResult)
        if (world.isClient) {
            return
        }
        onBulletBlockHit(blockHitResult)
    }

    open fun onBulletBlockHit(blockHitResult: BlockHitResult){
        val entity = owner
        if (entity is LivingEntity && entity is SpellCastingEntity) {
            spells.processSingleBlockHit(blockHitResult,world,this,entity,Hand.MAIN_HAND,level,entityEffects)
        }
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        readModifiableNbt(nbt)
        super.readCustomDataFromNbt(nbt)
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        writeModifiableNbt(nbt)
        super.writeCustomDataToNbt(nbt)
    }

}
