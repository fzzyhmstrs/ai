package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil
import me.fzzyhmstrs.amethyst_imbuement.util.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.client.MinecraftClient
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

    override fun effect(world: World, target: Entity?, user: LivingEntity, level: Int, hit: HitResult?): Boolean {
        val blockPos: BlockPos = user.blockPos
        val entityList: MutableList<Entity> = if (hit == null) {
            RaycasterUtil.raycastEntityArea(rangeOfEffect() + 1.0 * level)
        } else if (hit.type == HitResult.Type.MISS){
            RaycasterUtil.raycastEntityArea(rangeOfEffect() + 1.0 * level)
        } else if (hit.type == HitResult.Type.BLOCK){
            RaycasterUtil.raycastEntityArea(rangeOfEffect() + 1.0 * level,(hit as BlockHitResult).pos)
        } else if (hit.type == HitResult.Type.ENTITY){
            RaycasterUtil.raycastEntityArea(rangeOfEffect() + 1.0 * level,(hit as EntityHitResult).entity.pos)
        } else {
            RaycasterUtil.raycastEntityArea(rangeOfEffect() + 1.0 * level)
        }
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
        if (!effect(world, user, hostileEntityList, level)) return false
        ScepterObject.setPersistentTickerNeed(world,user,hostileEntityList,level,blockPos,
            RegisterEnchantment.LEVITATING_BULLET,16-2*level,40+20*level)
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
        return true
    }

    override fun effect(world: World, user: LivingEntity, entityList: MutableList<Entity>, level: Int): Boolean {
        val xAmount = user.getRotationVec(MinecraftClient.getInstance().tickDelta).x
        val zAmount = user.getRotationVec(MinecraftClient.getInstance().tickDelta).z
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

        for (i in 0..min(level-1,entityList.size-1)){
            val entity = entityList[i]
            val sbe = ShulkerBulletEntity(world,user,entity,axis)
            world.spawnEntity(sbe)
        }
        return true
    }
    override fun persistentEffect(
        world: World,
        user: LivingEntity,
        blockPos: BlockPos,
        entityList: MutableList<Entity>,
        level: Int
    ){
        val rnd1 = entityList.size
        val rnd2 = world.random.nextInt(rnd1)
        val entity = entityList[rnd2]

        val xAmount = user.getRotationVec(MinecraftClient.getInstance().tickDelta).x
        val zAmount = user.getRotationVec(MinecraftClient.getInstance().tickDelta).z
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
        val sbe = ShulkerBulletEntity(world,user,entity,axis)
        world.spawnEntity(sbe)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_SHULKER_SHOOT
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.FURY,60,20,12,imbueLevel,2, Items.SHULKER_SHELL)
    }

}