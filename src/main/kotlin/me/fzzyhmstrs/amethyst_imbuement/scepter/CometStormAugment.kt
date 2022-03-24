package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil
import me.fzzyhmstrs.amethyst_imbuement.util.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.projectile.FireballEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class CometStormAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier, maxLvl, *slot) {

    override fun effect(world: World, target: Entity?, user: LivingEntity, level: Int, hit: HitResult?): Boolean {
        val blockPos: BlockPos
        val entityList: MutableList<Entity> = if (hit == null) {
            blockPos = user.blockPos
            RaycasterUtil.raycastEntityArea(rangeOfEffect() + 1.0 * level)
        } else if (hit.type == HitResult.Type.MISS){
            blockPos = user.blockPos
            RaycasterUtil.raycastEntityArea(rangeOfEffect() + 1.0 * level)
        } else if (hit.type == HitResult.Type.BLOCK){
            blockPos = (hit as BlockHitResult).blockPos
            RaycasterUtil.raycastEntityArea(rangeOfEffect() + 1.0 * level,hit.pos)
        } else if (hit.type == HitResult.Type.ENTITY){
            blockPos = (hit as EntityHitResult).entity.blockPos
            RaycasterUtil.raycastEntityArea(rangeOfEffect() + 1.0 * level,hit.entity.pos)
        } else {
            blockPos = user.blockPos
            RaycasterUtil.raycastEntityArea(rangeOfEffect() + 1.0 * level)
        }
        if (entityList.isEmpty() || blockPos == user.blockPos) return false
        effect(world, user, entityList, level)
        ScepterObject.setPersistentTickerNeed(world,user,entityList,level,blockPos,
            RegisterEnchantment.COMET_STORM,16-3*level,140 + 100 * level)
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
        return true
    }

    override fun effect(world: World, user: LivingEntity, entityList: MutableList<Entity>, level: Int): Boolean {
        user.isInvulnerable = true
        var successes = 0
        for (entity3 in entityList) {
            if(entity3 is Monster){
                successes++
                val ce = FireballEntity(world,user,0.0,-5.0,0.0,1)
                ce.setPosition(entity3.x,entity3.y + 15,entity3.z)
                world.spawnEntity(ce)
            }
        }
        user.isInvulnerable = false
        return successes > 0
    }

    @Suppress("SpellCheckingInspection")
    override fun persistentEffect(world: World, user: LivingEntity, blockPos: BlockPos, entityList: MutableList<Entity>, level: Int) {
        val rnd1 = entityList.size
        val rnd2 = world.random.nextInt(rnd1)
        val rnd3 = world.random.nextFloat()
        val entity = entityList[rnd2]
        val bP: BlockPos
        val bpXrnd: Int
        val bpZrnd: Int

        if (rnd3 >0.3) {
            bP = entity.blockPos
            bpXrnd = world.random.nextInt(5) - 2
            bpZrnd = world.random.nextInt(5) - 2
        } else {
            bP = blockPos
            bpXrnd = world.random.nextInt((rangeOfEffect()*2 + 1).toInt()) - rangeOfEffect().toInt()
            bpZrnd = world.random.nextInt((rangeOfEffect()*2 + 1).toInt()) - rangeOfEffect().toInt()
        }
        val rndX = bP.x + bpXrnd
        val rndZ = bP.z + bpZrnd
        val ce = FireballEntity(world,user,0.0,-5.0,0.0,1)
        ce.setPosition(rndX.toDouble(),(blockPos.y + 25).toDouble(),rndZ.toDouble())
        world.spawnEntity(ce)
    }

    override fun rangeOfEffect(): Double {
        return 6.0
    }

    override fun raycastHitRange(): Double {
        return 18.0
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.FURY,400,50,24,imbueLevel,2, Items.TNT)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_FIRE_AMBIENT
    }

}