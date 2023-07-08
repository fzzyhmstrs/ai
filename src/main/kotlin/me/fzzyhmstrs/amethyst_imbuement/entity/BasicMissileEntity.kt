package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_core.entity.MissileEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World

open class BasicMissileEntity(entityType: EntityType<out BasicMissileEntity>, world: World): MissileEntity(entityType, world) {

    constructor(world: World, owner: LivingEntity): this(RegisterEntity.BASIC_MISSILE_ENTITY,world, owner)

    constructor(entityType: EntityType<out BasicMissileEntity?>,world: World, owner: LivingEntity) : this(entityType,world){
        this.owner = owner
        this.setPosition(
            owner.x,
            owner.eyeY - 0.4,
            owner.z
        )
        this.setRotation(owner.yaw, owner.pitch)
    }

    private var burning: Boolean = false

    fun burning(): BasicMissileEntity{
        burning = true
        return this
    }

    override fun isBurning(): Boolean {
        return burning
    }

    override fun onMissileEntityHit(entityHitResult: EntityHitResult) {
        val hit = entityHitResult.entity
        val entity = owner as? LivingEntity ?: return
        val aug = spells.primary()?:return
        if (!(hit is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(entity, hit, aug)))
            super.onMissileEntityHit(entityHitResult)
    }


}