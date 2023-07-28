package me.fzzyhmstrs.amethyst_imbuement.entity.hamster

import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import net.minecraft.entity.*
import net.minecraft.entity.damage.DamageSource
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.tag.DamageTypeTags
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World

open class HamsicleEntity: BaseHamsterEntity, SpellCastingEntity {

    constructor(entityType: EntityType<HamsicleEntity>, world: World): super(entityType, world)

    constructor(entityType: EntityType<HamsicleEntity>, world: World, ageLimit: Int, createdBy: LivingEntity? = null) : super(entityType, world, ageLimit, createdBy){
    }

    override fun initialize(
        world: ServerWorldAccess,
        difficulty: LocalDifficulty,
        spawnReason: SpawnReason,
        entityData: EntityData?,
        entityNbt: NbtCompound?
    ): EntityData? {
        val data = super.initialize(world, difficulty, spawnReason, entityData, entityNbt)
        setVariant(HamsterVariant.FROST)
        return data
    }

    override fun canFreeze(): Boolean {
        return false
    }

    override fun attackEffects(target: Entity, damageSource: DamageSource, damage: Float) {
        target.frozenTicks = 180
    }

    override fun damage(source: DamageSource, amount: Float): Boolean {
        if (source.isIn(DamageTypeTags.IS_FREEZING))
            return false
        return super.damage(source, amount)
    }

}
