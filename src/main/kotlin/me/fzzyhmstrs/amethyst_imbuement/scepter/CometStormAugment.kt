package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.PerLvlI
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.util.LoreTier
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
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class CometStormAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier, maxLvl, *slot) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(140,100).withAmplifier(1).withRange(8.0,1.0,0.0)

    private val delay = PerLvlI(16,-3,0)

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        val (blockPos,entityList) = RaycasterUtil.raycastEntityArea(user,hit,effect.range(level))
        if (entityList.isEmpty() || blockPos == user.blockPos) return false
        effect(world, user, entityList, level, effect)
        ScepterObject.setPersistentTickerNeed(world,user,entityList,level,blockPos,
            RegisterEnchantment.COMET_STORM,delay.value(level),effect.duration(level), effect)
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
        user.isInvulnerable = true
        var successes = 0
        for (entity3 in entityList) {
            if(entity3 is Monster){
                successes++
                val ce = FireballEntity(world,user,0.0,-5.0,0.0,effect.amplifier(0))
                ce.setPosition(entity3.x,entity3.y + 15,entity3.z)
                world.spawnEntity(ce)
            }
        }
        user.isInvulnerable = false
        return successes > 0
    }

    @Suppress("SpellCheckingInspection")
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
        val rnd3 = world.random.nextFloat()
        val entity = entityList[rnd2]
        val bP: BlockPos
        val bpXrnd: Int
        val bpZrnd: Int
        val range = effect.range(level)

        if (rnd3 >0.4) {
            bP = entity.blockPos
            bpXrnd = world.random.nextInt(5) - 2
            bpZrnd = world.random.nextInt(5) - 2
        } else {
            bP = blockPos
            bpXrnd = world.random.nextInt((range*2 + 1).toInt()) - range.toInt()
            bpZrnd = world.random.nextInt((range*2 + 1).toInt()) - range.toInt()
        }
        val rndX = bP.x + bpXrnd
        val rndZ = bP.z + bpZrnd
        //update to a modifiable entity
        val ce = FireballEntity(world,user,0.0,-5.0,0.0,effect.amplifier(0))
        //update to a modifiable entity
                ce.setPosition(rndX.toDouble(),(blockPos.y + 15).toDouble(),rndZ.toDouble())
        world.spawnEntity(ce)
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.FURY,400,50,18,imbueLevel,LoreTier.HIGH_TIER, Items.TNT)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_FIRE_AMBIENT
    }

}