package me.fzzyhmstrs.amethyst_imbuement.entity.hamster

import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.entity.EntityData
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.tag.DamageTypeTags
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World

open class HamethystEntity: BaseHamsterEntity, SpellCastingEntity {

    constructor(entityType: EntityType<HamethystEntity>, world: World): super(entityType, world)

    constructor(entityType: EntityType<HamethystEntity>, world: World, ageLimit: Int, createdBy: LivingEntity? = null) : super(entityType, world, ageLimit, createdBy){
    }

    companion object{
        fun createHamethystAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, AiConfig.entities.hamethyst.baseHealth.get())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, AiConfig.entities.hamethyst.baseDamage.get().toDouble())
        }
    }

    override fun initialize(
        world: ServerWorldAccess,
        difficulty: LocalDifficulty,
        spawnReason: SpawnReason,
        entityData: EntityData?,
        entityNbt: NbtCompound?
    ): EntityData? {
        val data = super.initialize(world, difficulty, spawnReason, entityData, entityNbt)
        setVariant(HamsterVariant.CRYSTAL)
        return data
    }


    override fun damage(source: DamageSource, amount: Float): Boolean {
        val actual = if (source.isIn(DamageTypeTags.BYPASSES_ARMOR)){
            amount * 0.5f
        } else {
            amount
        }

        return super.damage(source, actual)
    }

}
