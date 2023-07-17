package me.fzzyhmstrs.amethyst_imbuement.entity.living

import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectContainer
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.entity.Scalable
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.fzzy_core.entity_util.PlayerCreatable
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.passive.AbstractHorseEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.MathHelper
import net.minecraft.world.EntityView
import net.minecraft.world.World
import java.util.*

class ChorseEntity(entityType: EntityType<out AbstractHorseEntity>?, world: World?) : AbstractHorseEntity(entityType, world),
    ModifiableEffectEntity, PlayerCreatable, Scalable {
    companion object{

        private val SCALE = DataTracker.registerData(ChorseEntity::class.java, TrackedDataHandlerRegistry.FLOAT)
        fun createChorseBaseAttributes(): DefaultAttributeContainer.Builder{
            return createMobAttributes()
                .add(EntityAttributes.HORSE_JUMP_STRENGTH,1.0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.275)
        }
    }

    override var entityEffects: AugmentEffect = AugmentEffect()
    override var level: Int = 1
    override var modifiableEffects: ModifiableEffectContainer = ModifiableEffectContainer()
    override var processContext: ProcessContext = ProcessContext.FROM_ENTITY_CONTEXT
    override var spells: PairedAugments = PairedAugments()

    override var createdBy: UUID? = null
    override var entityOwner: LivingEntity? = null
    override var maxAge: Int = -1

    fun setChorseOwner(owner:LivingEntity?){
        this.createdBy = owner?.uuid
        this.entityOwner = owner
    }

    override fun passEffects(spells: PairedAugments, ae: AugmentEffect, level: Int) {
        super.passEffects(spells, ae, level)
        val chk = world
        if (chk is ServerWorld) {
            initialize(chk,chk.getLocalDifficulty(this.blockPos), SpawnReason.MOB_SUMMONED,null,null)
        }
    }

    override fun initDataTracker() {
        super.initDataTracker()
        dataTracker.startTracking(SCALE,1f)
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        writePlayerCreatedNbt(nbt)
        writeModifiableNbt(nbt)
        nbt.putFloat("scale_factor",getScale())
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        readPlayerCreatedNbt(world, nbt)
        readModifiableNbt(nbt)
        setScale(nbt.getFloat("scale_factor").takeIf { it > 0f } ?: 1f)
    }

    override fun getScale(): Float {
        return dataTracker.get(SCALE)
    }

    override fun setScale(scale: Float) {
        dataTracker.set(SCALE, MathHelper.clamp(scale,0.0f,20.0f))
        this.calculateDimensions()
    }

    override fun getOwnerUuid(): UUID? {
        return createdBy
    }

    override fun method_48926(): EntityView {
        return this.world
    }

    override fun getOwner(): LivingEntity? {
        return if (entityOwner != null) {
            entityOwner
        } else if (world is ServerWorld && createdBy != null) {
            val o = (world as ServerWorld).getEntity(createdBy)
            if (o != null && o is LivingEntity) {
                entityOwner = o
                o
            } else {
                null
            }
        }else {
            null
        }
    }
}