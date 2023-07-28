package me.fzzyhmstrs.amethyst_imbuement.entity.hamster

import me.fzzyhmstrs.amethyst_core.augments.paired.DamageSourceBuilder
import me.fzzyhmstrs.amethyst_core.augments.paired.ExplosionBuilder
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import net.minecraft.entity.*
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Hand
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World

open class LampsterEntity: BaseHamsterEntity, SpellCastingEntity {

    constructor(entityType: EntityType<LampsterEntity>, world: World): super(entityType, world)

    constructor(entityType: EntityType<LampsterEntity>, world: World, ageLimit: Int, createdBy: LivingEntity? = null) : super(entityType, world, ageLimit, createdBy){
    }

    override fun initialize(
        world: ServerWorldAccess,
        difficulty: LocalDifficulty,
        spawnReason: SpawnReason,
        entityData: EntityData?,
        entityNbt: NbtCompound?
    ): EntityData? {
        val data = super.initialize(world, difficulty, spawnReason, entityData, entityNbt)
        setVariant(HamsterVariant.MAGMA)
        return data
    }

    override fun isFireImmune(): Boolean {
        return true
    }

    override fun attackEffects(target: Entity, damageSource: DamageSource, damage: Float) {
        target.setOnFireFor(5)
    }

    override fun remove(reason: RemovalReason) {
        if (reason.shouldDestroy()) {
            val dsb = DamageSourceBuilder(world,this,this).set(DamageTypes.IN_FIRE).add(DamageTypes.EXPLOSION)
            val eb = ExplosionBuilder(dsb,this,this.eyePos).withPower(0.5f).withCreateFire(true).withType(World.ExplosionSourceType.TNT)
            spells.causeExplosion(eb,processContext,owner,world,Hand.MAIN_HAND,level,entityEffects)
        }
        super.remove(reason)
    }

}
