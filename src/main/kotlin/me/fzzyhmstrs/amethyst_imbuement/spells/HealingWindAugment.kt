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
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class HealingWindAugment: MiscAugment(ScepterTier.THREE,11), PersistentEffectHelper.PersistentEffect{

    override val baseEffect: AugmentEffect = super.baseEffect
                                                .withDamage(1.9f,0.1f)
                                                .withAmplifier(1)
                                                .withDuration(360,40)
                                                .withRange(11.5,0.5)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE,1200,240,
            24,imbueLevel,65, LoreTier.HIGH_TIER, RegisterItem.DAZZLING_MELON_SLICE)
    }

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        val hitResult = EntityHitResult(user, Vec3d(user.x,user.getBodyY(0.5),user.z))
        val (blockPos, entityList) = RaycasterUtil.raycastEntityArea(user,hitResult,effect.range(level))
        if (entityList.isEmpty()) return false
        val bl = effect(world, user, entityList, level, effect)
        if (bl) {
            val data = AugmentPersistentEffectData(world, user, blockPos, entityList, level, effect)
            PersistentEffectHelper.setPersistentTickerNeed(
                RegisterEnchantment.HEALING_WIND,
                delay.value(level),
                effect.duration(level),
                data
            )
            if (world is ServerWorld){
                world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,user.x,user.getBodyY(0.5),user.z,100,effect.range(level),0.8,effect.range(level),0.0)
            }
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
        for (target in entityList) {
            if (!(target is PassiveEntity || target is GolemEntity || target is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(user,target,this))) continue
            if (target !is LivingEntity) continue
            if (target.health == target.maxHealth) continue
            target.heal(effect.damage(level))
            successes++
        }
        return successes > 0
    }

    override val delay: PerLvlI
        get() = PerLvlI(36,-1)

    @Suppress("SpellCheckingInspection")
    override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
        if (data !is AugmentPersistentEffectData) return
        val hitResult = EntityHitResult(data.user, Vec3d(data.user.x,data.user.getBodyY(0.5),data.user.z))
        val (_, entityList) = RaycasterUtil.raycastEntityArea(data.user,hitResult,data.effect.range(data.level))
        effect(data.world,data.user,entityList,data.level,data.effect)
        data.world.playSound(null,data.user.blockPos,soundEvent(),SoundCategory.PLAYERS,1.0f,0.8f + data.world.random.nextFloat()*0.4f)
        val world = data.world
        if (world is ServerWorld){
            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,data.user.x,data.user.getBodyY(0.5),data.user.z,100,data.effect.range(data.level),0.8,data.effect.range(data.level),0.0)
        }
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_CONDUIT_AMBIENT
    }

}
