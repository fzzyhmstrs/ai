package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.coding_util.PerLvlI
import me.fzzyhmstrs.amethyst_core.coding_util.PersistentEffectHelper
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.raycaster_util.RaycasterUtil
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentPersistentEffectData
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.FireAugment
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerFireballEntity.Companion.createFireball
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class CometStormAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier, maxLvl, *slot),
    PersistentEffectHelper.PersistentEffect,
    FireAugment {

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
            val data = AugmentPersistentEffectData(world, user, blockPos, entityList, level, effect)
            PersistentEffectHelper.setPersistentTickerNeed(
                RegisterEnchantment.COMET_STORM,
                delay.value(level),
                effect.duration(level),
                data
            )
            world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
            effect.accept(user, AugmentConsumer.Type.BENEFICIAL)
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
    override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
        if (data !is AugmentPersistentEffectData) return
        val rnd1 = data.entityList.size
        val rnd2 = data.world.random.nextInt(rnd1)
        val rnd3 = data.world.random.nextFloat()
        val entity = data.entityList[rnd2]
        val bP: BlockPos
        val bpXrnd: Int
        val bpZrnd: Int
        val range = data.effect.range(data.level)

        if (rnd3 >0.4) {
            bP = entity.blockPos
            bpXrnd = data.world.random.nextInt(5) - 2
            bpZrnd = data.world.random.nextInt(5) - 2
        } else {
            bP = data.blockPos
            bpXrnd = data.world.random.nextInt((range*2 + 1).toInt()) - range.toInt()
            bpZrnd = data.world.random.nextInt((range*2 + 1).toInt()) - range.toInt()
        }
        val rndX = bP.x + bpXrnd
        val rndZ = bP.z + bpZrnd
        val ce = createFireball(data.world, data.user, Vec3d(0.0,-5.0,0.0), Vec3d(rndX.toDouble(),(data.blockPos.y + 15).toDouble(),rndZ.toDouble()), data.effect, data.level)
        data.world.spawnEntity(ce)
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,400,50,18,imbueLevel, LoreTier.HIGH_TIER, Items.TNT)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_FIRE_AMBIENT
    }
}