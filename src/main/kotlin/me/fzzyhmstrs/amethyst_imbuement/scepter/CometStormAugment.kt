package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerFireballEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerFireballEntity.Companion.createFireball
import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.*
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
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class CometStormAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier, maxLvl, *slot), PersistentAugment, FireAugment {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(140,100)
            .withAmplifier(1)
            .withRange(8.0,1.0)

    override val delay = PerLvlI(16,-3,0)

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        val (blockPos, entityList) = RaycasterUtil.raycastEntityArea(user,hit,effect.range(level))
        if (entityList.isEmpty() || blockPos == user.blockPos) return false
        val bl = effect(world, user, entityList, level, effect)
        if (bl) {
            ScepterObject.setPersistentTickerNeed(
                world, user, entityList, level, blockPos,
                RegisterEnchantment.COMET_STORM, delay.value(level), effect.duration(level), effect
            )
            world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
            effect.accept(user,AugmentConsumer.Type.BENEFICIAL)
        }
        return bl
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
                val ce = createFireball(world, user, Vec3d(0.0,-5.0,0.0), entity3.pos.add(0.0,15.0,0.0), effect, level)
                if (world.spawnEntity(ce)){
                    successes++
                }
            }
        }
        effect.accept(toLivingEntityList(entityList),AugmentConsumer.Type.HARMFUL)
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
        val ce = createFireball(world, user, Vec3d(0.0,-5.0,0.0), Vec3d(rndX.toDouble(),(blockPos.y + 15).toDouble(),rndZ.toDouble()), effect, level)
        world.spawnEntity(ce)
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.FURY,400,50,18,imbueLevel,LoreTier.HIGH_TIER, Items.TNT)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_FIRE_AMBIENT
    }
}