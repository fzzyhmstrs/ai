package me.fzzyhmstrs.amethyst_imbuement.entity.hamster

import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.fzzy_config.config_util.ConfigSection
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedDouble
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedFloat
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedInt
import net.minecraft.entity.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.nbt.NbtCompound
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World

open class HambieEntity: BaseHamsterEntity, SpellCastingEntity {

    constructor(entityType: EntityType<HambieEntity>, world: World): super(entityType, world)

    constructor(entityType: EntityType<HambieEntity>, world: World, ageLimit: Int, createdBy: LivingEntity? = null) : super(entityType, world, ageLimit, createdBy){
    }

    class Hambie: ConfigSection(Header.Builder().space().add("readme.entities.hambie_1").add("readme.entities.hambie_2").build()){
        var baseLifespan = ValidatedInt(2400,180000,20)
        var baseMoveSpeed = ValidatedDouble(0.22,1.0,0.01)
        var baseHealth = ValidatedDouble(12.0,40.0,1.0)
        var baseDamage = ValidatedFloat(2.5f,20.0f,0.0f)
    }

    companion object{
        fun createZambieAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, AiConfig.entities.hambie.baseHealth.get())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, AiConfig.entities.hambie.baseMoveSpeed.get())
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, AiConfig.entities.hambie.baseDamage.get().toDouble())
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
