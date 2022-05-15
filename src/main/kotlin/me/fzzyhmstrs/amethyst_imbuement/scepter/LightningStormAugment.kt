package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.Heightmap
import net.minecraft.world.World

@Suppress("SpellCheckingInspection")
class LightningStormAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier, maxLvl, *slot) {

    override fun effect(world: World, target: Entity?, user: LivingEntity, level: Int, hit: HitResult?): Boolean {
        val (_,entityList) = raycastEntityArea(user,hit,level)
        if (entityList.isEmpty()) {
            return false
        }
        val s = entityList.size
        var x = 0
        var y= 0
        var z = 0
        for(e in entityList){
            x += e.blockPos.x
            y += e.blockPos.y
            z += e.blockPos.z
        }
        val avgBlockPos = BlockPos(x/s,y/s,z/s)

        (world as ServerWorld).setWeather(0, 1200, true, true)
        if (!effect(world, user, entityList, level)) return false
        ScepterObject.setPersistentTickerNeed(world,user,entityList,level,avgBlockPos,RegisterEnchantment.LIGHTNING_STORM,21-3*level,120*level)
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
        return true
    }


    override fun effect(world: World, user: LivingEntity, entityList: MutableList<Entity>, level: Int): Boolean {
        user.isInvulnerable = true
        var successes = 0
        for (entity3 in entityList) {
            if(entity3 is Monster && world.isSkyVisible(entity3.blockPos)){
                successes++
                val le = EntityType.LIGHTNING_BOLT.create(world)
                le?.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(entity3.blockPos))
                world.spawnEntity(le)
            }
        }
        user.isInvulnerable = false
        return successes > 0
    }

    override fun persistentEffect(
        world: World,
        user: LivingEntity,
        blockPos: BlockPos,
        entityList: MutableList<Entity>,
        level: Int
    ) {
        var tries = 2
        while (tries >= 0) {
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
            val rndBlockPos = BlockPos(bP.x + bpXrnd,world.getTopY(Heightmap.Type.MOTION_BLOCKING,bP.x + bpXrnd,bP.z + bpZrnd), bP.z + bpZrnd)
            if (entity !is Monster || !world.isSkyVisible(rndBlockPos)){
                tries--
                continue
            }
            val le = EntityType.LIGHTNING_BOLT.create(world)
            le?.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(rndBlockPos))
            world.spawnEntity(le)
            break
        }
    }

    override fun rangeOfEffect(): Double {
        return 8.0
    }

    override fun raycastHitRange(): Double {
        return 16.0
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.FURY,400,50,20,imbueLevel,2, Items.COPPER_BLOCK)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ITEM_TRIDENT_THUNDER
    }

}