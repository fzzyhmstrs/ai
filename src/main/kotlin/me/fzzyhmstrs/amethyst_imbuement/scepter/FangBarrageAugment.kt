package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentPersistentEffectData
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerFangsEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import kotlin.math.min

class FangBarrageAugment: MiscAugment(ScepterTier.THREE,6), PersistentEffectHelper.PersistentEffect {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(28,0,0)
            .withAmplifier(9,1,0)
            .withDamage(5.8F,0.2F)

    override val delay = PerLvlI(15,-1,0)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,100,50,
            26,imbueLevel,2, LoreTier.HIGH_TIER, Items.EMERALD_BLOCK)
    }

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        val entityList: MutableList<Entity> = mutableListOf()
        val d: Double
        val e: Double
        if (target != null){
            d = min(target.y, user.y)
            e = d + 2.0
            entityList.add(target)
        } else {
            d = user.y
            e = d + 2.0
        }
        val f = (user.yaw + 90) * MathHelper.PI / 180
        val successes = conjureBarrage(user,world,d,e,f, effect, level)
        val bl = successes > 0
        if (bl){
            effect.accept(user,AugmentConsumer.Type.BENEFICIAL)
        }
        val data = AugmentPersistentEffectData(world, user, BlockPos.ORIGIN, entityList, level, effect)
        PersistentEffectHelper.setPersistentTickerNeed(RegisterEnchantment.FANG_BARRAGE, delay.value(level),effect.duration(level), data)
        return bl
    }

    override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
        if (data !is AugmentPersistentEffectData) return
        val d: Double
        val e: Double
        if (data.entityList.isNotEmpty()){
            val target = data.entityList[0]
            d = min(target.y, data.user.y)
            e = d + 2.0
            data.entityList.add(target)
        } else {
            d = data.user.y
            e = d + 2.0
        }
        val f = (data.user.yaw + 90) * MathHelper.PI / 180
        conjureBarrage(data.user,data.world,d,e,f, data.effect, data.level)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_EVOKER_FANGS_ATTACK
    }

    private fun conjureBarrage(user: LivingEntity, world: World, d: Double, e: Double, f: Float, effect: AugmentEffect, level: Int): Int{
        var dd = d
        var ee = e
        var successes = 0
        val fangs = effect.amplifier(level)
        for (i in 0..fangs) {
            val g = 1.25 * (i + 1).toDouble()
            for (k in -1..1){
                val success = PlayerFangsEntity.conjureFangs(
                    world,
                    user,
                    user.x + MathHelper.cos(f + (11.0F * MathHelper.PI / 180 * k)).toDouble() * g,
                    user.z + MathHelper.sin(f + (11.0F * MathHelper.PI / 180 * k)).toDouble() * g,
                    dd,
                    ee,
                    f,
                    i,
                    effect,
                    level,
                    this
                )
                if (success != Double.NEGATIVE_INFINITY) {
                    successes++
                    dd = success
                    ee = dd + 2.0
                }
            }
        }
        return successes
    }
}
