package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentPersistentEffectData
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.spell.PlayerFireballEntity.Companion.createFireball
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
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

class CometStormAugment: MiscAugment(ScepterTier.THREE,9), PersistentEffectHelper.PersistentEffect{

    override val baseEffect: AugmentEffect = super.baseEffect
                                                .withDamage(7.75f,0.25f)
                                                .withAmplifier(1)
                                                .withDuration(70,4)
                                                .withRange(8.25,0.25)
            

    override val delay = PerLvlI(19,-1,0)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,PerLvlI(480,-20),75,
            21,imbueLevel,10, LoreTier.HIGH_TIER, Items.TNT)
    }

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
        var successes = 0
        for (entity3 in entityList) {
            if(entity3 is Monster || entity3 is SpellCastingEntity && !AiConfig.entities.isEntityPvpTeammate(user,entity3,this)){
                val vel = entity3.pos.subtract(user.pos.add(0.0,user.standingEyeHeight.toDouble(),0.0)).normalize().multiply(4.0)
                val ce = createFireball(world, user, vel, user.eyePos.subtract(0.0,0.2,0.0), effect, level, this)
                if (world.spawnEntity(ce)){
                    successes++
                }
            }
        }
        effect.accept(toLivingEntityList(entityList),AugmentConsumer.Type.HARMFUL)
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
        val vel = Vec3d(rndX.toDouble(),(data.blockPos.y).toDouble(),rndZ.toDouble()).subtract(data.user.pos.add(0.0,data.user.standingEyeHeight.toDouble(),0.0)).normalize().multiply(4.0)
        val ce = createFireball(data.world, data.user, vel, data.user.eyePos.subtract(0.0,0.2,0.0), data.effect, data.level, this)
        data.world.spawnEntity(ce)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_FIRE_AMBIENT
    }
}
