package me.fzzyhmstrs.amethyst_imbuement.entity.hamster

import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.fzzy_config.config_util.ConfigSection
import me.fzzyhmstrs.fzzy_config.config_util.ReadMeText
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedDouble
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedFloat
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedInt
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

    class Hamethyst: ConfigSection(Header.Builder().space().add("readme.entities.hamethyst_1").add("readme.entities.hamethyst_2").build()){
        @ReadMeText("readme.entities.hamethyst.baseLifespan")
        var baseLifespan = ValidatedInt(6000,360000,-1)
        var baseHealth = ValidatedDouble(32.0,40.0,1.0)
        var baseDamage = ValidatedFloat(8.0f,40.0f,0.0f)
        var baseMoveSpeed = ValidatedDouble(0.28,1.0,0.01)
        var baseKnockbackResist = ValidatedDouble(0.5,1.0,0.0)
    }

    companion object{
        fun createHamethystAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, AiConfig.entities.hamethyst.baseHealth.get())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, AiConfig.entities.hamethyst.baseMoveSpeed.get())
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, AiConfig.entities.hamethyst.baseKnockbackResist.get())
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
