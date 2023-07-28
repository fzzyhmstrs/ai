package me.fzzyhmstrs.amethyst_imbuement.entity.hamster

import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.entity.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.nbt.NbtCompound
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World

open class ZambieEntity: BaseHamsterEntity, SpellCastingEntity {

    constructor(entityType: EntityType<ZambieEntity>, world: World): super(entityType, world)

    constructor(entityType: EntityType<ZambieEntity>, world: World, ageLimit: Int, createdBy: LivingEntity? = null) : super(entityType, world, ageLimit, createdBy){
    }

    companion object{
        fun createZambieAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, AiConfig.entities.zambie.baseHealth.get())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.22)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, AiConfig.entities.zambie.baseDamage.get().toDouble())
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
        setVariant(HamsterVariant.ZOMBIE)
        entityGroup = EntityGroup.UNDEAD
        return data
    }
}
