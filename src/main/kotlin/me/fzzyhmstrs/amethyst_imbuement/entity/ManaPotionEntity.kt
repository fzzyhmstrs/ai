package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.entity.EntityType
import net.minecraft.entity.ExperienceOrbEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.thrown.ThrownItemEntity
import net.minecraft.item.Item
import net.minecraft.potion.PotionUtil
import net.minecraft.potion.Potions
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World
import net.minecraft.world.WorldEvents

class ManaPotionEntity: ThrownItemEntity {

    constructor(entityType: EntityType<out ManaPotionEntity>, world: World): super(entityType, world)

    constructor(world: World, owner: LivingEntity): super(RegisterEntity.MANA_POTION, owner, world)

    constructor(world: World, x: Double, y: Double, z: Double): super(RegisterEntity.MANA_POTION, x, y, z, world)

    override fun getDefaultItem(): Item {
        return RegisterItem.MANA_POTION
    }

    override fun getGravity(): Float {
        return 0.07f
    }

    override fun onCollision(hitResult: HitResult?) {
        super.onCollision(hitResult)
        if (this.world is ServerWorld) {
            this.world.syncWorldEvent(
                WorldEvents.SPLASH_POTION_SPLASHED,
                this.blockPos,
                PotionUtil.getColor(Potions.WATER)
            )
            val i: Int = (3 + this.world.random.nextInt(5) + this.world.random.nextInt(5)) * 2
            ExperienceOrbEntity.spawn(this.world as ServerWorld?, this.pos, i)
            this.discard()
        }
    }

}