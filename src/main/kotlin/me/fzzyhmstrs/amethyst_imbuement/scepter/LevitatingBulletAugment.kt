package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerBulletEntity
import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.PerLvlI
import me.fzzyhmstrs.amethyst_imbuement.util.LoreTier
import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil.raycastEntityArea
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.projectile.ShulkerBulletEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class LevitatingBulletAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier, maxLvl, *slot) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(40,20,0).withRange(8.0,1.0,0.0).withDamage(4.0f)

    private val delay = PerLvlI(16,-2,0)

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        val blockPos: BlockPos = user.blockPos
        val (_,entityList) = raycastEntityArea(user,hit,effect.range(level))
        if (entityList.isEmpty()) {
            return false
        }
        val hostileEntityList: MutableList<Entity> = mutableListOf()
        for (entity in entityList){
            if (entity is Monster){
                hostileEntityList.add(entity)
            }
        }
        if (hostileEntityList.isEmpty()) {
            return false
        }
        if (!effect(world, user, hostileEntityList, level, effect)) return false
        ScepterObject.setPersistentTickerNeed(world,user,hostileEntityList,level,blockPos,
            RegisterEnchantment.LEVITATING_BULLET, delay.value(level),effect.duration(level),effect)
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
        return true
    }

    override fun effect(
        world: World,
        user: LivingEntity,
        entityList: MutableList<Entity>,
        level: Int,
        effect: AugmentEffect
    ): Boolean {
        val axis = getAxis(user)
        for (i in 0..min(level-1,entityList.size-1)){
            val entity = entityList[i]
            //consider custom shulker bullet entity for the modifiability
            val sbe = PlayerBulletEntity(world,user,entity,axis)
            sbe.passEffects(effect, level)
            world.spawnEntity(sbe)
        }
        return true
    }
    override fun persistentEffect(
        world: World,
        user: LivingEntity,
        blockPos: BlockPos,
        entityList: MutableList<Entity>,
        level: Int,
        effect: AugmentEffect
    ) {
        val rnd1 = entityList.size
        val rnd2 = world.random.nextInt(rnd1)
        val entity = entityList[rnd2]

        val axis = getAxis(user)
        val sbe = PlayerBulletEntity(world,user,entity,axis)
        sbe.passEffects(effect, level)
        world.spawnEntity(sbe)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_SHULKER_SHOOT
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.FURY,80,20,16,imbueLevel,LoreTier.HIGH_TIER, Items.SHULKER_SHELL)
    }

    private fun getAxis(user: LivingEntity): Direction.Axis{
        val xAmount = user.getRotationVec(1.0F).x
        val zAmount = user.getRotationVec(1.0F).z
        val maxAmount = max(abs(xAmount), abs(zAmount))
        val axis: Direction.Axis = if (maxAmount == abs(xAmount)){
            if (xAmount < 0){
                Direction.EAST.axis
            } else {
                Direction.WEST.axis
            }
        }  else {
            if (xAmount < 0){
                Direction.SOUTH.axis
            } else {
                Direction.NORTH.axis
            }
        }
        return axis
    }

}